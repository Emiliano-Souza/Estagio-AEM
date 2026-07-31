package com.adobe.aem.guides.wknd.core.models.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.adobe.aem.guides.wknd.core.models.Equipe;
import com.adobe.aem.guides.wknd.core.models.MembroEquipe;
import com.adobe.aem.guides.wknd.core.services.ExibicaoEquipeService;
import com.day.cq.wcm.api.designer.Style;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = Equipe.class,
    resourceType = Equipe.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class EquipeImpl implements Equipe {

    private static final String TITULO_PADRAO = "Nossa equipe";

    @ValueMapValue
    @Default(values = TITULO_PADRAO)
    private String titulo;

    @ChildResource(name = "membros")
    private List<MembroEquipe> membros;

    @OSGiService
    private ExibicaoEquipeService exibicaoEquipeService;

    @ScriptVariable
    private Style currentStyle;

    @Override
    public String getTitulo() {
        if (titulo == null || titulo.trim().isEmpty()) {
            return TITULO_PADRAO;
        }

        return titulo.trim();
    }

    @Override
    public List<MembroEquipe> getMembros() {
        if (membros == null || membros.isEmpty()) {
            return Collections.emptyList();
        }

        if (exibicaoEquipeService == null) {
            return criarListaImutavel(membros);
        }

        int maxMembros = exibicaoEquipeService.getMaxMembros();

        if (maxMembros <= 0) {
            return Collections.emptyList();
        }

        int limite = Math.min(maxMembros, membros.size());

        return criarListaImutavel(
            membros.subList(0, limite)
        );
    }

    @Override
    public boolean isMostrarCargo() {
        return getStyleBoolean("mostrarCargo", true);
    }

    @Override
    public boolean isMostrarFoto() {
        return getStyleBoolean("mostrarFoto", true);
    }


    private List<MembroEquipe> criarListaImutavel(
        List<MembroEquipe> lista
    ) {
        return Collections.unmodifiableList(
            new ArrayList<>(lista)
        );
    }

    private boolean getStyleBoolean(
        String propriedade,
        boolean valorPadrao
    ) {
        if (currentStyle == null) {
            return valorPadrao;
        }

        Boolean valor = currentStyle.get(
            propriedade,
            Boolean.class
        );

        return valor == null ? valorPadrao : valor;
    }
}