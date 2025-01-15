package com.seb.Login;

import com.seb.Main;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Logout extends JavalinLoggedInPage {
    public Logout(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Main.sessionUserTimer.remove(ctx.cookie("JSESSIONID"));
        ctx.redirect("/login");
    }
}
