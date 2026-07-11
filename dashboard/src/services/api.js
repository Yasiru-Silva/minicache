const BASE_URL = "http://localhost:8081";

export async function fetchStats() {
  const response = await fetch(`${BASE_URL}/stats`);
  if (!response.ok) throw new Error("Failed to fetch stats");
  return response.json();
}

export async function fetchKeys() {
  const response = await fetch(`${BASE_URL}/keys`);
  if (!response.ok) throw new Error("Failed to fetch keys");
  return response.json();
}