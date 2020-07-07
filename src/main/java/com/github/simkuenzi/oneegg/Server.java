package com.github.simkuenzi.oneegg;

import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {
    public static void main(String[] args) {

        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port"));

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/com/github/simkuenzi/oneegg/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        JavalinThymeleaf.configure(templateEngine);

        Javalin app = Javalin.create(config ->
                config.addStaticFiles("com/github/simkuenzi/oneegg/static/"))
                .start(port);

        app
                .get("/", ctx -> ctx.render("home.html", model()))
                .post("/", ctx -> {
                    Map<String, Object> vars = model();
                    String in = ctx.formParam("ingredients-in");
                    Ingredients ingredients = new Ingredients(in);
                    vars.put("ingredientsIn", in);
                    vars.put("ingredientsOut", ingredients.calculate().asText());
                    ctx.render("home.html", vars);
                });
    }

    private static Map<String, Object> model() throws IOException {
        Map<String, Object> vars = new HashMap<>();
        Properties versionProps = new Properties();
        versionProps.load(Server.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        return vars;
    }
}
