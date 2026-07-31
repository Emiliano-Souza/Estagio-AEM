(function () {
    "use strict";

    const IMAGE_SELECTOR = ".cmp-equipe__foto";

    function mostrarAvatar(imagem) {
        const card = imagem.closest(".cmp-equipe__membro");

        if (!card) {
            return;
        }

        const avatar = card.querySelector(".cmp-equipe__avatar");

        imagem.hidden = true;

        if (avatar) {
            avatar.hidden = false;
        }
    }

    function prepararImagem(imagem) {
        if (imagem.dataset.equipeInicializada === "true") {
            return;
        }

        imagem.dataset.equipeInicializada = "true";

        imagem.addEventListener(
            "error",
            function () {
                mostrarAvatar(imagem);
            },
            { once: true }
        );

        if (imagem.complete && imagem.naturalWidth === 0) {
            mostrarAvatar(imagem);
        }
    }

    function inicializar() {
        document
            .querySelectorAll(IMAGE_SELECTOR)
            .forEach(prepararImagem);
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            inicializar,
            { once: true }
        );
    } else {
        inicializar();
    }
})();