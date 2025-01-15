package com.seb.spellsAndWeapons;

import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AddSpell extends JavalinLoggedInPage {
    public AddSpell(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        try {
            Mysql.addSpell(ctx.queryParam("name"), ctx.queryParam("requirements"), ctx.queryParam("level"), ctx.queryParam("description"));
        } catch (SQLException e) {
            ctx.redirect("/spellAdd?error=true");
            return;
        }
        ctx.redirect("/spellAdd");
    }
}
