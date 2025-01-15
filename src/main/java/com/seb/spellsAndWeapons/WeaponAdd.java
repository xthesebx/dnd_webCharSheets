package com.seb.spellsAndWeapons;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponAdd extends JavalinLoggedInPage {
    public WeaponAdd(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/weaponAdd.html");
        ResultSet rs = Mysql.getWeapons();
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            sb.append("<tr><td class=\"tablerow\">" + rs.getString(1) + "</td><td class=\"tablerow\">" + rs.getString(2) + "</td><td class=\"tablerow\">" + rs.getString(3) + "</td><td class=\"tablerow\">" + rs.getString(4) + "</td><td class=\"tablerow\">" + rs.getString(5) + "</td></tr>");
        }
        html = html.replace("<!-- $EINTRÄGE -->", sb.toString());
        if (ctx.queryParam("error") != null) {
            html = html.replace("<body>", "<body><a class=\"error\">Error, maybe some columns were left empty?</a>");
        }
        ctx.html(html);
    }
}
