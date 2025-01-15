package com.seb;

import com.seb.Login.LoginStatus;
import org.json.JSONObject;

import java.sql.SQLException;

public class Main {

    public static JSONObject sessionUserTimer = new JSONObject();

    public static void main(String[] args) throws SQLException {
        new Webserver();
        //TODO: layouts für fight etc., layout editor
        Mysql.createMysql();
    }

    public static boolean isLoggedIn(String sessionId) {
        return sessionId != null && Main.sessionUserTimer.has(sessionId) &&
                Main.sessionUserTimer.getJSONObject(sessionId).has("timestamp") &&
                Main.sessionUserTimer.getJSONObject(sessionId).getLong("timestamp") >= System.currentTimeMillis() &&
                Main.sessionUserTimer.getJSONObject(sessionId).get("loginstatus").equals(LoginStatus.SUCCESS);
    }
}