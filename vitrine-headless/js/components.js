import { AEM_HOST, DIFFICULTY_LABELS } from "./config.js";

/**
 * Impede que textos recebidos da API sejam interpretados como HTML.
 */
function escapeHtml(value = "") {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

/**
 * Formata o preço no padrão brasileiro.
 */
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

/**
 * Transforma uma aventura recebida do AEM em um card HTML.
 */
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