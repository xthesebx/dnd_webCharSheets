package com.seb.Login;

import com.hawolt.logger.Logger;
import com.seb.Main;
import com.seb.Mysql;
import com.seb.abs.JavalinPage;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Login extends JavalinPage {

    public Login(Context ctx) throws NoSuchAlgorithmException {
        super(ctx);
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        assert password != null;
        byte[] data = password.getBytes();
        byte[] hashresult = MessageDigest.getInstance("SHA-256").digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashresult) {
            sb.append(String.format("%02X", b));
        }
        String pwhash = sb.toString().toLowerCase();
        try {
            LoginStatus ls = Mysql.login(username, pwhash);

            String sessionid = ctx.cookie("JSESSIONID");
            JSONObject sessionobject = new JSONObject().put("loginstatus", ls);
            if (ls.equals(LoginStatus.SUCCESS))
                sessionobject.put("user", username).put("timestamp", System.currentTimeMillis() + 900000);
            Main.sessionUserTimer.put(sessionid, sessionobject);
        } catch (SQLException e) {
            Logger.error(e);
        }
        ctx.redirect("/");
    }
}
