package com.seb.character;

import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class CharView extends JavalinAuthPage {
    
    String id;
    
    public CharView(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        id = ctx.pathParam("id");
        String s = FileUtil.readFile("html/viewChar.html");
        if (Mysql.charHasShare(id)) {
            ResultSet rs = Mysql.getShare(id);
            rs.next();
            s = s.replace("document.getElementById(\"shareForm\").setAttribute(\"action\", \"/share/$ID?tab=\" + tabName);", "document.getElementById(\"shareForm\").setAttribute(\"action\", \"/delshare/$ID?tab=\" + tabName);")
                    .replace("<form id=\"shareForm\" name=\"shareForm\" action=\"/share/$ID\" method=\"post\">", "<form id=\"shareForm\" name=\"shareForm\" action=\"/delshare/$ID\" method=\"post\">")
                    .replace("<button type=\"submit\" form=\"shareForm\" name=\"share\" value=\"Share\" class=\"submit\">Share</button>", "<button type=\"submit\" form=\"shareForm\" name=\"share\" value=\"Share\" class=\"submit\">Delete Share</button>");
            URL path = Main.class.getResource("Main.class");
            if (path != null && path.toString().startsWith("file"))
                s = s.replace("$SHARELINK", "http://" + ctx.host() + "/share/" + rs.getString(1));
            else s = s.replace("$SHARELINK", "https://" + ctx.host() + "/share/" + rs.getString(1));
        } else s = s.replace("$SHARELINK", "");
        String first = firstDiv(s.substring(0, s.indexOf("<div id=\"Play-View")));


        String second = secondDiv(s.substring(s.indexOf("<div id=\"Play-View\""), s.indexOf("\n" +
                "<button onclick=\"window.location = '/'\" class=\"scheme\">Back</button>")));
        String footer = s.substring(s.indexOf("\n" +
                "<button onclick=\"window.location = '/'\" class=\"scheme\">Back</button>")).replace("$ID", id);
        StringBuilder others = new StringBuilder();
        StringBuilder tabs = new StringBuilder();
        if (Mysql.userHasCustomViews(getUser())) {
            ResultSet rs = Mysql.getCustomViews(getUser());
            while (rs.next()) {
                others.append(secondDiv(rs.getString(1).replace("$TABNAME", rs.getString(2))));
                tabs.append("\n" + "    <button onclick=\"openTab(event, '").append(rs.getString(2)).append("')\" class=\"tablinks scheme\">").append(rs.getString(2)).append("</button>");
            }
        }
        String html = first + second + footer;
        html = html.replace("<!-- CUSTOM VIEWS-->", others.toString());
        html = html.replace("<!-- CUSTOM TABS -->", tabs.toString());
        if (ctx.queryParam("tab") != null) {
            String tab = ctx.queryParam("tab");
            html = html.replace("<button onclick=\"openTab(event, 'Default-View')\" id=\"defaultOpen\" class=\"tablinks scheme\">Default-View</button>", "<button onclick=\"openTab(event, 'Default-View')\" class=\"tablinks scheme\">Default-View</button>");
            html = html.replace("<button onclick=\"openTab(event, '" + StringEscapeUtils.unescapeHtml4(tab) + "')\" class=\"tablinks scheme\">" + StringEscapeUtils.unescapeHtml4(tab) + "</button>", "<button onclick=\"openTab(event, '" + StringEscapeUtils.unescapeHtml4(tab) + "')\" id=\"defaultOpen\" class=\"tablinks scheme\">" + StringEscapeUtils.unescapeHtml4(tab) + "</button>");
        }
        ctx.html(html);
    }
    
    private String firstDiv(String html) throws SQLException {
        int str = 0, ges = 0, inte = 0, kon = 0, weis = 0, cha = 0, übungsbonus = 2;
        boolean[] retÜbung = new boolean[6];
        int retIt = 0, skillIt = 0;
        boolean[] skillÜbung = new boolean[18];
        ResultSet rs = Mysql.charDetails(id);
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.next();
        String charClass = "";
        String classAttribute = "";
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
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
                .replace("Erfolge</span>[0]", "Erfolge</span>[0][0][0]")
                .replace("Fehlschläge</span>[0]", "Fehlschläge</span>[0][0][0]")
                .replace("Erfolge</span>[1]", "Erfolge</span>[1][0][0]")
                .replace("Fehlschläge</span>[1]", "Fehlschläge</span>[1][0][0]")
                .replace("Erfolge</span>[2]", "Erfolge</span>[1][1][0]")
                .replace("Fehlschläge</span>[2]", "Fehlschläge</span>[1][1][0]")
                .replace("Erfolge</span>[3]", "Erfolge</span>[1][1][1]")
                .replace("Fehlschläge</span>[3]", "Fehlschläge</span>[1][1][1]")
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
                        spells.append("<tr><td>" + resultSet.getString("name") + "</td></tr>");
                    }
                    html = html.replace("$SPELLLIST" + i, spells.toString());
                }

            }
        }
        return html;
    }
    
    private String secondDiv(String html) throws SQLException {
        int str = 0, ges = 0, inte = 0, kon = 0, weis = 0, cha = 0, übungsbonus = 2;
        boolean[] retÜbung = new boolean[6];
        int retIt = 0, skillIt = 0;
        boolean[] skillÜbung = new boolean[18];
        ResultSet rs = Mysql.charDetails(id);
        ResultSetMetaData rsmd = rs.getMetaData();
        rs.next();
        String charClass = "";
        String classAttribute = "";
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
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
            if (rsmd.getColumnName(i).equals("story")) {
                if (rs.getString(i) == null) {
                    html = html.replaceFirst("\\$STORY", "");
                } else
                    html = html.replaceFirst("\\$STORY", StringEscapeUtils.unescapeHtml4(rs.getString(i)));
                continue;
            }
            if (rsmd.getColumnName(i).equals("id")) continue;
            if (rs.getString(i) == null) {
                html = html.replaceFirst("\\$" + rsmd.getColumnName(i).toUpperCase(), "");
                switch (rsmd.getColumnName(i)) {
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
                .replace("Erfolge</span>[0]", "Erfolge</span>[0][0][0]")
                .replace("Fehlschläge</span>[0]", "Fehlschläge</span>[0][0][0]")
                .replace("Erfolge</span>[1]", "Erfolge</span>[1][0][0]")
                .replace("Fehlschläge</span>[1]", "Fehlschläge</span>[1][0][0]")
                .replace("Erfolge</span>[2]", "Erfolge</span>[1][1][0]")
                .replace("Fehlschläge</span>[2]", "Fehlschläge</span>[1][1][0]")
                .replace("Erfolge</span>[3]", "Erfolge</span>[1][1][1]")
                .replace("Fehlschläge</span>[3]", "Fehlschläge</span>[1][1][1]")
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
                        spells.append("<tr><td>").append(resultSet.getString("name")).append("</td></tr>");
                    }
                    html = html.replace("$SPELLLIST" + i, spells.toString());
                }

            }
        }
        ResultSet charDetails = Mysql.charDetails(id);
        ResultSetMetaData charMeta = charDetails.getMetaData();
        charDetails.next();
        for (int i = 1; i <= charMeta.getColumnCount(); i++) {
            String value = charDetails.getString(i);
            if (charMeta.getColumnName(i).equals("id")) {
                html = html.replace("$ID", value);
            }
            if (charDetails.getString(i) == null) value = "";
            if (charMeta.getColumnName(i).endsWith("ü")) {
                if (charDetails.getInt(i) == 1) {
                    html = html.replaceFirst("name=\"" + charMeta.getColumnName(i) + "\"", "name=\"" + charMeta.getColumnName(i) + "\"" + " checked");
                    continue;
                }
            }
            html = html.replaceFirst("name=\"" + charMeta.getColumnName(i) + "\"", "name=\"" + charMeta.getColumnName(i) + "\"" + " value=\"" + value + "\"");
        }
        weapons = Mysql.getWeapons();
        StringBuilder options = new StringBuilder();
        ArrayList<String> weaponList = new ArrayList<>();
        ResultSet charWeapons = Mysql.getCharWeapons(id);
        while (charWeapons.next()) {
            weaponList.add(charWeapons.getString(1));
        }
        while (weapons.next()) {
            options.append("<tr><td><input type='checkbox' name='weapons' value='").append(weapons.getString("name")).append("' class='schema'").append(weaponList.contains(weapons.getString("name")) ? " checked" : "").append(">").append(weapons.getString("name")).append(" ").append(weapons.getString("damage")).append("</td></tr>");
        }
        html = html.replaceFirst("<!-- OPTIONS -->", options.toString());
        ResultSet spells = Mysql.getSpells();
        StringBuilder spellOptions = new StringBuilder();
        ArrayList<String> spellList = new ArrayList<>();
        ResultSet charSpells = Mysql.getCharSpells(id);
        while (charSpells.next()) {
            spellList.add(charSpells.getString(1));
        }
        while (spells.next()) {
            spellOptions.append("<tr><td><input type='checkbox' class='schema' name=\"spells\" value=\"").append(spells.getString("id")).append("\" ").append(spellList.contains(spells.getString("name")) ? "checked" : "").append(">").append(spells.getString("name"));
        }
        html = html.replace("<!-- SPELLOPTIONS -->", spellOptions.toString());
        return html;
    }
}
