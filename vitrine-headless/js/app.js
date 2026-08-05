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
  adventuresList.innerHTML = "";
  resultsCount.textContent = "";

  showStatus("Carregando aventuras...");

  try {
    const adventures = await fetchAdventures(difficulty);

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