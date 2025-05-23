package com.seb.basicSite;

import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CreateShare extends JavalinAuthPage {
    public CreateShare(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Mysql.addShare(ctx.pathParam("id"));
        ctx.redirect("/charsheet/" + ctx.pathParam("id") + "?tab=" + ctx.queryParam("tab") );
    }
}
