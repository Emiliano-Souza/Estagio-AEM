package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    resourceType = PerfilModel.RESOURCE_TYPE,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class PerfilModel {

    public static final String RESOURCE_TYPE = "wknd/components/perfil";

    @ValueMapValue
    private String nome;

    @ValueMapValue
    private String cargo;

    @ValueMapValue
    private String bio;

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    public String getBio() {
        return bio;
    }
}