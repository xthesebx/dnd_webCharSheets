package com.seb.Login;

import com.seb.Main;
import com.seb.abs.JavalinPage;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoginPage extends JavalinPage {

    public LoginPage(Context ctx) throws IOException {
        super(ctx);
        String sessionid = ctx.cookie("JSESSIONID");
        ctx.sessionAttributeMap();
        String page = Files.readString(Path.of("html/login.html"));
        LoginStatus ls = LoginStatus.NONE;
        if (sessionid != null) {
            if (Main.sessionUserTimer.has(sessionid))
                if (Main.sessionUserTimer.getJSONObject(sessionid).has("loginstatus")) {
                    if (Main.sessionUserTimer.getJSONObject(sessionid).has("timestamp"))
                        if (Main.sessionUserTimer.getJSONObject(sessionid).getLong("timestamp") < System.currentTimeMillis())
                            Main.sessionUserTimer.getJSONObject(sessionid).put("loginstatus", LoginStatus.TIMED_OUT);
                    ls = (LoginStatus) Main.sessionUserTimer.getJSONObject(sessionid).get("loginstatus");
                    Main.sessionUserTimer.getJSONObject(sessionid).put("loginstatus", LoginStatus.NONE);
                }
        }
        switch (ls) {
            case WRONG_DATA -> page = page.replace("$ERROR", "Wrong Login Information, please try again");
            case SUCCESS -> ctx.redirect("/");
            case NONE -> page = page.replace("$ERROR", "");
            case TIMED_OUT -> page = page.replace("$ERROR", "Login timed out, please login again");
            case NO_CONNECTION -> page = page.replace("$ERROR", "Server Error, please try again later");
        }
        ctx.html(page);
    }
}
