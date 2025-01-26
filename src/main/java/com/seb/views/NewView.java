package com.seb.views;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class NewView extends JavalinLoggedInPage {
    public NewView(Context ctx) throws NoSuchAlgorithmException, SQLException {
        super(ctx);
        if(cancel) return;
        String basename = "New Tab";
        int i = 1;
        String tabname = "New Tab";
        while (Mysql.userHasCustomView(getUser(), tabname)) {
            i++;
            tabname = basename + i;
        }
        Mysql.createCustomView(getUser(), tabname);
        ctx.redirect("/charsheet/" + ctx.queryParam("id") + "?tab=" + tabname);
    }
}
