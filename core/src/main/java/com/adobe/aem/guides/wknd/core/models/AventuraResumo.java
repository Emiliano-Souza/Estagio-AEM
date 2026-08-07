package com.adobe.aem.guides.wknd.core.models;

public final class AventuraResumo {

    private final String titulo;
    private final String imagem;
    private final String dificuldade;
    private final double preco;

    public AventuraResumo(
        String titulo,
        String imagem,
        String dificuldade,
        double preco
    ) {
        this.titulo = titulo == null ? "" : titulo;
        this.imagem = imagem == null ? "" : imagem;
        this.dificuldade = dificuldade == null ? "" : dificuldade;
        this.preco = preco;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagem() {
        return imagem;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public double getPreco() {
        return preco;
    }
}