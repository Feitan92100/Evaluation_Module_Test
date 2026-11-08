package fr.ipi.pokemon.repository;

import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DresseurRepositoryTest {
    @Autowired
    private DresseurRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void should_load_seeded_dresseur_when_id_exists() {
        // Arrange
        Long id = 1L;

        // Act
        Dresseur dresseur = repository.findById(id).orElseThrow();

        // Assert
        assertEquals(1L, dresseur.getId());
        assertEquals("Sacha", dresseur.getNom());
        assertEquals("Kanto", dresseur.getRegion());
        assertEquals(3, repository.count());
    }

    @Test
    void should_load_related_pokemons_when_dresseur_has_a_team() {
        // Arrange
        Long id = 1L;

        // Act
        Dresseur dresseur = repository.findById(id).orElseThrow();

        // Assert
        assertEquals(3, dresseur.getPokemons().size());
        Pokemon pikachu = dresseur.getPokemons().stream()
                .filter(pokemon -> pokemon.getNom().equals("Pikachu")).findFirst().orElseThrow();
        assertEquals("Électrik", pikachu.getType());
        assertEquals(50, pikachu.getNiveau());
        assertEquals(1L, pikachu.getId());
        assertEquals(id, pikachu.getDresseur().getId());
    }

    @Test
    void should_load_empty_team_when_dresseur_has_no_pokemon() {
        // Arrange
        Long id = 3L;

        // Act
        Dresseur dresseur = repository.findById(id).orElseThrow();

        // Assert
        assertEquals("Pierre", dresseur.getNom());
        assertEquals(0, dresseur.getPokemons().size());
    }

    @Test
    void should_return_empty_when_id_does_not_exist() {
        // Arrange
        Long id = 999L;

        // Act
        Optional<Dresseur> dresseur = repository.findById(id);

        // Assert
        assertEquals(Optional.empty(), dresseur);
    }

    @Test
    void should_persist_team_when_saving_new_dresseur() {
        // Arrange
        Dresseur nouveau = new Dresseur("Flora", "Hoenn");
        nouveau.ajouterPokemon(new Pokemon("Poussifeu", "Feu", 12));

        // Act
        Long id = repository.saveAndFlush(nouveau).getId();
        entityManager.clear(); // Force une vraie relecture SQL, hors du cache JPA.
        Dresseur recharge = repository.findById(id).orElseThrow();

        // Assert
        assertNotNull(id);
        assertEquals("Flora", recharge.getNom());
        assertEquals("Hoenn", recharge.getRegion());
        assertEquals(1, recharge.getPokemons().size());
        Pokemon pokemon = recharge.getPokemons().get(0);
        assertNotNull(pokemon.getId());
        assertEquals("Poussifeu", pokemon.getNom());
        assertEquals("Feu", pokemon.getType());
        assertEquals(12, pokemon.getNiveau());
        assertEquals(id, pokemon.getDresseur().getId());
    }

    @Test
    void should_delete_owned_pokemons_when_deleting_dresseur() {
        // Arrange
        Dresseur dresseur = repository.findById(1L).orElseThrow();

        // Act
        repository.delete(dresseur);
        repository.flush();
        entityManager.clear();

        // Assert
        assertEquals(Optional.empty(), repository.findById(1L));
        assertEquals(0L, entityManager.createQuery(
                "select count(p) from Pokemon p where p.dresseur.id = :id", Long.class)
                .setParameter("id", 1L).getSingleResult());
        assertEquals(2, repository.findById(2L).orElseThrow().getPokemons().size());
    }
}
