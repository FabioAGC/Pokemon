package com.exemplo;

import com.examplo.PokemonDoDia;
import com.exemplo.PokemonDoDiaTest;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PokemonDoDiaTest {

    // Dados de teste em formato JSON
    private static final String JSON_VALIDO_SIMPLES = "{\"name\":\"bulbasaur\",\"height\":7,\"weight\":69,\"types\":[{\"type\":{\"name\":\"grass\"}}]}";
    private static final String JSON_VALIDO_DUPLO_TIPO = "{\"name\":\"charizard\",\"height\":17,\"weight\":905,\"types\":[{\"type\":{\"name\":\"fire\"}},{\"type\":{\"name\":\"flying\"}}]}";
    private static final String JSON_SEM_TIPOS = "{\"name\":\"ditto\",\"height\":3,\"weight\":40,\"types\":[]}";
    private static final String JSON_SEM_NOME = "{\"height\":4,\"weight\":60,\"types\":[{\"type\":{\"name\":\"electric\"}}]}";
    private static final String JSON_MALFORMADO = "{\"name\":\"pikachu\", \"height\":4";

    // --- Testes Positivos (10 Assertions) ---

    @Test
    @DisplayName("1. Deve formatar dados de um Pokémon de um tipo")
    void testFormatPokemonData_SingleType() {
        String expected = "=== POKÉMON DO DIA ===\nID: 1\nNome: bulbasaur\nTipo: grass\nAltura: 7\nPeso: 69";
        String actual = PokemonDoDia.formatPokemonData(1, JSON_VALIDO_SIMPLES);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("2. Deve formatar dados de um Pokémon de dois tipos, pegando o primeiro")
    void testFormatPokemonData_DualType() {
        String expected = "=== POKÉMON DO DIA ===\nID: 6\nNome: charizard\nTipo: fire\nAltura: 17\nPeso: 905";
        String actual = PokemonDoDia.formatPokemonData(6, JSON_VALIDO_DUPLO_TIPO);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("3. Deve formatar corretamente os dados com ID 25")
    void testFormatPokemonData_Id25() {
        String json = "{\"name\":\"pikachu\",\"height\":4,\"weight\":60,\"types\":[{\"type\":{\"name\":\"electric\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 25\nNome: pikachu\nTipo: electric\nAltura: 4\nPeso: 60";
        String actual = PokemonDoDia.formatPokemonData(25, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("4. Deve formatar corretamente os dados com ID 150")
    void testFormatPokemonData_Id150() {
        String json = "{\"name\":\"mewtwo\",\"height\":20,\"weight\":1220,\"types\":[{\"type\":{\"name\":\"psychic\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 150\nNome: mewtwo\nTipo: psychic\nAltura: 20\nPeso: 1220";
        String actual = PokemonDoDia.formatPokemonData(150, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("5. Deve formatar corretamente os dados com altura e peso altos")
    void testFormatPokemonData_HighValues() {
        String json = "{\"name\":\"wailord\",\"height\":145,\"weight\":3980,\"types\":[{\"type\":{\"name\":\"water\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 321\nNome: wailord\nTipo: water\nAltura: 145\nPeso: 3980";
        String actual = PokemonDoDia.formatPokemonData(321, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("6. Deve formatar corretamente os dados com ID e nome longos")
    void testFormatPokemonData_LongName() {
        String json = "{\"name\":\"alcremie-rainbow-swirl\",\"height\":3,\"weight\":5,\"types\":[{\"type\":{\"name\":\"fairy\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 869\nNome: alcremie-rainbow-swirl\nTipo: fairy\nAltura: 3\nPeso: 5";
        String actual = PokemonDoDia.formatPokemonData(869, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("7. Deve formatar corretamente os dados com valores zero")
    void testFormatPokemonData_ZeroValues() {
        String json = "{\"name\":\"shedinja\",\"height\":8,\"weight\":12,\"types\":[{\"type\":{\"name\":\"bug\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 292\nNome: shedinja\nTipo: bug\nAltura: 8\nPeso: 12";
        String actual = PokemonDoDia.formatPokemonData(292, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("8. Deve formatar os dados com valores pequenos")
    void testFormatPokemonData_SmallValues() {
        String json = "{\"name\":\"flabebe\",\"height\":1,\"weight\":1,\"types\":[{\"type\":{\"name\":\"fairy\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 669\nNome: flabebe\nTipo: fairy\nAltura: 1\nPeso: 1";
        String actual = PokemonDoDia.formatPokemonData(669, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("9. Deve formatar com nome de tipo longo")
    void testFormatPokemonData_LongTypeName() {
        String json = "{\"name\":\"arceus\",\"height\":32,\"weight\":3200,\"types\":[{\"type\":{\"name\":\"normal\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 493\nNome: arceus\nTipo: normal\nAltura: 32\nPeso: 3200";
        String actual = PokemonDoDia.formatPokemonData(493, json);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("10. Deve formatar dados de um Pokémon da última geração")
    void testFormatPokemonData_LastGen() {
        String json = "{\"name\":\"tatsugiri\",\"height\":3,\"weight\":80,\"types\":[{\"type\":{\"name\":\"dragon\"}},{\"type\":{\"name\":\"water\"}}]}";
        String expected = "=== POKÉMON DO DIA ===\nID: 979\nNome: tatsugiri\nTipo: dragon\nAltura: 3\nPeso: 80";
        String actual = PokemonDoDia.formatPokemonData(979, json);
        assertEquals(expected, actual);
    }

    // --- Testes Negativos (10 Assertions) ---

    @Test
    @DisplayName("11. Deve lançar exceção se a string JSON for malformada")
    void testFormatPokemonData_MalformedJson() {
        assertThrows(JsonSyntaxException.class, () -> {
            PokemonDoDia.formatPokemonData(10, JSON_MALFORMADO);
        });
    }

    @Test
    @DisplayName("12. Deve lançar exceção se a string JSON for nula")
    void testFormatPokemonData_NullJson() {
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(1, null);
        });
    }

    @Test
    @DisplayName("13. Deve lançar exceção se 'name' estiver faltando")
    void testFormatPokemonData_MissingName() {
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(25, JSON_SEM_NOME);
        });
    }

    @Test
    @DisplayName("14. Deve lançar exceção se 'height' estiver faltando")
    void testFormatPokemonData_MissingHeight() {
        String json = "{\"name\":\"pikachu\",\"weight\":60,\"types\":[{\"type\":{\"name\":\"electric\"}}]}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(25, json);
        });
    }

    @Test
    @DisplayName("15. Deve lançar exceção se 'weight' estiver faltando")
    void testFormatPokemonData_MissingWeight() {
        String json = "{\"name\":\"pikachu\",\"height\":4,\"types\":[{\"type\":{\"name\":\"electric\"}}]}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(25, json);
        });
    }

    @Test
    @DisplayName("16. Deve lançar exceção se 'types' for um array vazio")
    void testFormatPokemonData_EmptyTypesArray() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            PokemonDoDia.formatPokemonData(132, JSON_SEM_TIPOS);
        });
    }

    @Test
    @DisplayName("17. Deve lançar exceção se 'types' for nulo")
    void testFormatPokemonData_NullTypes() {
        String json = "{\"name\":\"ditto\",\"height\":3,\"weight\":40,\"types\":null}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(132, json);
        });
    }

    @Test
    @DisplayName("18. Deve lançar exceção se 'type' estiver faltando dentro do array de tipos")
    void testFormatPokemonData_MissingTypeObject() {
        String json = "{\"name\":\"bulbasaur\",\"height\":7,\"weight\":69,\"types\":[{\"type\":null}]}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(1, json);
        });
    }

    @Test
    @DisplayName("19. Deve lançar exceção se 'name' estiver faltando no objeto 'type'")
    void testFormatPokemonData_MissingTypeName() {
        String json = "{\"name\":\"bulbasaur\",\"height\":7,\"weight\":69,\"types\":[{\"type\":{\"url\":\"...\"}}]}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(1, json);
        });
    }

    @Test
    @DisplayName("20. Deve lançar exceção se o JSON estiver completamente vazio")
    void testFormatPokemonData_EmptyJson() {
        String json = "{}";
        assertThrows(NullPointerException.class, () -> {
            PokemonDoDia.formatPokemonData(1, json);
        });
    }
}