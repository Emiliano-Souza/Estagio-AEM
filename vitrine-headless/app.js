"use strict";

const AEM_HOST = "http://localhost:4502";

const ENDPOINTS = {
  todas: "/graphql/execute.json/wknd/aventurasList",
  porDificuldade:
    "/graphql/execute.json/wknd/aventurasListDificuldade",
};

const DIFFICULTY_LABELS = {
  facil: "Fácil",
  moderada: "Moderada",
  dificil: "Difícil",
};

const adventuresList = document.querySelector("#adventures-list");
const statusElement = document.querySelector("#status");
const resultsCount = document.querySelector("#results-count");
const filterButtons = document.querySelectorAll("[data-difficulty]");

let activeRequest = null;

/**
 * Monta somente a URL da Persisted Query.
 * Nenhuma query GraphQL é construída no cliente.
 */
function buildEndpoint(difficulty = "") {
  if (!difficulty) {
    return `${AEM_HOST}${ENDPOINTS.todas}`;
  }

  const value = encodeURIComponent(difficulty);

  return (
    `${AEM_HOST}${ENDPOINTS.porDificuldade}` +
    `;dificuldade=${value}`
  );
}

function escapeHtml(value = "") {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatPrice(value) {
  const price = Number(value);

  if (!Number.isFinite(price)) {
    return "Preço indisponível";
  }

  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL",
  }).format(price);
}

function showStatus(message, isError = false) {
  statusElement.textContent = message;
  statusElement.hidden = false;
  statusElement.classList.toggle("status--error", isError);
}

function hideStatus() {
  statusElement.hidden = true;
  statusElement.classList.remove("status--error");
}

function createAdventureCard(adventure) {
  const title = escapeHtml(adventure.titulo || "Aventura sem título");

  const difficultyValue = adventure.dificuldade || "";
  const difficultyLabel =
    DIFFICULTY_LABELS[difficultyValue] || difficultyValue || "Não informada";

  const instructor = escapeHtml(
    adventure.instrutor?.nome || "Instrutor não informado"
  );

  const description = adventure.descricao?.plaintext?.trim();
  const imagePath = adventure.imagem?._path;
  const imageUrl = imagePath ? `${AEM_HOST}${imagePath}` : "";

  const imageMarkup = imageUrl
    ? `
      <img
        class="adventure-card__image"
        src="${escapeHtml(imageUrl)}"
        alt="${title}"
        loading="lazy"
      >
    `
    : "";

  const descriptionMarkup = description
    ? `
      <p class="adventure-card__description">
        ${escapeHtml(description)}
      </p>
    `
    : "";

  return `
    <article class="adventure-card">
      <div class="adventure-card__image-wrapper">
        ${imageMarkup}

        <span class="adventure-card__difficulty">
          ${escapeHtml(difficultyLabel)}
        </span>
      </div>

      <div class="adventure-card__content">
        <h3 class="adventure-card__title">
          ${title}
        </h3>

        ${descriptionMarkup}

        <div class="adventure-card__footer">
          <p class="adventure-card__instructor">
            Instrutor
            <strong>${instructor}</strong>
          </p>

          <p class="adventure-card__price">
            ${formatPrice(adventure.preco)}
          </p>
        </div>
      </div>
    </article>
  `;
}

function renderAdventures(adventures) {
  adventuresList.innerHTML = adventures
    .map(createAdventureCard)
    .join("");

  const total = adventures.length;

  resultsCount.textContent =
    total === 1
      ? "1 aventura encontrada"
      : `${total} aventuras encontradas`;

  if (total === 0) {
    showStatus("Nenhuma aventura encontrada para este filtro.");
    return;
  }

  hideStatus();
}

async function loadAdventures(difficulty = "") {
  if (activeRequest) {
    activeRequest.abort();
  }

  activeRequest = new AbortController();

  adventuresList.innerHTML = "";
  resultsCount.textContent = "";

  showStatus("Carregando aventuras...");

  try {
    const response = await fetch(buildEndpoint(difficulty), {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
      credentials: "include",
      signal: activeRequest.signal,
    });

    if (!response.ok) {
      throw new Error(
        `AEM respondeu com o status ${response.status}.`
      );
    }

    const json = await response.json();

    if (json.errors?.length) {
      throw new Error(json.errors[0].message);
    }

    const adventures = json.data?.aventuraList?.items ?? [];

    renderAdventures(adventures);
  } catch (error) {
    if (error.name === "AbortError") {
      return;
    }

    console.error("Erro ao carregar aventuras:", error);

    showStatus(
      "Não foi possível carregar as aventuras do AEM.",
      true
    );
  }
}

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    filterButtons.forEach((item) => {
      item.classList.remove("is-active");
    });

    button.classList.add("is-active");

    const difficulty = button.dataset.difficulty || "";

    loadAdventures(difficulty);
  });
});

loadAdventures();