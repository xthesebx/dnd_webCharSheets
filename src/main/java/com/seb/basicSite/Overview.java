package com.seb.basicSite;

import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;
import io.javalin.util.FileUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Overview extends JavalinLoggedInPage {

    public Overview(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String html = FileUtil.readFile("html/Overview.html");
        ResultSet rs = Mysql.getCharacters(getUser());
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            String id = rs.getString(2);
            sb.append("<tr> <td style='text-align: center' class='tablerow' onclick=\"window.location='/charsheet/").append(id).append("'\">").append(rs.getString(1))
                    .append("</td> <td style='text-align: center' class='tablerow' onclick=\"window.location='/charsheet/").append(id).append("'\">").append(rs.getString("class"))
                    .append("</td> <td style='text-align: center' class='tablerow' onclick=\"window.location='/charsheet/").append(id).append("'\">").append(rs.getString("level"))
                    .append("</td> <td style='text-align: center' class='tablerow' onclick=\"window.location='/charsheet/").append(id).append("'\">").append(id)
                    .append("</tr>");
        }
        html = html.replace("$EINTRÄGE", sb.toString());
        if (Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user").equals("stdbasti"))
            html = html.replace("ADMINBUTTON","<br><button onclick=\"location='/admin'\" class='scheme'>Admin</button>");
        else html = html.replace("ADMINBUTTON","");
        ctx.html(html);
    }
}
