package com.seb;

import com.hawolt.logger.Logger;
import com.seb.Login.LoginStatus;
import io.javalin.util.FileUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Mysql {

    private static Connection con;
    private static JSONObject data;
    private static long timestamp;

    static {
        try {
            data = new JSONObject(Files.readString(Path.of("mysqldata.json")));
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public static void createMysql() throws SQLException {
        String password = data.getString("password");
        String username = data.getString("username");
        con = DriverManager.getConnection("jdbc:mariadb://sebgameservers.de:3306/dnd", username, password);
        timestamp = System.currentTimeMillis() + 28800000;
    }

    public static ResultSet charDetails(String id) throws SQLException {
        checkCon();
        String statement = "SELECT * FROM characters left join class_level on class_level.level=characters.level and class_level.class=characters.class WHERE id = '"+ escapeWildcardsForMySQL(id) + "';";
        return con.prepareStatement(statement).executeQuery();
    }

    public static LoginStatus login(String username, String password) throws SQLException {
        checkCon();
        String statement = "SELECT count(*) FROM users WHERE username = '" + escapeWildcardsForMySQL(username) + "' AND password = '" + escapeWildcardsForMySQL(password) + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        int i = rs.getInt(1);
        if (i == 1) return LoginStatus.SUCCESS;
        else return LoginStatus.WRONG_DATA;
    }

    public static void addCharacter(String user, Map<String, List<String>> stringListMap) throws SQLException {
        checkCon();
        StringBuilder statement = new StringBuilder("INSERT INTO dnd.characters (owner");
        StringBuilder values = new StringBuilder(") VALUES ('" + user + "'");
        stringListMap.forEach((key, value) -> {
            if (value.contains(";") || value.contains("'") || key.contains("'") || key.contains(";")) {return;}
            if (key.contains("submit")) return;
            if (value.get(0).isEmpty()) return;
            if (key.equals("weapons") || key.equals("spells")) return;
            statement.append(", ").append(escapeWildcardsForMySQL(key));
            if (key.endsWith("ü")) {
                values.append(", '").append(1).append("'");
                return;
            }
            values.append(", '").append(escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(value.get(0)))).append("'");
        });
        statement.append(values).append(");");
        con.prepareStatement(statement.toString()).executeUpdate();
        ResultSet rs = con.prepareStatement("SELECT LAST_INSERT_ID();").executeQuery();
        rs.next();
        int id = rs.getInt(1);
        if (stringListMap.get("weapons") != null)
            stringListMap.get("weapons").forEach(value -> {
                try {
                    con.prepareStatement("INSERT INTO waffen_character (`waffe`, `character`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(value)) + "', '" + id + "');").executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        if (stringListMap.get("spells") != null)
            stringListMap.get("spells").forEach(value -> {
                try {
                    con.prepareStatement("INSERT INTO char_spells (`spell`, `character`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(value)) + "', '" + id + "');").executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public static ResultSet getCharacters(String user) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, id, class, level FROM characters WHERE owner = '" + escapeWildcardsForMySQL(user) + "';").executeQuery();
    }

    public static void addCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO regCodes (code) VALUES ('" + escapeWildcardsForMySQL(code) + "');").executeUpdate();
    }

    public static boolean codeExists(String code) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT code FROM regCodes WHERE code = '" + escapeWildcardsForMySQL(code) + "';").executeQuery();
        return rs.next();
    }

    public static ResultSet getCodes() throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM regCodes;").executeQuery();
    }

    public static void deleteCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("DELETE FROM regCodes WHERE code = '" + escapeWildcardsForMySQL(code) + "';").executeUpdate();
    }

    public static void addUser(String username, String password) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO users (username, password) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(username)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(password)) + "');").executeUpdate();
    }

    public static String getCharacterOwner(String characterId) throws SQLException {
        checkCon();
        String statement = "SELECT owner FROM characters WHERE id = '" + escapeWildcardsForMySQL(characterId) + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        return rs.getString(1);
    }

    private static void checkCon() throws SQLException {
        if (con == null || con.isClosed() || timestamp < System.currentTimeMillis()) createMysql();
    }

    public static ResultSet getWeapons() throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM waffen;").executeQuery();
    }

    public static void editCharacter(String characterId, Map<String, List<String>> stringListMap) throws SQLException {
        checkCon();
        StringBuilder statement = new StringBuilder("UPDATE dnd.characters SET ");
        AtomicInteger i = new AtomicInteger();
        ArrayList<String> übungslist = new ArrayList<>();
        AtomicBoolean fullEdit = new AtomicBoolean(false);
        übungslist.add("strretü");
        übungslist.add("gesretü");
        übungslist.add("konretü");
        übungslist.add("intretü");
        übungslist.add("weisretü");
        übungslist.add("charetü");
        übungslist.add("akrobatikü");
        übungslist.add("arkaneü");
        übungslist.add("athletikü");
        übungslist.add("auftretenü");
        übungslist.add("einschüchternü");
        übungslist.add("fingerfertigkeitü");
        übungslist.add("geschichteü");
        übungslist.add("heimlichkeitü");
        übungslist.add("medizinü");
        übungslist.add("tiereü");
        übungslist.add("motivü");
        übungslist.add("nachforschungenü");
        übungslist.add("naturü");
        übungslist.add("religionü");
        übungslist.add("täuschenü");
        übungslist.add("überzeugenü");
        übungslist.add("wahrnehmungü");
        übungslist.add("überlebenü");
        stringListMap.forEach((key, value) -> {
            if (value.contains(";") || value.contains("'") || key.contains("'") || key.contains(";")) {return;}
            if (key.contains("submit")) return;
            if (value.get(0).isEmpty()) return;
            if (key.contains("weapon")) return;
            if (key.contains("spells")) return;
            if (key.equals("tab")) return;
            if (key.equals("editmode")) {
                fullEdit.set(true);
                return;
            }
            if (value.get(0).equals("null")) return;
            String s = value.get(0);
            if (key.endsWith("ü")) {
                übungslist.remove(key);
                s = "1";
            }
            s = escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(s));
            statement.append(escapeWildcardsForMySQL(key)).append(" = '").append(s).append("', ");
            i.getAndIncrement();
        });
        if (fullEdit.get()) {
            übungslist.forEach(value -> statement.append(value).append(" = '").append("0").append("', "));
            con.prepareStatement("DELETE FROM waffen_character WHERE `character` = " + escapeWildcardsForMySQL(characterId)).executeUpdate();
            if (stringListMap.get("weapons") != null)
                stringListMap.get("weapons").forEach(value -> {
                    try {
                        con.prepareStatement("INSERT INTO waffen_character (`waffe`, `character`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(value)) + "', '" + escapeWildcardsForMySQL(characterId) + "');").executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            con.prepareStatement("DELETE FROM char_spells WHERE `character` = " + escapeWildcardsForMySQL(characterId)).executeUpdate();
            if (stringListMap.get("spells") != null)
                stringListMap.get("spells").forEach(value -> {
                    try {
                        con.prepareStatement("INSERT INTO char_spells (`spell`, `character`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(value)) + "', '" + escapeWildcardsForMySQL(characterId) + "');").executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        if (i.get() == 0) return;
        statement.delete(statement.length() - 2, statement.length());
        statement.append(" WHERE id = '").append(escapeWildcardsForMySQL(characterId)).append("';");
        con.prepareStatement(statement.toString()).executeUpdate();
    }

    public static ResultSet getCharWeapons(String characterId) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, finesse, damage, damagetype, spell FROM waffen_character INNER JOIN waffen ON waffen_character.waffe = waffen.name  WHERE `character` = '" + escapeWildcardsForMySQL(characterId) + "';").executeQuery();
    }

    public static ResultSet getCharSpells(String characterId, String level) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, requirements, level, description from char_spells inner join spells on char_spells.spell = spells.id WHERE `character` = '" + escapeWildcardsForMySQL(characterId) + "' AND `level` = '" + escapeWildcardsForMySQL(level) + "';").executeQuery();
    }

    public static ResultSet getCharSpells(String characterId) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, requirements, level, description from char_spells inner join spells on char_spells.spell = spells.id WHERE `character` = '" + escapeWildcardsForMySQL(characterId) + "' order by 'level' asc ;").executeQuery();
    }

    public static String getClassSpellAttribute(String className) throws SQLException {
        String statement = "Select spellType from classes where `name` = '" + escapeWildcardsForMySQL(className) + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static boolean getCharHasSpells(String characterId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("Select count(spell) from char_spells where `character` = '" + escapeWildcardsForMySQL(characterId) + "';").executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public static boolean getCharHasSpellLevel(String characterId, String level) throws SQLException {
        ResultSet rs = getCharSpells(characterId, level);
        return rs.next();
    }

    public static ResultSet getSpells() throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM spells;").executeQuery();
    }

    public static ResultSet getSpellSlots(String level, String klasse) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM class_level where `level` = '" + escapeWildcardsForMySQL(level) + "' and `class` = '" + escapeWildcardsForMySQL(klasse) + "';").executeQuery();
    }

    public static void addSpell(String name, String requirements, String level, String description) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO spells (`name`, `requirements`, `level`, `description`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(name)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(requirements)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(level)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(description)) + "');").executeUpdate();
    }

    public static void addWeapon(String name, String finesse, String damage, String damagetype, String spell) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO waffen (`name`, `finesse`, `damage`, `damagetype`, `spell`) VALUES ('" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(name)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(finesse)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(damage)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(damagetype)) + "', '" + escapeWildcardsForMySQL(StringEscapeUtils.escapeHtml4(spell)) + "');").executeUpdate();
    }

    public static void deleteChar(String id) throws SQLException {
        checkCon();
        String statement = "DELETE FROM char_spells WHERE `character` = '" + escapeWildcardsForMySQL(id) + "';";
        con.prepareStatement(statement).executeUpdate();
        statement = "DELETE FROM waffen_character WHERE `character` = '" + escapeWildcardsForMySQL(id) + "';";
        con.prepareStatement(statement).executeUpdate();
        statement = "DELETE FROM characters WHERE `id` = '" + escapeWildcardsForMySQL(id) + "';";
        con.prepareStatement(statement).executeUpdate();
    }

    public static boolean userHasCustomViews(String user) throws SQLException {
        checkCon();
        String statement = "select count(view) from custom_views where `user` = '" + escapeWildcardsForMySQL(user) + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        return rs.getInt(1) != 0;
    }

    public static void createCustomView(String user, String viewname) throws SQLException {
        checkCon();
        String statement = "INSERT INTO custom_views (`user`, `view`, `tabname`) VALUES ('" + escapeWildcardsForMySQL(user) + "', '" + FileUtil.readFile("html/defaultView.html") + "', '" + escapeWildcardsForMySQL(viewname) + "');";
        con.prepareStatement(statement).executeUpdate();
    }

    public static ResultSet getCustomViews(String user) throws SQLException {
        checkCon();
        String statement = "SELECT view, tabname FROM custom_views where `user` = '" + escapeWildcardsForMySQL(user) + "';";
        return con.prepareStatement(statement).executeQuery();
    }

    public static boolean userHasCustomView(String user, String view) throws SQLException {
        checkCon();
        String statement = "SELECT count(view) from custom_views where `user` = '" + escapeWildcardsForMySQL(user) + "' and `tabname` = '" + escapeWildcardsForMySQL(view) + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        return rs.getInt(1) != 0;
    }

    private static String escapeStringForMySQL(String s) {
        return s.replace("\\", "\\\\")
                .replace("\b","\\b")
                .replace("\n","\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace(new String(Character.toChars(26)), "\\Z")
                .replace(new String(Character.toChars(0)), "\\0")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
    }

    private static String escapeWildcardsForMySQL(String s) {
        return escapeStringForMySQL(s)
                .replace("%", "\\%")
                .replace("_","\\_");
    }

    public static String unescape(String s) {
        return s.replace("\\\\", "\\")
                .replace("\\b","\b")
                .replace("\\n","\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\Z", new String(Character.toChars(26)))
                .replace("\\0", new String(Character.toChars(0)))
                .replace("\\'", "'")
                .replace("\\\"", "\"")
                .replace("\\%", "%")
                .replace("\\_","_")
                .replace("&nbsp;", " ");
    }

    public static void addShare(String charId) throws SQLException {
        checkCon();
        if (charHasShare(charId)) {
            return;
        }
        String uuid = UUID.randomUUID().toString();
        while (hasUUID(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        con.prepareStatement("INSERT INTO shares(`UUID`, `character`) VALUES ('" + escapeWildcardsForMySQL(uuid) + "', '" + escapeWildcardsForMySQL(charId) + "');").executeUpdate();
    }

    private static boolean hasUUID(String uuid) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT count(UUID) from shares where `UUID` = '" + escapeWildcardsForMySQL(uuid) + "';").executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public static ResultSet getShareChar(String uuid) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT `character` FROM shares WHERE `UUID` = '" + escapeWildcardsForMySQL(uuid) + "';").executeQuery();
        if (rs.next()) {
            return charDetails(rs.getString("character"));
        } else return null;
    }

    public static boolean charHasShare(String charId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT COUNT(UUID) from shares where `character` = '" + escapeWildcardsForMySQL(charId) + "';").executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public static void deleteShare(String charid) throws SQLException {
        checkCon();
        con.prepareStatement("DELETE from shares where `character` = '" + escapeWildcardsForMySQL(charid) + "';").executeUpdate();
    }

    public static ResultSet getShare(String charId) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT UUID FROM shares where `character` = '" + escapeWildcardsForMySQL(charId) + "';").executeQuery();
    }
}
