package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    resourceType = DestaqueModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class DestaqueModel {

    public static final String RESOURCE_TYPE = "wknd/components/destaque";

    @ValueMapValue
    private String titulo;

    @ValueMapValue
    private String texto;

    @ValueMapValue
    private String textoBotao;

    @ValueMapValue
    private String urlBotao;

    public String getTitulo() {
        return titulo;
    }

    public String getTexto() {
        return texto;
    }

    public String getTextoBotao() {
        return textoBotao;
    }

    public String getUrlBotao() {
        return urlBotao;
    }

    public boolean isMostrarBotao() {
        return urlBotao != null && !urlBotao.trim().isEmpty();
    }
}