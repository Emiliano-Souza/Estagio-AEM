package com.adobe.aem.guides.wknd.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MembroEquipeModel {

    @ValueMapValue
    @Default(values = "")
    private String nome;

    @ValueMapValue
    @Default(values = "")
    private String cargo;

    @ValueMapValue
    @Default(values = "")
    private String foto;

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    public String getFoto() {
        return foto;
    }
}