package com.adobe.aem.guides.wknd.core.models;

import java.util.List;

public interface UltimasDoMagazine {

    int getQuantidade();

    List<ArtigoMagazine> getArtigos();

    boolean isVazio();
}