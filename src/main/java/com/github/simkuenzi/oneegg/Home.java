package com.github.simkuenzi.oneegg;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Path("")
public class Home {
    @Context
    private ServletContext servletContext;
    @Context
    private HttpServletRequest servletRequest;
    @Context
    private HttpServletResponse servletResponse;

    private final TemplateEngine templateEngine;

    Home(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response get() throws Exception {
        Map<String, Object> vars = new HashMap<>();
        return render(vars);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response post(MultivaluedMap<String, String> rawForm) throws Exception {
        String in = rawForm.getFirst("ingredients-in");
        Ingredients ingredients = new Ingredients(in);
        Map<String, Object> vars = new HashMap<>();
        vars.put("ingredientsIn", in);
        vars.put("ingredientsOut", ingredients.calculate().asText());
        return render(vars);
    }

    protected Response render(Map<String, Object> vars) throws Exception {
        Properties versionProps = new Properties();
        versionProps.load(Home.class.getResourceAsStream("version.properties"));
        vars.put("version", versionProps.getProperty("version"));
        WebContext context = new WebContext(servletRequest, servletResponse, servletContext, servletRequest.getLocale(), vars);
        return Response.ok(templateEngine.process("home", context), MediaType.TEXT_HTML_TYPE.withCharset("utf-8")).build();
    }
}
