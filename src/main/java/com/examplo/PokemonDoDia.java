package com.examplo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Random;

public class PokemonDoDia {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Random random = new Random();
        int id = random.nextInt(1025) + 1;

        try {
            String pokemonData = fetchPokemonData(id, client);
            String result = formatPokemonData(id, pokemonData);
            System.out.println(result);
        } catch (IOException e) {
            System.out.println("Erro na requisição ou no processamento: " + e.getMessage());
        }
    }

    public static String fetchPokemonData(int id, OkHttpClient client) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Não foi possível buscar o Pokémon (status: " + response.code() + ")");
            }
        }
    }

    public static String formatPokemonData(int id, String jsonData) {
        JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();
        String name = json.get("name").getAsString();
        int height = json.get("height").getAsInt();
        int weight = json.get("weight").getAsInt();
        String type = json.getAsJsonArray("types")
                .get(0).getAsJsonObject()
                .getAsJsonObject("type")
                .get("name").getAsString();

        return String.format(
                "=== POKÉMON DO DIA ===\n" +
                        "ID: %d\nNome: %s\nTipo: %s\nAltura: %d\nPeso: %d",
                id, name, type, height, weight
        );
    }
}