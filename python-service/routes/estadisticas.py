# estadisticas.py
"""
Blueprint de Flask para endpoints de estadísticas.

Este módulo define las rutas para consultar estadísticas del sistema.
"""

from flask import Blueprint, jsonify
from services.data_store import data_store
from services.simulator import SimuladorDispositivos

estadisticas_bp = Blueprint('estadisticas', __name__)

@estadisticas_bp.route('/estadisticas/zonas', methods=['GET'])
def obtener_estadisticas_todas_zonas():
    """
    Obtiene estadísticas de todas las zonas monitoreadas.
    
    Returns:
        JSON con estadísticas agregadas por zona
    """
    SimuladorDispositivos.actualizar_estadisticas_simuladas(data_store.estadisticas)
    
    return jsonify({
        "total_zonas": len(data_store.estadisticas),
        "estadisticas": data_store.estadisticas
    })