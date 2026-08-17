
# 🛒 POS Sistema (Punto de Venta)

Sistema de Punto de Venta (POS) Full Stack moderno, robusto y escalable. Desarrollado con **Spring Boot 3** en el backend y **React + TypeScript** en el frontend, implementando las mejores prácticas de la industria como arquitectura por capas, JWT para autenticación, y pruebas unitarias.

## 🚀 Tecnologías Utilizadas

### Backend
- ☕ **Java 17/22** & **Spring Boot 3.2.5**
- 🛡️ **Spring Security** + **JWT (JJWT)** para autenticación stateless
- 🗄️ **PostgreSQL** como base de datos relacional
- 🦋 **Flyway** para migraciones de base de datos versionadas
- 🧪 **JUnit 5 + Mockito** para pruebas unitarias (Cobertura > 80%)
- 📝 **SpringDoc OpenAPI (Swagger)** para documentación de API

### Frontend
- ⚛️ **React 18** + **TypeScript**
- ⚡ **Vite** como bundler
- 🎨 **Tailwind CSS** para estilos modernos y responsivos
- 🌐 **Axios** para consumo de API con interceptores
- 🏪 **Zustand** para manejo de estado global
- 🔔 **React Hot Toast** para notificaciones

## ✨ Características Principales
- 🔐 Autenticación segura con JWT y roles de usuario.
- 📦 Gestión completa de inventario (Productos y Categorías).
- 🛒 Módulo de ventas con carrito de compras interactivo.
- 📊 Dashboard con métricas y estadísticas en tiempo real.
- 📥 Exportación de reportes a Excel (Apache POI).

## 🛠️ Prerrequisitos
- Java 17 o superior (Recomendado: Corretto 22)
- Node.js 18+ y npm
- PostgreSQL 15+
- Docker (Opcional, para despliegue con contenedores)

## ⚙️ Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/IngeniUriDev/pos-sistema.git
cd pos-sistema

Conmfiguracion de Base de datos

Asegúrate de tener PostgreSQL corriendo y crea una base de datos llamada pos_db. Las migraciones se ejecutarán automáticamente al iniciar el backend.
 Ejecucion de Backend
cd pos-sistema
./mvnw clean spring-boot:run
# La API estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html

Ejecutar Frontend

cd frontend
npm install
npm run dev
# La aplicación estará disponible en http://localhost:5173

Credenciales por Defecto (Desarrollo)
Usuario: admin
Contraseña: admin123

Estructura

├── pos-sistema/          # Backend Spring Boot
│   ├── src/main/java/    # Código fuente (Controllers, Services, Repositories, Security)
│   ├── src/test/java/    # Pruebas unitarias
│   └── src/main/resources/db/migration/ # Scripts de Flyway
├── frontend/             # Frontend React + TypeScript
│   ├── src/
│   │   ├── api/          # Servicios de Axios
│   │   ├── components/   # Componentes reutilizables
│   │   ├── pages/        # Vistas principales
│   │   └── stores/       # Estado global (Zustand)
└── README.md

🤝 Contribución
Las contribuciones son bienvenidas. Por favor, abre un Issue o un Pull Request para discutir cambios importantes.
📄 Licencia
Este proyecto está bajo la Licencia MIT.

---

### 🛡️ PASO 2: Verificar el `.gitignore`

Para no subir basura a GitHub (como `node_modules` o archivos compilados), asegúrate de que en la **carpeta raíz** tengas un archivo `.gitignore` con este contenido:

```text
# Backend (Java/Maven)
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/
*.class
*.log

# Frontend (Node/React)
frontend/node_modules/
frontend/dist/
frontend/.vite/
frontend/.env.local

# IDEs
.idea/
*.iml
.vscode/
*.swp
*.swo

# OS
.DS_Store
Thumbs.db
