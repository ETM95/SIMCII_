# dispositivos.py
"""
Blueprint de Flask para endpoints de dispositivos.

Este módulo define las rutas para consultar dispositivos del sistema.
"""

from flask import Blueprint, jsonify
from services.data_store import data_store

dispositivos_bp = Blueprint('dispositivos', __name__)

@dispositivos_bp.route('/dispositivos/simulados', methods=['GET'])
def obtener_dispositivos_simulados():
    """
    Obtiene la lista de dispositivos simulados/monitoreados.
    
    Returns:
        JSON con lista de dispositivos y metadatos
    """
    return jsonify({
        "total": len(data_store.dispositivos_simulados),
        "dispositivos": [dispositivo.to_dict() for dispositivo in data_store.dispositivos_simulados],
        "ultima_actualizacion": data_store.ultima_actualizacion_dispositivos.isoformat() if data_store.ultima_actualizacion_dispositivos else None
    })