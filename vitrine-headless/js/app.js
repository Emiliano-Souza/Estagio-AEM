"use strict";

import {
  fetchAdventures,
  fetchMagazineArticles,
} from "./api.js";

import {
  createAdventureCard,
  createMagazineCard,
} from "./components.js";

const adventuresList =
  document.querySelector("#adventures-list");

const statusElement =
  document.querySelector("#status");

const resultsCount =
  document.querySelector("#results-count");

const filterButtons =
  document.querySelectorAll("[data-difficulty]");

const magazineList =
  document.querySelector("#magazine-list");

const magazineStatus =
  document.querySelector("#magazine-status");

const magazineCount =
  document.querySelector("#magazine-count");


function showStatus(element, message, isError = false) {
  element.textContent = message;
  element.hidden = false;
  element.classList.toggle("status--error", isError);
}

function hideStatus(element) {
  element.hidden = true;
  element.classList.remove("status--error");
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
    showStatus(
      statusElement,
      "Nenhuma aventura encontrada para este filtro."
    );

    return;
  }

  hideStatus(statusElement);
}


function renderMagazine(articles) {
  magazineList.innerHTML = articles
    .map(createMagazineCard)
    .join("");

  const total = articles.length;

  magazineCount.textContent =
    total === 1
      ? "1 artigo"
      : `${total} artigos`;

  if (total === 0) {
    showStatus(
      magazineStatus,
      "Nenhum artigo encontrado no Magazine."
    );

    return;
  }

  hideStatus(magazineStatus);
}


async function loadAdventures(difficulty = "") {
  adventuresList.innerHTML = "";
  resultsCount.textContent = "";

  showStatus(
    statusElement,
    "Carregando aventuras..."
  );

  try {
    const adventures =
      await fetchAdventures(difficulty);

    renderAdventures(adventures);
  } catch (error) {
    if (error.name === "AbortError") {
      return;
    }

    console.error(
      "Erro ao carregar aventuras:",
      error
    );

    showStatus(
      statusElement,
      "Não foi possível carregar as aventuras do AEM.",
      true
    );
  }
}


async function loadMagazine() {
  magazineList.innerHTML = "";
  magazineCount.textContent = "";

  showStatus(
    magazineStatus,
    "Carregando artigos..."
  );

  try {
    const articles =
      await fetchMagazineArticles();

    renderMagazine(articles);
  } catch (error) {
    console.error(
      "Erro ao carregar artigos:",
      error
    );

    showStatus(
      magazineStatus,
      "Não foi possível carregar os artigos do Magazine.",
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

    const difficulty =
      button.dataset.difficulty || "";

    loadAdventures(difficulty);
  });
});


loadAdventures();
loadMagazine();