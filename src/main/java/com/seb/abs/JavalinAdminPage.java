package com.seb.abs;

import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class JavalinAdminPage extends JavalinLoggedInPage {
    public JavalinAdminPage(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        if (!this.getUser().equals("stdbasti")) {
            ctx.redirect("/");
            cancel = true;
        }
    }
}
