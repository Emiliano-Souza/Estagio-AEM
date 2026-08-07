import { AEM_HOST, DIFFICULTY_LABELS } from "./config.js";

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

export function createAdventureCard(adventure) {
  const title = escapeHtml(
    adventure.titulo || "Aventura sem título"
  );

  const difficultyValue = adventure.dificuldade || "";

  const difficultyLabel =
    DIFFICULTY_LABELS[difficultyValue] ||
    difficultyValue ||
    "Não informada";

  const instructor = escapeHtml(
    adventure.instrutor?.nome || "Instrutor não informado"
  );

  const description = adventure.descricao?.plaintext?.trim();

  const imagePath = adventure.imagem?._path;

  const imageUrl = imagePath
    ? `${AEM_HOST}${imagePath}`
    : "";

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

export function createMagazineCard(article) {
  const title = escapeHtml(
    article.titulo || "Artigo sem título"
  );

  const imageUrl = article.imagem
    ? `${AEM_HOST}${article.imagem}`
    : "";

  const articleUrl = article.link
    ? `${AEM_HOST}${article.link}`
    : "";

  const imageMarkup = imageUrl
    ? `
      <img
        class="magazine-card__image"
        src="${escapeHtml(imageUrl)}"
        alt="${title}"
        loading="lazy"
      >
    `
    : `
      <div
        class="magazine-card__image-placeholder"
        aria-hidden="true"
      >
        WKND
      </div>
    `;

  const content = `
    <div class="magazine-card__image-wrapper">
      ${imageMarkup}
    </div>

    <div class="magazine-card__content">
      <p class="magazine-card__eyebrow">
        WKND Magazine
      </p>

      <h3 class="magazine-card__title">
        ${title}
      </h3>

      <span class="magazine-card__action">
        Ler artigo
      </span>
    </div>
  `;

  if (!articleUrl) {
    return `
      <article class="magazine-card">
        ${content}
      </article>
    `;
  }

  return `
    <article class="magazine-card">
      <a
        class="magazine-card__link"
        href="${escapeHtml(articleUrl)}"
      >
        ${content}
      </a>
    </article>
  `;
}