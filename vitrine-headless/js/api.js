import { AEM_HOST, ENDPOINTS } from "./config.js";

let activeRequest = null;

export function buildEndpoint(difficulty = "") {
  if (!difficulty) {
    return `${AEM_HOST}${ENDPOINTS.aventuras}`;
  }

  const value = encodeURIComponent(difficulty);

  return (
    `${AEM_HOST}${ENDPOINTS.aventurasPorDificuldade}` +
    `;dificuldade=${value}`
  );
}

export async function fetchAdventures(difficulty = "") {
  if (activeRequest) {
    activeRequest.abort();
  }

  activeRequest = new AbortController();

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

  return json.data?.aventuraList?.items ?? [];
}