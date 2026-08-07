"use strict";

import { fetchAdventures } from "./api.js";
import { createAdventureCard } from "./components.js";

const adventuresList = document.querySelector("#adventures-list");
const statusElement = document.querySelector("#status");
const resultsCount = document.querySelector("#results-count");
const filterButtons = document.querySelectorAll("[data-difficulty]");

function showStatus(message, isError = false) {
  statusElement.textContent = message;
  statusElement.hidden = false;
  statusElement.classList.toggle("status--error", isError);
}

function hideStatus() {
  statusElement.hidden = true;
  statusElement.classList.remove("status--error");
}

function getDifficultyFromUrl() {
  const params =
    new URLSearchParams(window.location.search);

  const difficulty =
    params.get("dificuldade") || "";

  const validDifficulties =
    Array.from(filterButtons).map(
      (button) => button.dataset.difficulty || ""
    );

  return validDifficulties.includes(difficulty)
    ? difficulty
    : "";
}

function updateUrl(difficulty = "") {
  const url = new URL(window.location.href);

  if (difficulty) {
    url.searchParams.set(
      "dificuldade",
      difficulty
    );
  } else {
    url.searchParams.delete("dificuldade");
  }

  window.history.pushState(
    {},
    "",
    url
  );
}

function setActiveFilter(difficulty = "") {
  filterButtons.forEach((button) => {
    const buttonDifficulty =
      button.dataset.difficulty || "";

    button.classList.toggle(
      "is-active",
      buttonDifficulty === difficulty
    );
  });
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
      "Nenhuma aventura encontrada para este filtro."
    );

    return;
  }

  hideStatus();
}

async function loadAdventures(difficulty = "") {
  adventuresList.innerHTML = "";
  resultsCount.textContent = "";

  showStatus("Carregando aventuras...");

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
      "Não foi possível carregar as aventuras do AEM.",
      true
    );
  }
}

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const difficulty =
      button.dataset.difficulty || "";

    setActiveFilter(difficulty);
    updateUrl(difficulty);
    loadAdventures(difficulty);
  });
});

window.addEventListener("popstate", () => {
  const difficulty =
    getDifficultyFromUrl();

  setActiveFilter(difficulty);
  loadAdventures(difficulty);
});

const initialDifficulty =
  getDifficultyFromUrl();

setActiveFilter(initialDifficulty);
loadAdventures(initialDifficulty);
