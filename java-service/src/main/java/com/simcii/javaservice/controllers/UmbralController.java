package com.simcii.javaservice.controllers;

import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.services.UmbralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de umbrales de alerta en el sistema SIMCII.
 * Expone operaciones CRUD para configurar, consultar y administrar los umbrales
 * que definen los rangos operativos aceptables para los dispositivos del invernadero.
 * Los umbrales son críticos para la generación automática de alertas del sistema.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @RestController
 * @RequestMapping("/api/umbrales")
 * @see Umbral
 * @see UmbralService
 */
@RestController
@RequestMapping("/api/umbrales")
public class UmbralController {
    
    /**
     * Servicio de negocio para la gestión de umbrales.
     * Inyectado automáticamente por Spring Framework.
     */
    @Autowired
    private UmbralService umbralService;
    
    /**
     * Crea un nuevo umbral de alerta en el sistema.
     * Establece los rangos de valores aceptables para un dispositivo específico
     * que, al ser violados, generarán alertas automáticas en el sistema.
     * 
     * @param umbral Objeto Umbral con la configuración completa del umbral
     * @return ResponseEntity<Umbral> con el umbral creado y su ID asignado
     * @HTTP 200 Éxito, umbral creado correctamente
     * @HTTP 400 Datos del umbral inválidos o inconsistentes
     * @see UmbralService#guardarUmbral(Umbral)
     */
    @PostMapping
    public ResponseEntity<Umbral> crearUmbral(@RequestBody Umbral umbral) {
        Umbral umbralGuardado = umbralService.guardarUmbral(umbral);
        return ResponseEntity.ok(umbralGuardado);
    }
    
    /**
     * Obtiene todos los umbrales configurados para un dispositivo específico.
     * Retorna la lista de rangos de alerta definidos para monitorear un dispositivo
     * particular en las diferentes zonas del invernadero.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return ResponseEntity<List<Umbral>> con la lista de umbrales del dispositivo
     * @HTTP 200 Éxito, devuelve la lista de umbrales (puede estar vacía)
     * @HTTP 404 Dispositivo no encontrado
     * @implNote Actualmente retorna una lista vacía - pendiente de implementación completa
     */
    @GetMapping("/dispositivo/{dispositivoId}")
    public ResponseEntity<List<Umbral>> obtenerUmbralesPorDispositivo(@PathVariable Long dispositivoId) {
        // Por ahora retornamos una respuesta simple
        // En una implementación completa, buscarías el dispositivo y luego los umbrales
        return ResponseEntity.ok(List.of());
    }
    
    /**
     * Actualiza la configuración de un umbral existente.
     * Permite modificar los valores mínimo, máximo, tipo de alerta y estado
     * de un umbral previamente configurado en el sistema.
     * 
     * @param id Identificador único del umbral a actualizar
     * @param umbral Objeto Umbral con los nuevos datos de configuración
     * @return ResponseEntity<Umbral> con el umbral actualizado
     * @HTTP 200 Éxito, umbral actualizado correctamente
     * @HTTP 404 No se encontró el umbral con el ID especificado
     * @HTTP 400 Datos de actualización inválidos
     * @see UmbralService#guardarUmbral(Umbral)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Umbral> actualizarUmbral(@PathVariable Long id, @RequestBody Umbral umbral) {
        umbral.setId(id);
        Umbral umbralActualizado = umbralService.guardarUmbral(umbral);
        return ResponseEntity.ok(umbralActualizado);
    }
    
    /**
     * Desactiva un umbral específico sin eliminarlo permanentemente.
     * El umbral permanece en la base de datos pero deja de generar alertas,
     * permitiendo su reactivación posterior si es necesario.
     * 
     * @param id Identificador único del umbral a desactivar
     * @return ResponseEntity<String> con confirmación de desactivación
     * @HTTP 200 Éxito, umbral desactivado correctamente
     * @HTTP 404 No se encontró el umbral con el ID especificado
     * @see UmbralService#desactivarUmbral(Long)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> desactivarUmbral(@PathVariable Long id) {
        umbralService.desactivarUmbral(id);
        return ResponseEntity.ok("Umbral desactivado correctamente");
    }
    
    /**
     * Obtiene un umbral específico por su identificador único.
     * 
     * @param id Identificador único del umbral a consultar
     * @return ResponseEntity<Umbral> con el umbral encontrado o error 404
     * @HTTP 200 Éxito, devuelve el umbral solicitado
     * @HTTP 404 No se encontró el umbral con el ID especificado
     * @see UmbralService#obtenerUmbralPorId(Long)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Umbral> obtenerUmbralPorId(@PathVariable Long id) {
        Umbral umbral = umbralService.obtenerUmbralPorId(id);
        if (umbral != null) {
            return ResponseEntity.ok(umbral);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}