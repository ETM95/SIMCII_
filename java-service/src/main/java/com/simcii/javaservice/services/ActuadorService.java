package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Actuador;
import com.simcii.javaservice.repositories.DispositivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de negocio para la gestión de actuadores en el sistema SIMCII.
 * Proporciona la lógica de negocio para operaciones con actuadores como
 * sistemas de riego, ventilación e iluminación, incluyendo control de estado
 * y modos de operación.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Actuador
 * @see DispositivoRepository
 */
@Service
public class ActuadorService {
    
    /**
     * Repositorio para acceso a datos de dispositivos.
     * Utilizado para operaciones CRUD y consultas específicas de actuadores.
     */
    @Autowired
    private DispositivoRepository dispositivoRepository;
    
    /**
     * Obtiene todos los actuadores registrados en el sistema.
     * Utiliza el repositorio de dispositivos y filtra por tipo Actuador
     * para retornar una lista tipada de actuadores.
     * 
     * @return List<Actuador> con todos los actuadores del sistema
     * @see DispositivoRepository#findAllActuadores()
     */
    public List<Actuador> findAllActuadores() {
        return dispositivoRepository.findAllActuadores().stream()
                .map(dispositivo -> (Actuador) dispositivo)
                .toList();
    }
    
    /**
     * Busca un actuador específico por su identificador único.
     * 
     * @param id Identificador único del actuador a buscar
     * @return Optional<Actuador> con el actuador encontrado o vacío si no existe
     *         o si el dispositivo no es un actuador
     * @see DispositivoRepository#findById(Long)
     */
    public Optional<Actuador> findActuadorById(Long id) {
        return dispositivoRepository.findById(id)
                .filter(dispositivo -> dispositivo instanceof Actuador)
                .map(dispositivo -> (Actuador) dispositivo);
    }
    
    /**
     * Activa un actuador específico cambiando su estado a activo.
     * Inicia la operación del actuador (ej: encender sistema de riego).
     * 
     * @param id Identificador único del actuador a activar
     * @return Actuador el actuador actualizado con estado activo
     * @throws RuntimeException si no se encuentra el actuador con el ID especificado
     * @see #cambiarEstadoActuador(Long, boolean)
     */
    public Actuador activarActuador(Long id) {
        return cambiarEstadoActuador(id, true);
    }
    
    /**
     * Desactiva un actuador específico cambiando su estado a inactivo.
     * Detiene la operación del actuador (ej: apagar sistema de riego).
     * 
     * @param id Identificador único del actuador a desactivar
     * @return Actuador el actuador actualizado con estado inactivo
     * @throws RuntimeException si no se encuentra el actuador con el ID especificado
     * @see #cambiarEstadoActuador(Long, boolean)
     */
    public Actuador desactivarActuador(Long id) {
        return cambiarEstadoActuador(id, false);
    }
    
    /**
     * Método privado para cambiar el estado de un actuador.
     * Reutiliza la lógica común para activar/desactivar actuadores.
     * 
     * @param id Identificador único del actuador
     * @param estado Nuevo estado del actuador (true = activo, false = inactivo)
     * @return Actuador el actuador actualizado
     * @throws RuntimeException si no se encuentra el actuador con el ID especificado
     */
    private Actuador cambiarEstadoActuador(Long id, boolean estado) {
        return findActuadorById(id)
                .map(actuador -> {
                    actuador.setEstado(estado);
                    return (Actuador) dispositivoRepository.save(actuador);
                })
                .orElseThrow(() -> new RuntimeException("Actuador no encontrado"));
    }
    
    /**
     * Cambia el modo de operación de un actuador específico.
     * Permite configurar modos como AUTOMÁTICO, MANUAL, PROGRAMADO, etc.
     * 
     * @param id Identificador único del actuador a modificar
     * @param modo Nuevo modo de operación para el actuador
     * @return Actuador el actuador actualizado con el nuevo modo
     * @throws RuntimeException si no se encuentra el actuador con el ID especificado
     */
    public Actuador cambiarModoOperacion(Long id, String modo) {
        return findActuadorById(id)
                .map(actuador -> {
                    actuador.setModoOperacion(modo);
                    return (Actuador) dispositivoRepository.save(actuador);
                })
                .orElseThrow(() -> new RuntimeException("Actuador no encontrado"));
    }
}