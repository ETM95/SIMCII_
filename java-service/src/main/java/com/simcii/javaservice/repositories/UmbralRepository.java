package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de entidades Umbral en la base de datos.
 * Proporciona operaciones de acceso a datos para la configuración de umbrales
 * de alerta que definen los rangos operativos aceptables de los dispositivos
 * en el sistema SIMCII.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Repository
 * @see Umbral
 * @see Dispositivo
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface UmbralRepository extends JpaRepository<Umbral, Long> {

    /**
     * Encuentra todos los umbrales asociados a un dispositivo específico.
     * Retorna tanto umbrales activos como inactivos para el dispositivo dado.
     * Útil para administración completa de la configuración de alertas.
     * 
     * @param dispositivo Dispositivo del cual obtener los umbrales
     * @return List<Umbral> con todos los umbrales del dispositivo
     */
    List<Umbral> findByDispositivo(Dispositivo dispositivo);

    /**
     * Encuentra únicamente los umbrales activos asociados a un dispositivo específico.
     * Los umbrales activos son aquellos que actualmente están monitoreando lecturas
     * y generando alertas cuando se violan los rangos configurados.
     * 
     * @param dispositivo Dispositivo del cual obtener los umbrales activos
     * @return List<Umbral> con los umbrales activos del dispositivo
     */
    List<Umbral> findByDispositivoAndActivoTrue(Dispositivo dispositivo);

    /**
     * Encuentra todos los umbrales asociados a un dispositivo por su identificador.
     * Alternativa conveniente que evita cargar primero la entidad Dispositivo
     * cuando solo se necesita el ID para la consulta.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Umbral> con todos los umbrales del dispositivo
     * @implNote Método identificado como necesario para completar funcionalidades
     */
    List<Umbral> findByDispositivoId(Long dispositivoId); // MÉTODO FALTANTE
}
