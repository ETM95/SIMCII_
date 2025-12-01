package com.simcii.javaservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * Servicio para la comunicación con el módulo Python del sistema SIMCII.
 * Gestiona el envío de eventos asincrónicos desde el módulo Java hacia el servicio Python
 * para procesamiento de análisis de datos, generación de alertas y reportes.
 * 
 * @author Jonathan Vega
 * @version 1.0
 * @since 2025
 * @Service
 * @see RestTemplate
 */
@Service
public class EventoService {

    /**
     * URL base del servicio Python configurada desde propiedades.
     * Valor por defecto: http://python-service:8000 para entornos Docker.
     * Puede sobrescribirse mediante la propiedad: python.service.url
     */
    @Value("${python.service.url:http://python-service:8000}")
    private String pythonServiceUrl;

    /**
     * Cliente REST para comunicación HTTP con servicios externos.
     * Configurado automáticamente por Spring Framework.
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Envía un evento al servicio Python para procesamiento asincrónico.
     * Los eventos pueden incluir lecturas de sensores, cambios de estado de actuadores,
     * violaciones de umbrales y otras notificaciones del sistema.
     * 
     * @param tipoEvento Tipo de evento a enviar (ej: "LECTURA_SENSOR", "ALERTA_GENERADA")
     * @param datos Map con los datos específicos del evento
     * @implNote El método es tolerante a fallos - los errores de comunicación no interrumpen
     *           el flujo principal de la aplicación
     * @see <a href="http://python-service:8000/api/v1/eventos">Endpoint Python Service</a>
     */
    public void enviarEventoPython(String tipoEvento, Map<String, Object> datos) {
        try {
            String url = pythonServiceUrl + "/api/v1/eventos";
            
            Map<String, Object> evento = Map.of(
                "id", UUID.randomUUID().toString(),
                "tipo", tipoEvento,
                "datos", datos
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(evento, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            System.out.println("Evento enviado a Python Service: " + tipoEvento + 
                             " | Status: " + response.getStatusCode());
            
        } catch (Exception e) {
            System.err.println("Error enviando evento a Python Service: " + e.getMessage());
        }
    }
}