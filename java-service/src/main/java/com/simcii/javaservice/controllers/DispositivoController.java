package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.services.DispositivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dispositivos")
@CrossOrigin(origins = "*")
public class DispositivoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DispositivoController.class);
    
    @Autowired
    private DispositivoService dispositivoService;
    
    @GetMapping
    public List<Dispositivo> getAllDispositivos() {
        logger.info("Obteniendo todos los dispositivos");
        return dispositivoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Dispositivo> getDispositivoById(@PathVariable Long id) {
        Optional<Dispositivo> dispositivo = dispositivoService.findById(id);
        return dispositivo.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createDispositivo(@RequestBody Dispositivo dispositivo) {
        try {
            logger.info("Creando dispositivo: {}", dispositivo);
            
            // Validaciones básicas
            if (dispositivo.getNombre() == null || dispositivo.getNombre().trim().isEmpty()) {
                logger.warn("Intento de crear dispositivo sin nombre");
                return ResponseEntity.badRequest().body("El nombre es requerido");
            }
            if (dispositivo.getUbicacion() == null || dispositivo.getUbicacion().trim().isEmpty()) {
                logger.warn("Intento de crear dispositivo sin ubicación");
                return ResponseEntity.badRequest().body("La ubicación es requerida");
            }
            
            // El tipo se determina automáticamente por la clase concreta
            // gracias a @JsonTypeInfo, no necesitamos validarlo manualmente
            
            // Asegurar que activo tenga valor por defecto
            if (dispositivo.getActivo() == null) {
                dispositivo.setActivo(true);
            }
            
            Dispositivo savedDispositivo = dispositivoService.save(dispositivo);
            logger.info("Dispositivo creado exitosamente: {}", savedDispositivo.getId());
            
            return ResponseEntity.ok(savedDispositivo);
            
        } catch (Exception e) {
            logger.error("Error al crear dispositivo: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error interno del servidor: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> updateDispositivo(@PathVariable Long id, @RequestBody Dispositivo dispositivoDetails) {
        try {
            logger.info("Actualizando dispositivo ID: {}", id);
            Dispositivo updatedDispositivo = dispositivoService.update(id, dispositivoDetails);
            return ResponseEntity.ok(updatedDispositivo);
        } catch (RuntimeException e) {
            logger.warn("Dispositivo no encontrado para actualizar: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDispositivo(@PathVariable Long id) {
        try {
            logger.info("Eliminando dispositivo ID: {}", id);
            dispositivoService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al eliminar dispositivo: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error al eliminar dispositivo");
        }
    }
}