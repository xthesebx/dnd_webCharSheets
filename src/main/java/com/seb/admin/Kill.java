package com.seb.admin;

import com.seb.abs.JavalinAdminPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Kill extends JavalinAdminPage {
    public Kill(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        System.exit(0);
    }
}
