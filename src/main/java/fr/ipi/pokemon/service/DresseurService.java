package fr.ipi.pokemon.service;

import fr.ipi.pokemon.dto.DresseurDto;
import fr.ipi.pokemon.entity.Dresseur;
import fr.ipi.pokemon.entity.Pokemon;
import fr.ipi.pokemon.exception.DresseurNotFoundException;
import fr.ipi.pokemon.repository.DresseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class DresseurService {
    private final DresseurRepository repository;

    public DresseurService(DresseurRepository repository) {
        this.repository = repository;
    }

    public int calculerNiveauDresseur(Long dresseurId) {
        return calculerNiveau(chargerDresseur(dresseurId));
    }

    public DresseurDto obtenirDresseur(Long dresseurId) {
        Dresseur dresseur = chargerDresseur(dresseurId);
        return new DresseurDto(dresseur.getNom(), dresseur.getPokemons().size(),
                calculerNiveau(dresseur));
    }

    private Dresseur chargerDresseur(Long id) {
        return repository.findById(id).orElseThrow(() -> new DresseurNotFoundException(id));
    }

    private int calculerNiveau(Dresseur dresseur) {
        if (dresseur.getPokemons().isEmpty()) {
            return 1;
        }

        int niveau = 0;
        Set<String> types = new HashSet<>();
        for (Pokemon pokemon : dresseur.getPokemons()) {
            niveau += pokemon.getNiveau();
            types.add(pokemon.getType());
            if (pokemon.getNiveau() >= 50) {
                niveau += 10;
            }
        }
        if (types.size() >= 3) {
            niveau += 20;
        }
        return niveau;
    }
}
