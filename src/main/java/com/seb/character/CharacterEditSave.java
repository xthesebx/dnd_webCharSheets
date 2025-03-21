package com.seb.character;

import com.seb.Mysql;
import com.seb.abs.JavalinAuthPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class CharacterEditSave extends JavalinAuthPage {
    public CharacterEditSave(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Mysql.editCharacter(ctx.pathParam("id"), ctx.formParamMap());
        ctx.redirect("/charsheet/" + ctx.pathParam("id") + "?tab=" + ctx.formParam("tab"));
    }
}
