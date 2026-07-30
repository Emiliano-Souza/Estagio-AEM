package com.adobe.aem.guides.wknd.core.models.impl;

import java.util.Locale;

import com.adobe.aem.guides.wknd.core.models.MembroEquipe;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    adapters = MembroEquipe.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class MembroEquipeImpl implements MembroEquipe {

    private static final String NOME_PADRAO = "Membro da equipe";

    @ValueMapValue
    @Default(values = NOME_PADRAO)
    private String nome;

    @ValueMapValue
    @Default(values = "")
    private String cargo;

    @ValueMapValue
    @Default(values = "")
    private String foto;

    @Override
    public String getNome() {
        return isBlank(nome) ? NOME_PADRAO : nome.trim();
    }

    @Override
    public String getCargo() {
        return isBlank(cargo) ? "" : cargo.trim();
    }

    @Override
    public String getFoto() {
        return isBlank(foto) ? "" : foto.trim();
    }

    @Override
    public boolean isTemCargo() {
        return !isBlank(cargo);
    }

    @Override
    public boolean isTemFoto() {
        return !isBlank(foto);
    }

    @Override
    public String getInicial() {
        return getNome()
            .substring(0, 1)
            .toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}