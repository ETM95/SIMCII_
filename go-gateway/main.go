package main

import (
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"

	"github.com/gin-gonic/gin"
)

func main() {
	r := gin.Default()

	// SERVIR ARCHIVOS ESTÁTICOS
	r.Static("/static", "./static")

	// CARGAR TEMPLATES
	r.LoadHTMLGlob("templates/*.html")

	// PÁGINA DE LOGIN
	r.GET("/", func(c *gin.Context) {
		c.HTML(http.StatusOK, "login.html", nil)
	})

	// DASHBOARD SIMPLE (TEMPORAL)
	r.GET("/dashboard", func(c *gin.Context) {
		c.HTML(http.StatusOK, "dashboard.html", gin.H{
			"Username": "admin",
		})
	})

	// LOGIN HANDLER CON AUTENTICACIÓN REAL
	r.POST("/login", func(c *gin.Context) {
		username := c.PostForm("username")
		password := c.PostForm("password")

		log.Printf("Intento de login: usuario=%s", username)

		// AUTENTICACIÓN CON CREDENCIALES FIJAS
		if username == "admin" && password == "admin" {
			log.Printf("Login exitoso para usuario: %s", username)

			// PODEMOS AGREGAR SESIÓN/JWT AQUÍ MÁS ADELANTE
			c.Redirect(http.StatusFound, "/dashboard")
			return
		}

		// LOGIN FALLIDO
		log.Printf("Login fallido para usuario: %s", username)
		c.HTML(http.StatusOK, "login.html", gin.H{
			"Error": "Credenciales incorrectas",
		})
	})

	// LOGOUT
	r.POST("/logout", func(c *gin.Context) {
		log.Println("Usuario cerró sesión")
		c.Redirect(http.StatusFound, "/")
	})

	// PROXY a Java API
	javaURL, _ := url.Parse("http://java-service:8080")
	javaProxy := httputil.NewSingleHostReverseProxy(javaURL)

	r.Any("/api/*path", func(c *gin.Context) {
		log.Printf("Proxy to Java: %s", c.Request.URL.Path)
		javaProxy.ServeHTTP(c.Writer, c.Request)
	})

	// ✅ PROXY a Python API
	pythonURL, _ := url.Parse("http://python-service:8000")
	pythonProxy := httputil.NewSingleHostReverseProxy(pythonURL)

	r.Any("/python-api/*path", func(c *gin.Context) {
		log.Printf("Proxy to Python: %s", c.Request.URL.Path)
		pythonProxy.ServeHTTP(c.Writer, c.Request)
	})

	// MIDDLEWARE DE AUTENTICACIÓN (PARA FUTURO)
	// r.Use(AuthMiddleware())

	log.Println("Go Gateway INICIADO en :8081")
	log.Println("Login: http://localhost:8081")
	log.Println("Dashboard: http://localhost:8081/dashboard")
	log.Println("Autenticación activa: usuario=admin, contraseña=admin")

	r.Run(":8081")
}

// MIDDLEWARE DE AUTENTICACIÓN (PARA IMPLEMENTAR DESPUÉS)
func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		// Aquí podemos agregar JWT o sesiones después
		c.Next()
	}
}
