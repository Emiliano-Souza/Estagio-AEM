package com.adobe.aem.guides.wknd.core.models;

public final class ArtigoMagazine {

    private final String titulo;
    private final String imagem;
    private final String link;

    public ArtigoMagazine(String titulo, String imagem, String link) {
        this.titulo = titulo == null ? "" : titulo;
        this.imagem = imagem == null ? "" : imagem;
        this.link = link == null ? "" : link;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public String getLink() {
        return link;
    }
}