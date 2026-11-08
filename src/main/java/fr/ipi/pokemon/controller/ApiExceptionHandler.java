package fr.ipi.pokemon.controller;

import fr.ipi.pokemon.exception.DresseurNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ErreurDto(String code, String message) { }

    @ExceptionHandler(DresseurNotFoundException.class)
    public ResponseEntity<ErreurDto> traiterDresseurAbsent(DresseurNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErreurDto("DRESSEUR_INTROUVABLE", exception.getMessage()));
    }
}
