package com.exemplo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Random;

public class PokemonDoDia {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int TOTAL_POKEMONS = 1025; // até a 9ª geração

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        Random random = new Random();

        int id1 = random.nextInt(TOTAL_POKEMONS) + 1;
        int id2 = random.nextInt(TOTAL_POKEMONS) + 1;

        try {
            JsonObject pokemon1 = buscarPokemon(client, id1);
            JsonObject pokemon2 = buscarPokemon(client, id2);

            if (pokemon1 != null && pokemon2 != null) {
                exibirPokemon(pokemon1, id1);
                exibirPokemon(pokemon2, id2);

                compararPokemons(pokemon1, id1, pokemon2, id2);
            }
        } catch (IOException e) {
            System.out.println("Erro na requisição: " + e.getMessage());
        }
    }

    private static JsonObject buscarPokemon(OkHttpClient client, int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + id)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            String body = response.body().string();
            return JsonParser.parseString(body).getAsJsonObject();
        } else {
            System.out.println("Não foi possível buscar o Pokémon (ID: " + id + ", status: " + response.code() + ")");
            return null;
        }
    }

    private static void exibirResultadoDoPokemon(JsonObject pokemon, int id) {
        String name = pokemon.get("name").getAsString();
        int height = pokemon.get("height").getAsInt();
        int weight = pokemon.get("weight").getAsInt();
        String type = pokemon.getAsJsonArray("types")
                .get(0).getAsJsonObject()
                .getAsJsonObject("type")
                .get("name").getAsString();
        String ability = pokemon.getAsJsonArray("abilities")
                .get(0).getAsJsonObject()
                .getAsJsonObject("ability")
                .get("name").getAsString();
        int forca = calcularForca(height, weight);

        String resultado = String.format(
                "=== POKÉMON ===\n" +
                "ID: %d\nNome: %s\nTipo: %s\nAltura: %d\nPeso: %d\nHabilidade: %s\nForça: %d",
                id, name, type, height, weight, ability, forca
        );

        System.out.println(resultado);
    }

    private static void compararPokemons(JsonObject pokemon1, int id1, JsonObject pokemon2, int id2) {
        int forca1 = calcularForca(pokemon1.get("height").getAsInt(), pokemon1.get("weight").getAsInt());
        int forca2 = calcularForca(pokemon2.get("height").getAsInt(), pokemon2.get("weight").getAsInt());

        System.out.println("\n=== COMPARAÇÃO DE POKÉMONS ===");
        if (forca1 > forca2) {
            System.out.printf("O Pokémon com ID %d é mais forte que o Pokémon com ID %d (Força: %d vs %d)\n", id1, id2, forca1, forca2);
        } else if (forca2 > forca1) {
            System.out.printf("O Pokémon com ID %d é mais forte que o Pokémon com ID %d (Força: %d vs %d)\n", id2, id1, forca2, forca1);
        } else {
            System.out.printf("Os Pokémon com ID %d e ID %d têm a mesma força (Força: %d)\n", id1, id2, forca1);
        }
    }

    private static int calcularForca(int height, int weight) {
        return (height * weight) / 10;
    }
}