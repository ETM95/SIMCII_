package com.simcii.javaservice.services;

import com.simcii.javaservice.models.Dispositivo;
import com.simcii.javaservice.models.Umbral;
import com.simcii.javaservice.repositories.UmbralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de negocio para la gestión de alertas y umbrales en el sistema SIMCII.
 * Proporciona la lógica para monitorear lecturas de dispositivos, verificar violaciones
 * de umbrales y generar alertas cuando se detectan condiciones fuera de rangos aceptables.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see Umbral
 * @see Dispositivo
 * @see UmbralRepository
 */
@Service
public class AlertaService {
    
    /**
     * Repositorio para acceso a datos de umbrales.
     * Utilizado para consultar y gestionar la configuración de umbrales de alerta.
     */
    @Autowired
    private UmbralRepository umbralRepository;
    
    /**
     * Verifica si una lectura de dispositivo viola alguno de sus umbrales activos.
     * Compara el valor de lectura contra todos los umbrales activos del dispositivo
     * y genera alertas para aquellos umbrales que sean violados.
     * 
     * @param dispositivo Dispositivo que generó la lectura
     * @param valor Valor numérico de la lectura a verificar
     * @see UmbralRepository#findByDispositivoAndActivoTrue(Dispositivo)
     */
    public void verificarUmbrales(Dispositivo dispositivo, Double valor) {
        List<Umbral> umbrales = umbralRepository.findByDispositivoAndActivoTrue(dispositivo);
        
        for (Umbral umbral : umbrales) {
            if (valor < umbral.getValorMin() || valor > umbral.getValorMax()) {
                generarAlerta(dispositivo, valor, umbral);
            }
        }
    }
    
    /**
     * Genera una alerta cuando se detecta una violación de umbral.
     * Actualmente implementa logging en consola, pero puede extenderse para
     * notificaciones por email, SMS, o integración con sistemas externos.
     * 
     * @param dispositivo Dispositivo que generó la alerta
     * @param valor Valor de lectura que violó el umbral
     * @param umbral Umbral específico que fue violado
     * @implNote Actualmente solo realiza logging - puede extenderse para notificaciones
     */
    private void generarAlerta(Dispositivo dispositivo, Double valor, Umbral umbral) {
        System.out.println("ALERTA: Dispositivo " + dispositivo.getNombre() + 
                          " valor " + valor + " fuera de rango [" + 
                          umbral.getValorMin() + " - " + umbral.getValorMax() + "]");
    }
    
    /**
     * Configura o actualiza un umbral de alerta en el sistema.
     * Puede usarse tanto para crear nuevos umbrales como para modificar existentes.
     * 
     * @param umbral Objeto Umbral con la configuración completa
     * @return Umbral el umbral guardado con su ID asignado
     * @see UmbralRepository#save(Object)
     */
    public Umbral configurarUmbral(Umbral umbral) {
        return umbralRepository.save(umbral);
    }
    
    /**
     * Obtiene todos los umbrales configurados para un dispositivo específico.
     * 
     * @param dispositivoId Identificador único del dispositivo a consultar
     * @return List<Umbral> con todos los umbrales del dispositivo
     * @see UmbralRepository#findByDispositivoId(Long)
     */
    public List<Umbral> obtenerUmbralesPorDispositivo(Long dispositivoId) {
        return umbralRepository.findByDispositivoId(dispositivoId);
    }
}