package com.seb.character;

import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class CharacterEdit extends JavalinAuthPage {
    public CharacterEdit(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;

        String html = FileUtil.readFile("html/editChar.html");
        String id = ctx.pathParam("id");
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
        ResultSet weapons = Mysql.getWeapons();
        StringBuilder options = new StringBuilder();
        ArrayList<String> weaponList = new ArrayList<>();
        ResultSet charWeapons = Mysql.getCharWeapons(ctx.pathParam("id"));
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
        ResultSet charSpells = Mysql.getCharSpells(ctx.pathParam("id"));
        while (charSpells.next()) {
            spellList.add(charSpells.getString(1));
        }
        while (spells.next()) {
            spellOptions.append("<tr><td><input type='checkbox' class='schema' name=\"spells\" value=\"").append(spells.getString("id")).append("\" ").append(spellList.contains(spells.getString("name")) ? "checked" : "").append(">").append(spells.getString("name"));
        }
        html = html.replace("<!-- SPELLOPTIONS -->", spellOptions.toString());
        ctx.html(html);
    }
}
