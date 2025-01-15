package com.seb.abs;

import com.hawolt.logger.Logger;
import com.seb.Login.LoginStatus;
import com.seb.Main;
import com.seb.Mysql;
import io.javalin.http.Context;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class JavalinLoggedInPage extends JavalinPage {
    protected boolean cancel = false;
    public JavalinLoggedInPage(Context ctx) throws NoSuchAlgorithmException, SQLException {
        super(ctx);
        if (!Main.isLoggedIn(ctx.cookie("JSESSIONID"))) {
            if (ctx.ip().equals("127.0.0.1")) {
                JSONObject logindata = new JSONObject();
                try {
                    logindata = new JSONObject(Files.readString(Path.of("logindata.json")));
                } catch (IOException e) {
                    Logger.error(e);
                }
                String password = logindata.getString("password");
                String username = logindata.getString("username");
                assert password != null;
                byte[] data = password.getBytes();
                byte[] hashresult = MessageDigest.getInstance("SHA-256").digest(data);
                StringBuilder sb = new StringBuilder();
                for (byte b : hashresult) {
                    sb.append(String.format("%02X", b));
                }
                String pwhash = sb.toString().toLowerCase();
                LoginStatus ls = Mysql.login(username, pwhash);
                String sessionid = ctx.cookie("JSESSIONID");
                JSONObject sessionobject = new JSONObject().put("loginstatus", ls);
                if (ls.equals(LoginStatus.SUCCESS))
                    sessionobject.put("user", username).put("timestamp", System.currentTimeMillis() + 900000);
                Main.sessionUserTimer.put(sessionid, sessionobject);
                return;
            }
            ctx.redirect("/login");
            cancel = true;
        } else Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).put("timestamp", System.currentTimeMillis() + 300000);
    }

    protected String getUser() {
        return Main.sessionUserTimer.getJSONObject(ctx.cookie("JSESSIONID")).getString("user");
    }
}
