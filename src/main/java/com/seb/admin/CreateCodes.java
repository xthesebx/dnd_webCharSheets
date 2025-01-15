package com.seb.admin;

import com.seb.Mysql;
import com.seb.abs.JavalinAdminPage;
import io.javalin.http.Context;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public class CreateCodes extends JavalinAdminPage {
    public CreateCodes(Context ctx) throws SQLException, NoSuchAlgorithmException {
        super(ctx);
        if (cancel) return;
        Random ran = new Random();
        byte[] code = new byte[16];
        for (int i = 0; i < Integer.parseInt(Objects.requireNonNull(ctx.formParam("count"))); i++) {
            ran.nextBytes(code);
            StringBuilder sb = new StringBuilder();
            for (byte b : code) {
                sb.append(String.format("%02X", b));
            }
            String codeString = sb.toString();
            if (!Mysql.codeExists(codeString)) Mysql.addCode(codeString);
            else i--;
        }
        ctx.redirect("/admin");
    }
}
