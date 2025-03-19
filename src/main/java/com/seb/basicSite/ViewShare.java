package com.seb.basicSite;

import com.seb.Mysql;
import com.seb.abs.JavalinPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ViewShare extends JavalinPage {
    public ViewShare(Context ctx) throws SQLException {
        super(ctx);
        String html = FileUtil.readFile("html/shareView.html");

        int str = 0, ges = 0, inte = 0, kon = 0, weis = 0, cha = 0, übungsbonus = 2;
        boolean[] retÜbung = new boolean[6];
        int retIt = 0, skillIt = 0;
        boolean[] skillÜbung = new boolean[18];
        ResultSet rs = Mysql.getShareChar(ctx.pathParam("id"));
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.next();
        String id = "";
        String charClass = "";
        String classAttribute = "";
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (rsmd.getColumnName(i).equals("story")) {
                if (rs.getString(i) == null) {
                    html = html.replaceFirst("\\$STORY", "");
                } else {
                    String story = StringEscapeUtils.unescapeHtml4(rs.getString(i));
                    story = Mysql.unescape(story);
                    html = html.replaceFirst("\\$STORY", story);
                }
                continue;
            }
            if (rsmd.getColumnName(i).equals("übungsbonus")) {
                übungsbonus = rs.getInt(i);
            }
            if (rsmd.getColumnName(i).endsWith("ü")) {
                if (rsmd.getColumnName(i).contains("ret") && !rsmd.getColumnName(i).contains("auftreten")) {
                    retÜbung[retIt] = rs.getBoolean(i);
                    retIt += 1;
                } else {
                    skillÜbung[skillIt] = rs.getBoolean(i);
                    skillIt += 1;
                }
            }
            if (rsmd.getColumnName(i).equals("id")) {
                id = rs.getString(i);
                continue;
            }
            if (rs.getString(i) == null) {
                html = html.replaceAll("\\$" + rsmd.getColumnName(i).toUpperCase() + "\\s*\\<", "<");
                switch (rsmd.getColumnName(i)) {
                    case "class" -> html = html.replace("$CLASS", "");
                    case "str" -> html = html.replace("$STRM", "");
                    case "ges" -> html = html.replace("$GESM", "");
                    case "weis" -> html = html.replace("$WEISM", "");
                    case "cha" -> html = html.replace("$CHAM", "");
                    case "kon" -> html = html.replace("$KONM", "");
                    case "intelligenz" -> html = html.replace("$INTM", "");
                }
                continue;
            }
            if (rsmd.getColumnName(i).equals("class")) {
                charClass = rs.getString(i);
            }

            if (rsmd.getColumnName(i).equals("name")) {
                html = html.replaceFirst("\\$" + rsmd.getColumnName(i).toUpperCase(), rs.getString(i));
            }
            html = html.replaceFirst("\\$" + rsmd.getColumnName(i).toUpperCase(), rs.getString(i));
            switch (rsmd.getColumnName(i)) {
                case "str": {
                    str = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$STRM", String.valueOf(str));

                    break;
                }
                case "ges": {
                    ges = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$GESM", String.valueOf(ges));
                    break;
                }
                case "intelligenz": {
                    inte = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$INTM", String.valueOf(inte));
                    break;
                }
                case "kon": {
                    kon = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$KONM", String.valueOf(kon));
                    break;
                }
                case "weis": {
                    weis = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$WEISM", String.valueOf(weis));
                    break;
                }
                case "cha": {
                    cha = (int) Math.floor(((rs.getInt(i) - 10) / 2));
                    html = html.replaceFirst("\\$CHAM", String.valueOf(cha));
                    break;
                }
            }
        }
        if (!charClass.isEmpty()) classAttribute = Mysql.getClassSpellAttribute(charClass);
        ResultSet weapons = Mysql.getCharWeapons(id);
        StringBuilder waffen = new StringBuilder();
        while (weapons.next()) {
            if (weapons.getInt("spell") == 0)
                waffen.append("<tr><td>" + weapons.getString(1) + "</td><td>" + (weapons.getString(2).equals("1") ? String.valueOf(ges + übungsbonus) : String.valueOf(str + übungsbonus)) + "</td><td>" + weapons.getString(3) + "+" + (weapons.getString(2).equals("1") ? ges : str) + " " + weapons.getString(4) + "</td></tr>");
            else
                waffen.append("<tr><td>" + weapons.getString(1) + "</td><td></td><td>" + weapons.getString(3) + " " + weapons.getString(4) + "</td></tr>");
        }
        html = html.replace("$STRRET", retÜbung[0] ? String.valueOf(str + übungsbonus) : String.valueOf(str))
                .replace("$GESRET", retÜbung[1] ? String.valueOf(ges + übungsbonus) : String.valueOf(ges))
                .replace("$KONRET", retÜbung[2] ? String.valueOf(kon + übungsbonus) : String.valueOf(kon))
                .replace("$INTRET", retÜbung[3] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$WEISRET", retÜbung[4] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$CHARET", retÜbung[5] ? String.valueOf(cha + übungsbonus) : String.valueOf(cha))
                .replace("$AKROBATIK", skillÜbung[0] ? String.valueOf(ges + übungsbonus) : String.valueOf(ges))
                .replace("$ARKANE", skillÜbung[1] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$ATHLETIK", skillÜbung[2] ? String.valueOf(str + übungsbonus) : String.valueOf(str))
                .replace("$AUFTRETEN", skillÜbung[3] ? String.valueOf(cha + übungsbonus) : String.valueOf(cha))
                .replace("$EINSCH", skillÜbung[4] ? String.valueOf(cha + übungsbonus) : String.valueOf(cha))
                .replace("$FF", skillÜbung[5] ? String.valueOf(ges + übungsbonus) : String.valueOf(ges))
                .replace("$GESCHICHTE", skillÜbung[6] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$HEIMLICH", skillÜbung[7] ? String.valueOf(ges + übungsbonus) : String.valueOf(ges))
                .replace("$MEDIZIN", skillÜbung[8] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$TIERE", skillÜbung[9] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$MOTIV", skillÜbung[10] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$NACH", skillÜbung[11] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$NATUR", skillÜbung[12] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$RELI", skillÜbung[13] ? String.valueOf(inte + übungsbonus) : String.valueOf(inte))
                .replace("$TÄUSCHEN", skillÜbung[14] ? String.valueOf(cha + übungsbonus) : String.valueOf(cha))
                .replace("$ÜBERZEUGEN", skillÜbung[15] ? String.valueOf(cha + übungsbonus) : String.valueOf(cha))
                .replace("$WAHRNEHMUNG", skillÜbung[16] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$ÜBERLEBEN", skillÜbung[0] ? String.valueOf(weis + übungsbonus) : String.valueOf(weis))
                .replace("$PASSIVE", skillÜbung[16] ? String.valueOf(weis + übungsbonus + 10) : String.valueOf(weis + 10))
                .replace("$ID", id)
                .replace("$WAFFEN", waffen.toString())
                .replace("Erfolge [0]", "Erfolge [0][0][0]")
                .replace("Fehlschläge [0]", "Fehlschläge [0][0][0]")
                .replace("Erfolge [1]", "Erfolge [1][0][0]")
                .replace("Fehlschläge [1]", "Fehlschläge [1][0][0]")
                .replace("Erfolge [2]", "Erfolge [1][1][0]")
                .replace("Fehlschläge [2]", "Fehlschläge [1][1][0]")
                .replace("Erfolge [3]", "Erfolge [1][1][1]")
                .replace("Fehlschläge [3]", "Fehlschläge [1][1][1]")
                .replace("[0]", "<input type=\"checkbox\" disabled/>")
                .replace("[1]", "<input type=\"checkbox\" disabled checked/>")
                //.replaceFirst("\\$CLASS", charClass)
                .replaceFirst("\\$CLASSSPELL", classAttribute)
        ;
        switch (classAttribute) {
            case "char" : {
                html = html.replaceFirst("\\$SPELLSAVE", String.valueOf(cha + übungsbonus + 8)).replaceFirst("\\$SPELLBONUS", String.valueOf(cha + übungsbonus));
                break;
            }
            case "weis" : {
                html = html.replaceFirst("\\$SPELLSAVE", String.valueOf(weis + übungsbonus + 8)).replaceFirst("\\$SPELLBONUS", String.valueOf(weis + übungsbonus));
                break;
            }
            case "inte" : {
                html = html.replaceFirst("\\$SPELLSAVE", String.valueOf(inte + übungsbonus + 8)).replaceFirst("\\$SPELLBONUS", String.valueOf(inte + übungsbonus));
                break;
            }
        }
        if (!Mysql.getCharHasSpells(id)) html = html.replace(" id=\"spellTable\"", " id=\"spellTable\" style=\"display: none\"");
        else {
            for (int i = 0; i < 10; i++) {
                if (Mysql.getCharHasSpellLevel(id, String.valueOf(i))) {
                    html = html.replaceFirst("spell" + i + "\" style=\"display: none\"", "spell" + i + "\"");
                    ResultSet resultSet = Mysql.getCharSpells(id, String.valueOf(i));
                    StringBuilder spells = new StringBuilder();
                    while (resultSet.next()) {
                        spells.append("<div class=\"row\"><div class=\"col\">" + resultSet.getString("name") + "</div></div>");
                    }
                    html = html.replace("$SPELLLIST" + i, spells.toString());
                }

            }
        }
        ctx.html(html);
    }
}
