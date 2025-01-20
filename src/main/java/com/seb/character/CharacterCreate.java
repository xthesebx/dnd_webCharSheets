package com.seb.character;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharacterCreate extends JavalinLoggedInPage {
    public CharacterCreate(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;

        String html = FileUtil.readFile("html/createChar.html");
        ResultSet weapons = Mysql.getWeapons();
        StringBuilder options = new StringBuilder();
        while (weapons.next()) {
            options.append("<tr><td><input type='checkbox' name='weapons' value='").append(weapons.getString("name")).append("' class='schema'").append(">").append("<td style=\"text-align: center\">").append(weapons.getString("name")).append("</td><td>").append(weapons.getString("damage")).append("</td></tr>");
        }
        html = html.replaceFirst("<!-- OPTIONS -->", options.toString());
        html = html.replace("$ÜBUNGEN", "").replace("$INVENTORY", "").replace("$MERKMALE", "").replace("$STORY", "");
        ResultSet spells = Mysql.getSpells();
        StringBuilder spellOptions = new StringBuilder();
        while (spells.next()) {
            spellOptions.append("<tr><td><input type='checkbox' class='schema' name=\"spells\" value=\"").append(spells.getString("id")).append("\" ").append("></td><td>").append(spells.getString("name")).append("</td><td>").append(spells.getString("level"));
        }
        html = html.replace("<!-- SPELLOPTIONS -->", spellOptions.toString());
        ctx.html(html);

    }
}
