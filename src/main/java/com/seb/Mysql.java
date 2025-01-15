package com.seb;

import com.hawolt.logger.Logger;
import com.seb.Login.LoginStatus;
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
import java.util.concurrent.atomic.AtomicInteger;

public class Mysql {

    private static Connection con;
    private static JSONObject data;

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
    }

    public static ResultSet charDetails(String id) throws SQLException {
        checkCon();
        String statement = "SELECT * FROM characters left join class_level on class_level.level=characters.level and class_level.class=characters.class WHERE id = '"+ id + "';";
        return con.prepareStatement(statement).executeQuery();
    }

    public static LoginStatus login(String username, String password) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT count(*) FROM users WHERE username = '" + username + "' AND password = '" + password + "';").executeQuery();
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
            statement.append(", ").append(key);
            if (key.endsWith("ü")) {
                values.append(", '").append(1).append("'");
                return;
            }
            values.append(", '").append(value.get(0)).append("'");
        });
        statement.append(values.toString()).append(");");
        Logger.error(statement.toString());
        con.prepareStatement(statement.toString()).executeUpdate();
    }

    public static ResultSet getCharacters(String user) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, id, class, level FROM characters WHERE owner = '" + user + "';").executeQuery();
    }

    public static void addCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO regCodes (code) VALUES ('" + code + "');").executeUpdate();
    }

    public static boolean codeExists(String code) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT code FROM regCodes WHERE code = '" + code + "';").executeQuery();
        return rs.next();
    }

    public static ResultSet getCodes() throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT * FROM regCodes;").executeQuery();
    }

    public static void deleteCode(String code) throws SQLException {
        checkCon();
        con.prepareStatement("DELETE FROM regCodes WHERE code = '" + code + "';").executeUpdate();
    }

    public static void addUser(String username, String password) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "');").executeUpdate();
    }

    public static String getCharacterOwner(String characterId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("SELECT owner FROM characters WHERE id = '" + characterId + "';").executeQuery();
        rs.next();
        return rs.getString(1);
    }

    private static void checkCon() throws SQLException {
        if (con == null || con.isClosed()) createMysql();
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
            if (value.get(0).equals("null")) return;
            String s = value.get(0);
            if (key.endsWith("ü")) {
                übungslist.remove(key);
                s = "1";
            }
            statement.append(key).append(" = '").append(s).append("', ");
            i.getAndIncrement();
        });
        übungslist.forEach(value -> {
            statement.append(value).append(" = '").append("0").append("', ");
        });
        if (i.get() == 0) return;
        statement.delete(statement.length() - 2, statement.length());
        statement.append(" WHERE id = '" + characterId + "';");
        con.prepareStatement(statement.toString()).executeUpdate();
        con.prepareStatement("DELETE FROM waffen_character WHERE `character` = " + characterId).executeUpdate();
        if (stringListMap.get("weapons") != null)
            stringListMap.get("weapons").forEach(value -> {
                try {
                    con.prepareStatement("INSERT INTO waffen_character (`waffe`, `character`) VALUES ('" + value + "', '" + characterId + "');").executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        con.prepareStatement("DELETE FROM char_spells WHERE `character` = " + characterId).executeUpdate();
        if (stringListMap.get("spells") != null)
            stringListMap.get("spells").forEach(value -> {
                try {
                    con.prepareStatement("INSERT INTO char_spells (`spell`, `character`) VALUES ('" + value + "', '" + characterId + "');").executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public static ResultSet getCharWeapons(String characterId) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, finesse, damage, damagetype, spell FROM waffen_character INNER JOIN waffen ON waffen_character.waffe = waffen.name  WHERE `character` = '" + characterId + "';").executeQuery();
    }

    public static ResultSet getCharSpells(String characterId, String level) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, requirements, level, description from char_spells inner join spells on char_spells.spell = spells.id WHERE `character` = '" + characterId + "' AND `level` = '" + level + "';").executeQuery();
    }

    public static ResultSet getCharSpells(String characterId) throws SQLException {
        checkCon();
        return con.prepareStatement("SELECT name, requirements, level, description from char_spells inner join spells on char_spells.spell = spells.id WHERE `character` = '" + characterId + "' order by 'level' asc ;").executeQuery();
    }

    public static String getClassSpellAttribute(String className) throws SQLException {
        String statement = "Select spellType from classes where `name` = '" + className + "';";
        ResultSet rs = con.prepareStatement(statement).executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public static boolean getCharHasSpells(String characterId) throws SQLException {
        checkCon();
        ResultSet rs = con.prepareStatement("Select count(spell) from char_spells where `character` = '" + characterId + "';").executeQuery();
        rs.next();
        if(rs.getInt(1) > 0) return true;
        return false;
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
        return con.prepareStatement("SELECT * FROM class_level where `level` = '" + level + "' and `class` = '" + klasse + "';").executeQuery();
    }

    public static void addSpell(String name, String requirements, String level, String description) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO spells (`name`, `requirements`, `level`, `description`) VALUES ('" + name + "', '" + requirements + "', '" + level + "', '" + description + "');").executeUpdate();
    }

    public static void addWeapon(String name, String finesse, String damage, String damagetype, String spell) throws SQLException {
        checkCon();
        con.prepareStatement("INSERT INTO waffen (`name`, `finesse`, `damage`, `damagetype`, `spell`) VALUES ('" + name + "', '" + finesse + "', '" + damage + "', '" + damagetype + "', '" + spell + "');").executeUpdate();
    }

    public static void deleteChar(String id) throws SQLException {
        checkCon();
        String statement = "DELETE FROM char_spells WHERE `character` = '" + id + "';";
        con.prepareStatement(statement).executeUpdate();
        statement = "DELETE FROM waffen_character WHERE `character` = '" + id + "';";
        con.prepareStatement(statement).executeUpdate();
        statement = "DELETE FROM characters WHERE `id` = '" + id + "';";
        con.prepareStatement(statement).executeUpdate();
    }
}
