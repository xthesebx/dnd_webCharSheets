package com.seb.character;

import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CharacterDelete extends JavalinAuthPage {
    public CharacterDelete(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Mysql.deleteChar(ctx.pathParam("id"));
        ctx.redirect("/");
    }
}
