package fr.ipi.pokemon.controller;

import fr.ipi.pokemon.dto.DresseurDto;
import fr.ipi.pokemon.exception.DresseurNotFoundException;
import fr.ipi.pokemon.service.DresseurService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DresseurController.class)
@Import(ApiExceptionHandler.class)
class DresseurControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DresseurService service;

    @Test
    void should_return_200_and_dto_when_dresseur_exists() throws Exception {
        // Arrange
        when(service.obtenirDresseur(1L)).thenReturn(new DresseurDto("Sacha", 3, 170));

        // Act
        ResultActions resultat = mockMvc.perform(get("/dresseurs/1"));

        // Assert
        resultat.andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nomDresseur").value("Sacha"))
                .andExpect(jsonPath("$.nombrePokemons").value(3))
                .andExpect(jsonPath("$.niveauDresseur").value(170))
                .andExpect(jsonPath("$.pokemons").doesNotExist());
        verify(service).obtenirDresseur(1L);
    }

    @Test
    void should_return_404_when_dresseur_does_not_exist() throws Exception {
        // Arrange
        when(service.obtenirDresseur(999L)).thenThrow(new DresseurNotFoundException(999L));

        // Act
        ResultActions resultat = mockMvc.perform(get("/dresseurs/999"));

        // Assert
        resultat.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("DRESSEUR_INTROUVABLE"))
                .andExpect(jsonPath("$.message").value("Le dresseur 999 n'existe pas."));
        verify(service).obtenirDresseur(999L);
    }

    @Test
    void should_return_level_one_when_dresseur_has_no_pokemon() throws Exception {
        // Arrange
        when(service.obtenirDresseur(3L)).thenReturn(new DresseurDto("Pierre", 0, 1));

        // Act
        ResultActions resultat = mockMvc.perform(get("/dresseurs/3"));

        // Assert
        resultat.andExpect(status().isOk())
                .andExpect(jsonPath("$.nomDresseur").value("Pierre"))
                .andExpect(jsonPath("$.nombrePokemons").value(0))
                .andExpect(jsonPath("$.niveauDresseur").value(1));
    }

    @Test
    void should_return_400_when_id_is_not_a_number() throws Exception {
        // Arrange
        String idInvalide = "abc";

        // Act
        ResultActions resultat = mockMvc.perform(get("/dresseurs/{id}", idInvalide));

        // Assert
        resultat.andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
}
