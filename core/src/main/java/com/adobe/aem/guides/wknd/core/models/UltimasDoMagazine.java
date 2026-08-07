package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

public interface UltimasDoMagazine {

    int getQuantidade();

    List<ArtigoMagazine> getArtigos();

    List<AventuraResumo> getAventuras();

    boolean isVazio();

    boolean isAventurasVazio();
}