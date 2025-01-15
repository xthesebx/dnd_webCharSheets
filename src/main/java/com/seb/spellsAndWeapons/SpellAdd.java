package com.seb.spellsAndWeapons;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpellAdd extends JavalinLoggedInPage {
    public SpellAdd(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/spellAdd.html");
        ResultSet rs = Mysql.getSpells();
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append("<tr><td class=\"tablerow\">" + rs.getString(1) + "</td><td class=\"tablerow\">" + rs.getString(2) + "</td><td class=\"tablerow\">" + rs.getString(3) + "</td><td class=\"tablerow\">" + rs.getString(4) + "</td></tr>");
        }
        html = html.replace("<!-- $EINTRÄGE -->", sb.toString());
        if (ctx.queryParam("error") != null) {
            html = html.replace("<body>", "<body><a class=\"error\">Error, maybe some columns were left empty?</a>");
        }
        ctx.html(html);
    }
}
