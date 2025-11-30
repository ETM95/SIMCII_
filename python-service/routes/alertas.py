# alertas.py
"""
Blueprint de Flask para endpoints relacionados con alertas.

Este módulo define las rutas para gestionar y consultar alertas del sistema.
"""

from flask import Blueprint, jsonify, request
from services.data_store import data_store

alertas_bp = Blueprint('alertas', __name__)

@alertas_bp.route('/alertas/activas', methods=['GET'])
def listar_alertas_activas():
    """
    Obtiene la lista de alertas activas en el sistema.
    
    Returns:
        JSON con el total de alertas y la lista de alertas activas ordenadas por criticidad
    """
    alertas_activas = data_store.obtener_alertas_activas()
    
    alertas_activas.sort(key=lambda x: (-x.nivel_criticidad, x.fecha_creacion), reverse=True)
    
    return jsonify({
        "total": len(alertas_activas),
        "alertas": [alerta.to_dict() for alerta in alertas_activas]
    })

@alertas_bp.route('/simulacion/reiniciar', methods=['POST'])
def reiniciar_simulacion():
    """
    Reinicia la simulación limpiando alertas y actualizando dispositivos.
    
    Returns:
        JSON con estado del reinicio
    """
    from app import obtener_dispositivos_desde_java
    obtener_dispositivos_desde_java()
    data_store.alertas = []
    return jsonify({
        "mensaje": "Simulacion reiniciada",
        "dispositivos_actualizados": len(data_store.dispositivos_simulados),
        "alertas_limpiadas": True
    })