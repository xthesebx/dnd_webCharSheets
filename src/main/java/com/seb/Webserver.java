package com.seb;

import com.seb.Login.Login;
import com.seb.Login.LoginPage;
import com.seb.Login.Logout;
import com.seb.Registration.Register;
import com.seb.Registration.RegisterPage;
import com.seb.admin.AdminOverview;
import com.seb.admin.CreateCodes;
import com.seb.admin.Kill;
import com.seb.basicSite.Overview;
import com.seb.character.*;
import com.seb.spellsAndWeapons.AddSpell;
import com.seb.spellsAndWeapons.AddWeapon;
import com.seb.spellsAndWeapons.SpellAdd;
import com.seb.spellsAndWeapons.WeaponAdd;
import io.javalin.Javalin;
import io.javalin.util.FileUtil;


public class Webserver {
    public Webserver() {
        Javalin javalin = Javalin.create().start(8008);

        javalin.post("/loginpost", Login::new);
        javalin.post("/kill", Kill::new);
        javalin.post("/createcodes", CreateCodes::new);
        javalin.post("/registerpost", Register::new);

        javalin.get("/savenewchar", CharacterNewSave::new);
        javalin.get("/logout", Logout::new);
        javalin.get("/login", LoginPage::new);
        javalin.get("/", Overview::new);
        javalin.get("/admin", AdminOverview::new);
        javalin.get("/register", RegisterPage::new);
        javalin.get("/createpage", CharacterCreate::new);
        javalin.get("/blank", ctx -> ctx.html(FileUtil.readFile("html/blankChar.html")));
        javalin.get("/editable", ctx -> ctx.html(FileUtil.readFile("html/editableChar.html")));
        javalin.get("/charsheet/<id>", CharView::new);
        javalin.get("/edit/<id>", CharacterEdit::new);
        javalin.get("/editchar/<id>", CharacterEditSave::new);
        javalin.get("/weaponAdd", WeaponAdd::new);
        javalin.get("/spellAdd", SpellAdd::new);
        javalin.get("/addspell", AddSpell::new);
        javalin.get("/addweapon", AddWeapon::new);
        javalin.get("/style.css", ctx -> ctx.html(FileUtil.readFile("html/style.css")));
    }
}
