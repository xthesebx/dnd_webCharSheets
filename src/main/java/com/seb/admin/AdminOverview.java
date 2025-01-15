package com.seb.admin;

import com.seb.Mysql;
import com.seb.abs.JavalinAdminPage;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminOverview extends JavalinAdminPage {
    public AdminOverview(Context ctx) throws IOException, SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String html = Files.readString(Path.of("html/adminPage.html"));
        StringBuilder sb = new StringBuilder();
        ResultSet rs = Mysql.getCodes();
        while (rs.next()) {
            sb.append("<a onmouseout=\"this.style.background='inherit'\" onmouseover=\"this.style.background='#136460'\" onclick=\"navigator.clipboard.writeText('").append(rs.getString(1)).append("');\">").append(rs.getString(1)).append("</a><br>");
        }
        html = html.replace("SHOWCODES", sb.toString());
        html += "</body>";
        ctx.html(html);
    }
}
