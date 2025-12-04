# reportes.py
"""
Blueprint de Flask para endpoints de reportes.

Este módulo define las rutas para generar y exportar reportes del sistema.
"""

from flask import Blueprint, Response, jsonify, current_app
from services.data_store import data_store
from datetime import datetime
import csv
import io
import logging

# Configurar logging
logger = logging.getLogger(__name__)

reportes_bp = Blueprint('reportes', __name__)

@reportes_bp.route('/alertas/csv', methods=['GET'])
def exportar_reporte_csv():
    """
    Exporta un reporte de alertas en formato CSV.
    """
    logger.info(f"Solicitud de reporte CSV recibida - {datetime.now()}")
    
    try:
        output = io.StringIO()
        writer = csv.writer(output)
        
        # Escribir encabezados
        writer.writerow([
            'ID', 'Dispositivo ID', 'Dispositivo Nombre', 'Zona', 
            'Tipo Alerta', 'Valor', 'Umbral Min', 'Umbral Max', 
            'Mensaje', 'Fecha Creacion', 'Activa', 'Nivel Criticidad'
        ])
        
        # Verificar si hay alertas
        alertas_existentes = False
        
        # Debug: Verificar data_store
        logger.info(f"data_store existe: {hasattr(data_store, 'alertas')}")
        
        if hasattr(data_store, 'alertas') and data_store.alertas:
            logger.info(f"Cantidad de alertas: {len(data_store.alertas)}")
            
            for alerta in data_store.alertas:
                logger.info(f"Procesando alerta: {getattr(alerta, 'id', 'N/A')}")
                
                writer.writerow([
                    getattr(alerta, 'id', 'N/A'),
                    getattr(alerta, 'dispositivo_id', 'N/A'),
                    getattr(alerta, 'dispositivo_nombre', 'N/A'),
                    getattr(alerta, 'zona', 'N/A'),
                    getattr(alerta, 'tipo_alerta', 'N/A'),
                    getattr(alerta, 'valor', 'N/A'),
                    getattr(alerta, 'umbral_min', 'N/A'),
                    getattr(alerta, 'umbral_max', 'N/A'),
                    getattr(alerta, 'mensaje', 'N/A'),
                    getattr(alerta, 'fecha_creacion', datetime.now().isoformat()),
                    "Si" if getattr(alerta, 'activa', False) else "No",
                    getattr(alerta, 'nivel_criticidad', 'N/A')
                ])
                alertas_existentes = True
        
        # Si no hay alertas válidas, escribir una fila informativa
        if not alertas_existentes:
            timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            writer.writerow([
                '--', '--', '--', '--', 'INFORMATIVO', '--', 
                '--', '--', f'No hay alertas registradas al {timestamp}', 
                datetime.now().isoformat(), 'No', 'INFORMATIVO'
            ])
            logger.info("No se encontraron alertas, generando reporte informativo")
        
        output.seek(0)
        csv_content = output.getvalue()
        
        logger.info(f"Tamaño del CSV: {len(csv_content)} bytes")
        
        filename = f"reporte_alertas_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        
        response = Response(
            csv_content,
            mimetype="text/csv; charset=utf-8",
            headers={
                "Content-Disposition": f"attachment; filename={filename}",
                "Content-Length": str(len(csv_content.encode('utf-8')))
            }
        )
        
        logger.info("Reporte CSV generado exitosamente")
        return response
        
    except Exception as e:
        logger.error(f"Error al generar reporte CSV: {str(e)}")
        # Generar un CSV de error
        output = io.StringIO()
        writer = csv.writer(output)
        writer.writerow(['Error', 'Mensaje'])
        writer.writerow(['ERROR', f'Error al generar reporte: {str(e)}'])
        output.seek(0)
        
        return Response(
            output.getvalue(),
            mimetype="text/csv; charset=utf-8",
            headers={
                "Content-Disposition": "attachment; filename=error_report.csv"
            }
        )

@reportes_bp.route('/estadisticas/csv', methods=['GET'])
def exportar_estadisticas_csv():
    """
    Exporta estadísticas del sistema en formato CSV.
    """
    logger.info(f"Solicitud de estadísticas CSV recibida - {datetime.now()}")
    
    try:
        output = io.StringIO()
        writer = csv.writer(output)
        
        writer.writerow([
            'Zona', 'Tipo Sensor', 'Promedio', 'Minimo', 'Maximo', 
            'Cantidad Lecturas', 'Ultima Actualizacion'
        ])
        
        # Verificar si hay estadísticas
        if hasattr(data_store, 'estadisticas') and data_store.estadisticas:
            for zona_nombre, stats in data_store.estadisticas.items():
                if isinstance(stats, dict) and "estadisticas" in stats:
                    for tipo_sensor, datos in stats["estadisticas"].items():
                        writer.writerow([
                            zona_nombre,
                            tipo_sensor.capitalize(),
                            f"{datos.get('promedio', 0.0):.2f}",
                            f"{datos.get('minimo', 0.0):.2f}",
                            f"{datos.get('maximo', 0.0):.2f}",
                            datos.get('cantidad_lecturas', 0),
                            stats.get('ultima_actualizacion', datetime.now().isoformat())
                        ])
        else:
            # Si no hay estadísticas, escribir una fila informativa
            timestamp = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            writer.writerow([
                'SISTEMA', 'GENERAL', '0.00', '0.00', '0.00', 
                0, f'No hay estadísticas disponibles al {timestamp}'
            ])
        
        output.seek(0)
        csv_content = output.getvalue()
        
        filename = f"estadisticas_zonas_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        
        return Response(
            csv_content,
            mimetype="text/csv; charset=utf-8",
            headers={
                "Content-Disposition": f"attachment; filename={filename}"
            }
        )
        
    except Exception as e:
        logger.error(f"Error al generar estadísticas CSV: {str(e)}")
        return jsonify({"error": str(e)}), 500

@reportes_bp.route('/alertas/test', methods=['GET'])
def test_endpoint():
    """
    Endpoint de prueba para verificar que el blueprint está funcionando.
    """
    return jsonify({
        "status": "ok",
        "message": "Blueprint de reportes funcionando correctamente",
        "timestamp": datetime.now().isoformat(),
        "endpoints": {
            "csv_alertas": "/api/alertas/csv",
            "csv_estadisticas": "/api/estadisticas/csv",
            "test": "/api/alertas/test"
        }
    })

@reportes_bp.route('/alertas/status', methods=['GET'])
def verificar_alertas():
    """
    Verifica el estado de las alertas.
    """
    return jsonify({
        "status": "ok",
        "alertas_disponibles": hasattr(data_store, 'alertas') and bool(data_store.alertas),
        "timestamp": datetime.now().isoformat()
    })