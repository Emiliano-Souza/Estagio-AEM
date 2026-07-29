(function () {
    "use strict";

    const COMPONENT_SELECTOR = ".cmp-equipe";
    const IMAGE_SELECTOR = ".cmp-equipe__foto";
    const CARD_SELECTOR = ".cmp-equipe__membro";

    function tratarImagemQuebrada(imagem) {
        if (imagem.dataset.equipeInicializada === "true") {
            return;
        }

        imagem.dataset.equipeInicializada = "true";

        function marcarComoSemFoto() {
            const card = imagem.closest(CARD_SELECTOR);

            if (card) {
                card.classList.add("cmp-equipe__membro--sem-foto");
            }

            imagem.hidden = true;
        }

        imagem.addEventListener("error", marcarComoSemFoto);

        if (imagem.complete && imagem.naturalWidth === 0) {
            marcarComoSemFoto();
        }
    }

    function inicializarComponente(componente) {
        componente
            .querySelectorAll(IMAGE_SELECTOR)
            .forEach(tratarImagemQuebrada);
    }

    function inicializarTodos() {
        document
            .querySelectorAll(COMPONENT_SELECTOR)
            .forEach(inicializarComponente);
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            inicializarTodos,
            { once: true }
        );
    } else {
        inicializarTodos();
    }
})();