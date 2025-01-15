package com.seb.Registration;

import com.seb.abs.JavalinPage;
import io.javalin.http.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RegisterPage extends JavalinPage {
    public RegisterPage(Context ctx) throws IOException {
        super(ctx);
        ctx.html(Files.readString(Path.of("html/register.html")));
    }
}
