package com.juegos1000tres.juegos1000tres_backend.juegos.SpaceInvaders.pruebas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/pruebas/space-invaders")
public class SpaceInvadersPruebaController {

    private final SpaceInvadersPruebaService service;

    public SpaceInvadersPruebaController(SpaceInvadersPruebaService service) {
        this.service = service;
    }

    @GetMapping("/score")
    public ResponseEntity<?> getScore() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("scores", this.service.obtenerScoresOrdenados());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/updates")
    public ResponseEntity<?> getUpdates(
            @RequestParam(value = "playerId", required = false) String playerId,
            @RequestParam(value = "screenId", required = false) String screenId) {
        try {
            Optional<Map<String, Object>> payload;
            if (screenId != null && !screenId.isBlank()) {
                payload = this.service.obtenerSiguienteActualizacionPantalla(screenId);
            } else {
                payload = this.service.obtenerSiguienteActualizacion(playerId);
            }

            return payload.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(errorBody(ex.getMessage()));
        }
    }

    @PostMapping("/event")
    public ResponseEntity<?> procesarEvento(@RequestBody(required = false) Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(this.service.procesarEvento(payload));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(errorBody(ex.getMessage()));
        }
    }

    private Map<String, Object> errorBody(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "error");
        body.put("message", message);
        return body;
    }
}
