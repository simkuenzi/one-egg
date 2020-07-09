package com.github.simkuenzi.oneegg;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {
    public static void main(String[] args) {

        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "0"));
        String context = System.getProperty("com.github.simkuenzi.http.context", "/");

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/com/github/simkuenzi/oneegg/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        JavalinThymeleaf.configure(templateEngine);

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("com/github/simkuenzi/oneegg/static/");
            config.contextPath = context;

            // Got those errors on the apache proxy with compression enabled. Related to the Issue below?
            // AH01435: Charset null not supported.  Consider aliasing it?, referer: http://pi/one-egg/
            // AH01436: No usable charset information; using configuration default, referer: http://pi/one-egg/
            config.compressionStrategy(CompressionStrategy.NONE);
        })

        // Workaround for https://github.com/tipsy/javalin/issues/1016
        // Aside from mangled up characters the wrong encoding caused apache proxy to fail on style.css.
        // Apache error log: AH01385: Zlib error -2 flushing zlib output buffer ((null))
        .before(ctx -> {
            if (ctx.res.getCharacterEncoding().equals("utf-8")) {
                ctx.res.setCharacterEncoding(StandardCharsets.UTF_8.name());
            }
        })

        .start(port);

        app
            .get("/", ctx -> ctx.render("home.html", model()))
            .post("/", ctx -> {
                Map<String, Object> vars = model();
                String in = ctx.formParam("ingredients-in");
                Recipe recipe = new Recipe(new TextIngredients(in));
                vars.put("ingredientsIn", in);
                vars.put("ingredientsOut", recipe.calculate().getIngredients());
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
