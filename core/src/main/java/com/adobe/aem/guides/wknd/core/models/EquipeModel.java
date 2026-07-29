package com.adobe.aem.guides.wknd.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.adobe.aem.guides.wknd.core.services.ExibicaoEquipeService;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

@Model(
    adaptables = SlingHttpServletRequest.class,
    resourceType = EquipeModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class EquipeModel {

    public static final String RESOURCE_TYPE = "wknd/components/equipe";

    @ValueMapValue
    @Default(values = "Nossa equipe")
    private String titulo;

    @ChildResource(name = "membros")
    private List<MembroEquipeModel> membros;

    @OSGiService
    private ExibicaoEquipeService exibicaoEquipeService;

    public String getTitulo() {
        return titulo;
    }

    @ScriptVariable
    private Style currentStyle;

    public List<MembroEquipeModel> getMembros() {
        List<MembroEquipeModel> listaCompleta =
            membros == null ? Collections.emptyList() : membros;

        if (exibicaoEquipeService == null) {
            return listaCompleta;
        }

        int maxMembros = exibicaoEquipeService.getMaxMembros();
        int limite = Math.min(maxMembros, listaCompleta.size());

        return Collections.unmodifiableList(
            new ArrayList<>(listaCompleta.subList(0, limite))
        );
    }

    public String getColunas() {
        return getStyleString("colunas", "3");
    }

    public String getFormatoFoto() {
        return getStyleString("formatoFoto", "circular");
    }

    public String getTamanhoFoto() {
        return getStyleString("tamanhoFoto", "media");
    }

    public boolean isMostrarCargo() {
        return getStyleBoolean("mostrarCargo", true);
    }

    public boolean isMostrarFoto() {
        return getStyleBoolean("mostrarFoto", true);
    }

    public String getCssClasses() {
        return String.join(
            " ",
            "cmp-equipe",
            "cmp-equipe--colunas-" + getColunas(),
            "cmp-equipe--formato-" + getFormatoFoto(),
            "cmp-equipe--foto-" + getTamanhoFoto()
        );
    }

    private String getStyleString(String propriedade, String valorPadrao) {
        if (currentStyle == null) {
            return valorPadrao;
        }

        String valor = currentStyle.get(propriedade, String.class);

        if (valor == null || valor.trim().isEmpty()) {
            return valorPadrao;
        }

        return valor;
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