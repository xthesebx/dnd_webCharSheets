package com.seb.abs;

import com.seb.Mysql;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class JavalinAuthPage extends JavalinLoggedInPage {
    public JavalinAuthPage(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String serverId = ctx.pathParam("id");
        if (!Mysql.getCharacterOwner(serverId).equals(getUser())) {
            cancel = true;
            ctx.status(403);
        }
    }
}
