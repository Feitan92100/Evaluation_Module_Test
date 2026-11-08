package fr.ipi.pokemon.service;

import fr.ipi.pokemon.dto.DresseurDto;
import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import fr.ipi.pokemon.exception.DresseurNotFoundException;
import fr.ipi.pokemon.repository.DresseurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DresseurServiceTest {
    @Mock
    private DresseurRepository repository;

    @InjectMocks
    private DresseurService service;

    @Test
    void should_sum_levels_when_no_bonus_applies() {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Stari", "Eau", 15),
                new Pokemon("Psykokwak", "Eau", 25));
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(40, niveau);
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_return_one_when_dresseur_has_no_pokemon() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(equipe()));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(1, niveau);
    }

    @ParameterizedTest(name = "niveau {0} -> score {1}")
    @CsvSource({"49, 49", "50, 60", "51, 61"})
    void should_apply_elite_bonus_when_level_reaches_fifty(int niveauPokemon, int attendu) {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Pikachu", "Électrik", niveauPokemon));
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(attendu, niveau);
    }

    @ParameterizedTest(name = "{0} types distincts -> score {1}")
    @CsvSource({"2, 20", "3, 50", "4, 60"})
    void should_apply_diversity_bonus_once_when_at_least_three_types(int nombreTypes, int attendu) {
        // Arrange
        String[] types = {"Eau", "Feu", "Plante", "Électrik"};
        Dresseur dresseur = equipe();
        for (int i = 0; i < nombreTypes; i++) {
            dresseur.ajouterPokemon(new Pokemon("Pokemon " + i, types[i], 10));
        }
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(attendu, niveau);
    }

    @Test
    void should_not_apply_diversity_bonus_when_three_pokemons_have_two_types() {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Stari", "Eau", 10),
                new Pokemon("Carapuce", "Eau", 20), new Pokemon("Salamèche", "Feu", 30));
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(60, niveau);
    }

    @Test
    void should_add_ten_per_elite_when_multiple_pokemons_reach_fifty() {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Salamèche", "Feu", 50),
                new Pokemon("Dracaufeu", "Feu", 60));
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(130, niveau); // 50 + 60 + 10 + 10
    }

    @Test
    void should_combine_bonuses_when_diversity_and_elite_conditions_are_met() {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Pikachu", "Électrik", 50),
                new Pokemon("Dracaufeu", "Feu", 60), new Pokemon("Carapuce", "Eau", 20));
        when(repository.findById(1L)).thenReturn(Optional.of(dresseur));

        // Act
        int niveau = service.calculerNiveauDresseur(1L);

        // Assert
        assertEquals(170, niveau); // 130 de base + 20 diversité + 20 élite
    }

    @Test
    void should_throw_exception_when_calculating_unknown_dresseur() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act
        DresseurNotFoundException exception = assertThrows(DresseurNotFoundException.class,
                () -> service.calculerNiveauDresseur(999L));

        // Assert
        assertEquals("Le dresseur 999 n'existe pas.", exception.getMessage());
        verify(repository).findById(999L);
    }

    @Test
    void should_build_dto_when_dresseur_exists() {
        // Arrange
        Dresseur dresseur = equipe(new Pokemon("Pikachu", "Électrik", 50));
        when(repository.findById(7L)).thenReturn(Optional.of(dresseur));

        // Act
        DresseurDto dto = service.obtenirDresseur(7L);

        // Assert
        assertEquals(new DresseurDto("Sacha", 1, 60), dto);
        verify(repository).findById(7L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void should_build_empty_team_dto_when_dresseur_has_no_pokemon() {
        // Arrange
        when(repository.findById(3L)).thenReturn(Optional.of(equipe()));

        // Act
        DresseurDto dto = service.obtenirDresseur(3L);

        // Assert
        assertEquals(new DresseurDto("Sacha", 0, 1), dto);
    }

    @Test
    void should_throw_exception_when_requesting_unknown_dresseur_dto() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act
        DresseurNotFoundException exception = assertThrows(DresseurNotFoundException.class,
                () -> service.obtenirDresseur(999L));

        // Assert
        assertEquals("Le dresseur 999 n'existe pas.", exception.getMessage());
    }

    private Dresseur equipe(Pokemon... pokemons) {
        Dresseur dresseur = new Dresseur("Sacha", "Kanto");
        for (Pokemon pokemon : pokemons) {
            dresseur.ajouterPokemon(pokemon);
        }
        return dresseur;
    }
}
