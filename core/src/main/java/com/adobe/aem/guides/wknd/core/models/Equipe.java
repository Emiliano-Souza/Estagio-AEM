package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

public interface Equipe {

    String RESOURCE_TYPE = "wknd/components/equipe";

    String getTitulo();

    List<MembroEquipe> getMembros();

    boolean isMostrarCargo();

    boolean isMostrarFoto();

}