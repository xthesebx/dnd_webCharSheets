package com.seb.spellsAndWeapons;

import com.hawolt.logger.Logger;
import com.seb.Mysql;
import com.seb.abs.JavalinLoggedInPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AddWeapon extends JavalinLoggedInPage {
    public AddWeapon(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        String finesse, spell;
        if (ctx.formParam("finesse") == null) finesse = "0";
        else finesse = "1";
        if (ctx.formParam("spell") == null) spell = "0";
        else spell = "1";
        try {
        Mysql.addWeapon(ctx.formParam("name"), finesse, ctx.formParam("damage"), ctx.formParam("damagetype"), spell);
        } catch (SQLException e) {
            ctx.redirect("/weaponAdd?error=true");
            return;
        }
        ctx.redirect("/weaponAdd");
    }
}
