(function () {
    "use strict";

    function inicializarDestaque(componente) {
        if (componente.dataset.destaqueInicializado === "true") {
            return;
        }

        componente.dataset.destaqueInicializado = "true";
        componente.classList.add("cmp-destaque--inicializado");
    }

    function inicializarTodosOsDestaques() {
        const componentes = document.querySelectorAll(
            '[data-cmp-is="destaque"]'
        );

        componentes.forEach(inicializarDestaque);
    }

    if (document.readyState === "loading") {
        document.addEventListener(
            "DOMContentLoaded",
            inicializarTodosOsDestaques
        );
    } else {
        inicializarTodosOsDestaques();
    }
})();