package fr.ipi.pokemon.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pokemon")
public class Pokemon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int niveau;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dresseur_id", nullable = false)
    private Dresseur dresseur;

    protected Pokemon() {
    }

    public Pokemon(String nom, String type, int niveau) {
        this.nom = nom;
        this.type = type;
        this.niveau = niveau;
    }

    void setDresseur(Dresseur dresseur) { this.dresseur = dresseur; }
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getType() { return type; }
    public int getNiveau() { return niveau; }
    public Dresseur getDresseur() { return dresseur; }
}
