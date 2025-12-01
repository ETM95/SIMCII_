package com.simcii.javaservice.repositories;

import com.simcii.javaservice.models.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la gestión de entidades Lectura en la base de datos.
 * Proporciona operaciones de acceso a datos para el historial de mediciones
 * capturadas por los dispositivos del sistema SIMCII, incluyendo consultas
 * temporales y paginadas para análisis de datos.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Repository
 * @see Lectura
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Long> {

    /**
     * Encuentra todas las lecturas de un dispositivo específico ordenadas por fecha descendente.
     * Retorna el historial completo de mediciones de un dispositivo, mostrando primero
     * las lecturas más recientes para facilitar el análisis temporal.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Lectura> con el historial de lecturas ordenado descendente por fecha
     */
    List<Lectura> findByDispositivoIdOrderByFechaHoraDesc(Long dispositivoId);

    /**
     * Encuentra lecturas de un dispositivo dentro de un rango temporal específico.
     * Útil para generar reportes históricos, análisis de tendencias y auditorías
     * de datos en períodos definidos.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @param inicio Fecha y hora de inicio del rango temporal (inclusive)
     * @param fin Fecha y hora de fin del rango temporal (inclusive)
     * @return List<Lectura> con las lecturas del rango temporal ordenadas descendente
     * @see LocalDateTime
     */
    @Query("SELECT l FROM Lectura l WHERE l.dispositivo.id = :dispositivoId AND l.fechaHora BETWEEN :inicio AND :fin ORDER BY l.fechaHora DESC")
    List<Lectura> findByDispositivoIdAndFechaHoraBetween(Long dispositivoId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Encuentra las lecturas más recientes de un dispositivo usando consulta nativa.
     * Optimizado para obtener rápidamente un subconjunto de lecturas recientes
     * sin cargar todo el historial, ideal para dashboards en tiempo real.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @param limit Número máximo de lecturas recientes a retornar
     * @return List<Lectura> con las lecturas más recientes del dispositivo
     * @implNote Utiliza consulta SQL nativa con LIMIT para mejor rendimiento
     */
    @Query(value = "SELECT * FROM lecturas WHERE dispositivo_id = :dispositivoId ORDER BY fecha_hora DESC LIMIT :limit", nativeQuery = true)
    List<Lectura> findUltimasLecturas(Long dispositivoId, int limit);
}