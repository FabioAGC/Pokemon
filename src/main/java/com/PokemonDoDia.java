package com.exemplo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PokemonDoDia {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int TOTAL_POKEMONS = 1025; // até a 9ª geração

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Random random = new Random();

        int id = random.nextInt(TOTAL_POKEMONS) + 1;

        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + id)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                JsonObject json = JsonParser.parseString(body).getAsJsonObject();

                String name = json.get("name").getAsString();
                int height = json.get("height").getAsInt();
                int weight = json.get("weight").getAsInt();
                String type = json.getAsJsonArray("types")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("type")
                        .get("name").getAsString();

                // Nova funcionalidade: primeira habilidade
                String ability = json.getAsJsonArray("abilities")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("ability")
                        .get("name").getAsString();

                // Calcular força do Pokémon
                int forca = (height * weight) / 10;

                String resultado = String.format(
                        "=== POKÉMON DO DIA ===\n" +
                        "ID: %d\nNome: %s\nTipo: %s\nAltura: %d\nPeso: %d\nHabilidade: %s\nForça: %d",
                        id, name, type, height, weight, ability, forca
                );

                System.out.println(resultado);

            } else {
                System.out.println("Não foi possível buscar o Pokémon (status: " + response.code() + ")");
            }
        } catch (IOException e) {
            System.out.println("Erro na requisição: " + e.getMessage());
        }
    }
}