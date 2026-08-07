package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.models.ArtigoMagazine;
import com.adobe.aem.guides.wknd.core.models.AventuraResumo;
import com.adobe.aem.guides.wknd.core.models.UltimasDoMagazine;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = UltimasDoMagazine.class,
    resourceType = UltimasDoMagazineImpl.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class UltimasDoMagazineImpl implements UltimasDoMagazine {

    public static final String RESOURCE_TYPE =
        "wknd/components/ultimas-do-magazine";

    private static final String MAGAZINE_PATH =
        "/content/wknd/us/en/magazine";

    private static final String AVENTURAS_PATH =
        "/content/dam/wknd/aventuras";

    private static final int QUANTIDADE_PADRAO = 4;

    private static final int QUANTIDADE_AVENTURAS = 2;

    private static final Logger LOG =
        LoggerFactory.getLogger(UltimasDoMagazineImpl.class);

    @OSGiService
    private QueryBuilder queryBuilder;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private PageManager pageManager;

    @ValueMapValue
    @Default(intValues = QUANTIDADE_PADRAO)
    private int quantidade;

    private List<ArtigoMagazine> artigos =
        Collections.emptyList();

    private List<AventuraResumo> aventuras =
        Collections.emptyList();

    @PostConstruct
    protected void init() {
        if (queryBuilder == null || resourceResolver == null) {
            LOG.warn(
                "Não foi possível carregar o conteúdo: "
                    + "QueryBuilder ou ResourceResolver indisponível."
            );
            return;
        }

        Session session = resourceResolver.adaptTo(Session.class);

        if (session == null) {
            LOG.warn(
                "Não foi possível adaptar o ResourceResolver para Session."
            );
            return;
        }

        carregarArtigos(session);
        carregarAventuras(session);
    }

    private void carregarArtigos(Session session) {
        int limite = quantidade > 0
            ? quantidade
            : QUANTIDADE_PADRAO;

        PageManager resolvedPageManager = pageManager;

        if (resolvedPageManager == null) {
            resolvedPageManager =
                resourceResolver.adaptTo(PageManager.class);
        }

        if (resolvedPageManager == null) {
            LOG.warn(
                "PageManager indisponível para consultar o Magazine."
            );
            return;
        }

        Query query = queryBuilder.createQuery(
            PredicateGroup.create(
                criarPredicadosArtigos(limite)
            ),
            session
        );

        List<ArtigoMagazine> resultados =
            new ArrayList<>();

        for (Hit hit : query.getResult().getHits()) {
            adicionarArtigo(
                hit,
                resolvedPageManager,
                resultados
            );
        }

        artigos =
            Collections.unmodifiableList(resultados);
    }

    private Map<String, String> criarPredicadosArtigos(
        int limite
    ) {
        Map<String, String> predicates =
            new LinkedHashMap<>();

        predicates.put("path", MAGAZINE_PATH);
        predicates.put("type", "cq:Page");

        predicates.put(
            "orderby",
            "@jcr:content/jcr:created"
        );

        predicates.put("orderby.sort", "desc");
        predicates.put(
            "p.limit",
            String.valueOf(limite)
        );

        return predicates;
    }

    private void adicionarArtigo(
        Hit hit,
        PageManager resolvedPageManager,
        List<ArtigoMagazine> resultados
    ) {
        try {
            Page page = resolvedPageManager.getPage(
                hit.getPath()
            );

            if (page == null) {
                return;
            }

            String titulo = page.getTitle();

            if (isBlank(titulo)) {
                titulo = page.getName();
            }

            String imagem = localizarImagem(page);
            String link = page.getPath() + ".html";

            resultados.add(
                new ArtigoMagazine(
                    titulo,
                    imagem,
                    link
                )
            );

        } catch (RepositoryException exception) {
            LOG.warn(
                "Não foi possível processar um resultado "
                    + "da consulta do Magazine.",
                exception
            );
        }
    }

    private void carregarAventuras(Session session) {
        Query query = queryBuilder.createQuery(
            PredicateGroup.create(
                criarPredicadosAventuras()
            ),
            session
        );

        List<AventuraResumo> resultados =
            new ArrayList<>();

        for (Hit hit : query.getResult().getHits()) {
            adicionarAventura(hit, resultados);
        }

        aventuras =
            Collections.unmodifiableList(resultados);
    }

    private Map<String, String> criarPredicadosAventuras() {
        Map<String, String> predicates =
            new LinkedHashMap<>();

        predicates.put("path", AVENTURAS_PATH);
        predicates.put("type", "dam:Asset");

        predicates.put(
            "1_property",
            "jcr:content/contentFragment"
        );

        predicates.put(
            "1_property.value",
            "true"
        );

        predicates.put(
            "orderby",
            "@jcr:created"
        );

        predicates.put("orderby.sort", "desc");

        predicates.put(
            "p.limit",
            String.valueOf(QUANTIDADE_AVENTURAS)
        );

        return predicates;
    }

    private void adicionarAventura(
        Hit hit,
        List<AventuraResumo> resultados
    ) {
        try {
            Resource fragmentResource =
                resourceResolver.getResource(
                    hit.getPath()
                );

            if (fragmentResource == null) {
                return;
            }

            Resource masterResource =
                fragmentResource.getChild(
                    "jcr:content/data/master"
                );

            if (masterResource == null) {
                return;
            }

            ValueMap properties =
                masterResource.getValueMap();

            String titulo =
                properties.get("titulo", String.class);

            if (isBlank(titulo)) {
                titulo = fragmentResource.getName();
            }

            String imagem =
                properties.get("imagem", String.class);

            String dificuldade =
                properties.get(
                    "dificuldade",
                    String.class
                );

            double preco = obterPreco(
                properties.get("preco")
            );

            resultados.add(
                new AventuraResumo(
                    titulo,
                    imagem,
                    dificuldade,
                    preco
                )
            );

        } catch (RepositoryException exception) {
            LOG.warn(
                "Não foi possível processar "
                    + "um Content Fragment de Aventura.",
                exception
            );
        }
    }

    private double obterPreco(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        if (value != null) {
            try {
                return Double.parseDouble(
                    String.valueOf(value)
                );
            } catch (NumberFormatException exception) {
                LOG.debug(
                    "Preço inválido no Content Fragment: {}",
                    value
                );
            }
        }

        return 0;
    }

    private String localizarImagem(Page page) {
        Resource contentResource =
            page.getContentResource();

        if (contentResource == null) {
            return "";
        }

        String[] caminhosPossiveis = {
            "image",
            "root/container/image",
            "root/container/container/image"
        };

        for (String caminho : caminhosPossiveis) {
            Resource imageResource =
                contentResource.getChild(caminho);

            String referencia =
                obterFileReference(imageResource);

            if (!isBlank(referencia)) {
                return referencia;
            }
        }

        return procurarImagemRecursivamente(
            contentResource,
            0
        );
    }

    private String obterFileReference(
        Resource resource
    ) {
        if (resource == null) {
            return "";
        }

        String fileReference =
            resource.getValueMap().get(
                "fileReference",
                String.class
            );

        return fileReference == null
            ? ""
            : fileReference;
    }

    private String procurarImagemRecursivamente(
        Resource resource,
        int profundidade
    ) {
        if (resource == null || profundidade > 6) {
            return "";
        }

        String referencia =
            obterFileReference(resource);

        if (!isBlank(referencia)) {
            return referencia;
        }

        for (Resource child : resource.getChildren()) {
            String encontrada =
                procurarImagemRecursivamente(
                    child,
                    profundidade + 1
                );

            if (!isBlank(encontrada)) {
                return encontrada;
            }
        }

        return "";
    }

    private boolean isBlank(String value) {
        return value == null
            || value.trim().isEmpty();
    }

    @Override
    public int getQuantidade() {
        return quantidade > 0
            ? quantidade
            : QUANTIDADE_PADRAO;
    }

    @Override
    public List<ArtigoMagazine> getArtigos() {
        return artigos;
    }

    @Override
    public List<AventuraResumo> getAventuras() {
        return aventuras;
    }

    @Override
    public boolean isVazio() {
        return artigos.isEmpty();
    }

    @Override
    public boolean isAventurasVazio() {
        return aventuras.isEmpty();
    }
}