package fr.kainovaii.obsidian.app.controllers;

import fr.kainovaii.obsidian.core.web.component.session.SessionMiddleware;
import fr.kainovaii.obsidian.core.web.controller.BaseController;
import fr.kainovaii.obsidian.core.web.controller.Controller;
import fr.kainovaii.obsidian.core.web.middleware.Before;
import fr.kainovaii.obsidian.core.web.route.methods.GET;
import spark.Request;
import spark.Response;

import java.util.Map;

@Controller
public class HomeController extends BaseController
{
    @Before(SessionMiddleware.class)
    @GET(value = "/", name = "site.home")
    private Object home(Request req, Response res)
    {
        return render("home.html", Map.of());
    }
}