# __init__.py
"""
Paquete de rutas para la API del sistema de alertas.

Este módulo inicializa y registra todos los blueprints de la aplicación.
"""

from .alertas import alertas_bp
from .dispositivos import dispositivos_bp
from .estadisticas import estadisticas_bp
from .reportes import reportes_bp

def init_routes(app):
    """
    Inicializa y registra todos los blueprints en la aplicación Flask.
    
    Args:
        app: Instancia de la aplicación Flask
    """
    app.register_blueprint(alertas_bp, url_prefix='/api')
    app.register_blueprint(dispositivos_bp, url_prefix='/api')
    app.register_blueprint(estadisticas_bp, url_prefix='/api')
    app.register_blueprint(reportes_bp, url_prefix='/api')