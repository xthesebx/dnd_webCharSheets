package com.seb.character;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CharacterNewSave extends JavalinLoggedInPage {
    public CharacterNewSave(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Mysql.addCharacter(getUser(), ctx.formParamMap());
        ctx.redirect("/");
    }
}
