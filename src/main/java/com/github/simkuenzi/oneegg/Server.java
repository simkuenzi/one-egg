package com.github.simkuenzi.oneegg;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.plugin.rendering.FileRenderer;
import io.javalin.plugin.rendering.JavalinRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class Server {
    public static void main(String[] args) {

        int port = Integer.parseInt(System.getProperty("com.github.simkuenzi.http.port", "9000"));
        String context = System.getProperty("com.github.simkuenzi.http.context", "/");

        JavalinRenderer.register(renderer(TemplateMode.HTML), ".html");
        JavalinRenderer.register(renderer(TemplateMode.JAVASCRIPT), ".js");

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
            .get("/", ctx -> ctx.render("home.html", model(new Recipe(), new Recipe())))
            .post("/", ctx -> {
                Recipe recipe = new Recipe(ctx);
                ctx.render("home.html", model(recipe, recipe.calculate()));
            })
            .post("/evalRef", ctx -> ctx.json(new TextIngredients(ctx.body()).all().map(Ingredient::getProductName).collect(Collectors.toList())))
            .post("/evalDef", ctx -> ctx.result(new Recipe(new TextIngredients(ctx.body())).defaultReference()))
            .post("/ingredient/:name/typeOptions.json", ctx -> {
                Map<String, Object> model = new HashMap<>();
                model.put("quantityType", new TextIngredients(ctx.body()).quantityType(ctx.pathParam("name")).name());
                ctx.render("typeOptions.js", model);
                ctx.contentType("text/json");
            });
    }

    private static Map<String, Object> model(Recipe origRecipe, Recipe newRecipe) throws IOException {
        Map<String, Object> vars = new HashMap<>();
        Properties versionProps = new Properties();
        versionProps.load(Server.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        vars.put("recipe", origRecipe);
        vars.put("newRecipe", newRecipe);
        return vars;
    }

    private static FileRenderer renderer(TemplateMode templateMode) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setPrefix("/com/github/simkuenzi/oneegg/templates/");
        templateResolver.setCacheable(false);
        templateResolver.setForceTemplateMode(true);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return (filePath, model, context) -> {
            WebContext thymeleafContext = new WebContext(context.req, context.res, context.req.getServletContext(), context.req.getLocale());
            thymeleafContext.setVariables(model);
            return templateEngine.process(filePath, thymeleafContext);
        };
    }
}
