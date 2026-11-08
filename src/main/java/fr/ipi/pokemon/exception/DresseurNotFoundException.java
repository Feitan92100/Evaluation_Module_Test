package fr.ipi.pokemon.exception;

public class DresseurNotFoundException extends RuntimeException {
    public DresseurNotFoundException(Long id) {
        super("Le dresseur " + id + " n'existe pas.");
    }
}
