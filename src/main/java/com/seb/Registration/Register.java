package com.seb.Registration;

import com.hawolt.logger.Logger;
import com.seb.Mysql;
import com.seb.abs.JavalinPage;
import io.javalin.http.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Register extends JavalinPage {
    //TODO: add error messages (wrong/used code, username exists already)
    public Register(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        String code = ctx.formParam("code");
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        if (Mysql.codeExists(code)) {
            assert password != null;
            byte[] data = password.getBytes();
            byte[] hashresult = MessageDigest.getInstance("SHA-256").digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashresult) {
                sb.append(String.format("%02X", b));
            }
            String pwhash = sb.toString().toLowerCase();
            Mysql.addUser(username, pwhash);
            Mysql.deleteCode(code);
        } else Logger.error(code);
        ctx.redirect("/login");
    }
}
