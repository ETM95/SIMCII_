# reportes.py
"""
Blueprint de Flask para endpoints de reportes.

Este módulo define las rutas para generar y exportar reportes del sistema.
"""

from flask import Blueprint, Response
from services.data_store import data_store
from services.simulator import SimuladorDispositivos
from datetime import datetime
import csv
import io

reportes_bp = Blueprint('reportes', __name__)

@reportes_bp.route('/reportes/alertas/csv', methods=['GET'])
def exportar_reporte_csv():
    """
    Exporta un reporte de alertas en formato CSV.
    
    Returns:
        Response con archivo CSV descargable
    """
    output = io.StringIO()
    writer = csv.writer(output)
    
    writer.writerow([
        'ID', 'Dispositivo ID', 'Dispositivo Nombre', 'Zona', 
        'Tipo Alerta', 'Valor', 'Umbral Min', 'Umbral Max', 
        'Mensaje', 'Fecha Creacion', 'Activa', 'Nivel Criticidad'
    ])
    
    for alerta in data_store.alertas:
        writer.writerow([
            alerta.id,
            alerta.dispositivo_id,
            alerta.dispositivo_nombre,
            alerta.zona,
            alerta.tipo_alerta,
            alerta.valor,
            alerta.umbral_min,
            alerta.umbral_max,
            alerta.mensaje,
            alerta.fecha_creacion,
            "Si" if alerta.activa else "No",
            alerta.nivel_criticidad
        ])
    
    output.seek(0)
    
    return Response(
        output.getvalue(),
        mimetype="text/csv",
        headers={
            "Content-Disposition": f"attachment; filename=reporte_alertas_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        }
    )

@reportes_bp.route('/reportes/estadisticas/csv', methods=['GET'])
def exportar_estadisticas_csv():
    """
    Exporta estadísticas del sistema en formato CSV.
    
    Returns:
        Response con archivo CSV descargable
    """
    output = io.StringIO()
    writer = csv.writer(output)
    
    writer.writerow([
        'Zona', 'Tipo Sensor', 'Promedio', 'Minimo', 'Maximo', 
        'Cantidad Lecturas', 'Ultima Actualizacion'
    ])
    
    for zona_nombre, stats in data_store.estadisticas.items():
        for tipo_sensor, datos in stats["estadisticas"].items():
            writer.writerow([
                zona_nombre,
                tipo_sensor.capitalize(),
                f"{datos['promedio']:.2f}",
                f"{datos['minimo']:.2f}",
                f"{datos['maximo']:.2f}",
                datos['cantidad_lecturas'],
                stats['ultima_actualizacion']
            ])
    
    output.seek(0)
    
    return Response(
        output.getvalue(),
        mimetype="text/csv",
        headers={
            "Content-Disposition": f"attachment; filename=estadisticas_zonas_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        }
    )