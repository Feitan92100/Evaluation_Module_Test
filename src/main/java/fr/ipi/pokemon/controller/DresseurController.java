package fr.ipi.pokemon.controller;

import fr.ipi.pokemon.dto.DresseurDto;
import fr.ipi.pokemon.service.DresseurService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dresseurs")
public class DresseurController {
    private final DresseurService service;

    public DresseurController(DresseurService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public DresseurDto obtenirDresseur(@PathVariable Long id) {
        return service.obtenirDresseur(id);
    }
}
