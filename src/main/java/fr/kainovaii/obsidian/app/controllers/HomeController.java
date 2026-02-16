package fr.kainovaii.obsidian.app.controllers;

import fr.kainovaii.obsidian.http.controller.BaseController;
import fr.kainovaii.obsidian.http.controller.annotations.Controller;
import fr.kainovaii.obsidian.http.middleware.annotations.Before;
import fr.kainovaii.obsidian.livecomponents.session.SessionMiddleware;
import fr.kainovaii.obsidian.routing.methods.GET;
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