package com.adobe.aem.guides.wknd.core.servlets;

import com.adobe.aem.guides.wknd.core.models.UltimasDoMagazine;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint JSON personalizado do componente Últimas do Magazine.
 *
 * Exemplo de URL:
 * caminho-do-componente.ultimas.json
 */
@Component(service = Servlet.class)
@SlingServletResourceTypes(
    resourceTypes = "wknd/components/ultimas-do-magazine",
    selectors = "ultimas",
    extensions = "json",
    methods = HttpConstants.METHOD_GET
)
public class UltimasDoMagazineServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG =
        LoggerFactory.getLogger(UltimasDoMagazineServlet.class);

    private static final ObjectMapper OBJECT_MAPPER =
        new ObjectMapper();

    @Override
    protected void doGet(
        SlingHttpServletRequest request,
        SlingHttpServletResponse response
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /*
         * A requisição aponta para uma instância do componente.
         *
         * O Sling adapta essa requisição para a interface
         * UltimasDoMagazine, cuja implementação executa a query.
         */
        UltimasDoMagazine model =
            request.adaptTo(UltimasDoMagazine.class);

        if (model == null) {
            responderErro(request, response);
            return;
        }

        /*
         * LinkedHashMap mantém a ordem em que os campos
         * foram adicionados ao JSON.
         */
        Map<String, Object> resultado = new LinkedHashMap<>();

        resultado.put("origem", "Sling Servlet");
        resultado.put("endpoint", "ultimas");
        resultado.put(
            "recurso",
            request.getResource().getPath()
        );
        resultado.put(
            "limiteConfigurado",
            model.getQuantidade()
        );
        resultado.put(
            "totalRetornado",
            model.getArtigos().size()
        );
        resultado.put(
            "vazio",
            model.isVazio()
        );
        resultado.put(
            "itens",
            model.getArtigos()
        );

        response.setStatus(
            SlingHttpServletResponse.SC_OK
        );

        OBJECT_MAPPER.writeValue(
            response.getWriter(),
            resultado
        );
    }

    /**
     * Produz uma resposta de erro controlada caso a adaptação
     * para o Sling Model não seja possível.
     */
    private void responderErro(
        SlingHttpServletRequest request,
        SlingHttpServletResponse response
    ) throws IOException {

        String resourcePath =
            request.getResource().getPath();

        LOG.warn(
            "Não foi possível adaptar o recurso {} "
                + "para UltimasDoMagazine.",
            resourcePath
        );

        response.setStatus(
            SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );

        Map<String, Object> erro = new LinkedHashMap<>();

        erro.put("origem", "Sling Servlet");
        erro.put("status", 500);
        erro.put(
            "erro",
            "Não foi possível carregar os artigos do Magazine."
        );
        erro.put("recurso", resourcePath);

        OBJECT_MAPPER.writeValue(
            response.getWriter(),
            erro
        );
    }
}