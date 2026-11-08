package fr.ipi.pokemon.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "dresseur")
public class Dresseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String region;

    @OneToMany(mappedBy = "dresseur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pokemon> pokemons = new ArrayList<>();

    protected Dresseur() {
    }

    public Dresseur(String nom, String region) {
        this.nom = nom;
        this.region = region;
    }

    public void ajouterPokemon(Pokemon pokemon) {
        pokemons.add(pokemon);
        pokemon.setDresseur(this);
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getRegion() { return region; }
    public List<Pokemon> getPokemons() { return Collections.unmodifiableList(pokemons); }
}
