# GruasTreMart Core API

## Descripción General

**GruasTreMart Core API** es una aplicación Spring Boot que proporciona servicios backend para una plataforma de gestión de grúas. La API permite gestionar demandas de grúas, operadores, usuarios, precios y comunicaciones entre clientes y operadores de grúas.

## Stack Tecnológico

- **Java 21**
- **Spring Boot 3.3.3**
- **MongoDB** (Base de datos NoSQL)
- **Supabase** (Autenticación y gestión de usuarios)
- **JWT Authentication** (Autenticación basada en tokens)
- **Spring Security** (Seguridad y autorización)
- **OpenFeign** (Cliente HTTP declarativo)
- **Docker** (Contenedorización)
- **Maven** (Gestión de dependencias)
- **Swagger/OpenAPI 3** (Documentación de API)

## Funcionalidades Implementadas

### 🚛 1. Gestión de Demandas de Grúas (`CraneDemandController`)

La funcionalidad principal de la aplicación que permite gestionar solicitudes de servicios de grúas.

#### Características:
- **Crear demandas de grúas**: Los usuarios pueden crear nuevas solicitudes de servicio
- **Búsqueda avanzada**: Filtrado por ubicación geográfica (latitud, longitud, radio)
- **Filtros por estado**: ACTIVE, INACTIVE, TAKEN, COMPLETED, CANCELLED
- **Filtros temporales**: Búsqueda por rango de fechas
- **Paginación**: Resultados paginados para mejor rendimiento
- **Asignación de demandas**: Los operadores pueden tomar/asignar demandas
- **Categorización por peso**: Sistema de categorías de peso para diferentes tipos de grúas

#### Endpoints:
- `GET /api/v1/crane-demands` - Buscar demandas con filtros
- `GET /api/v1/crane-demands/{id}` - Obtener demanda específica
- `POST /api/v1/crane-demands` - Crear nueva demanda
- `PATCH /api/v1/crane-demands/{id}/assign` - Asignar demanda a operador

### 👥 2. Gestión de Usuarios (`UserController`)

Sistema completo de gestión de usuarios de la plataforma.

#### Características:
- **Búsqueda de usuarios**: Con filtros por email y ID de Supabase
- **Paginación**: Listado paginado de usuarios
- **Integración con Supabase**: Autenticación externa
- **Perfiles de usuario**: Gestión de información personal

#### Endpoints:
- `GET /api/v1/users` - Buscar usuarios con filtros
- `GET /api/v1/users/{id}` - Obtener usuario específico
- `POST /api/v1/users` - Crear nuevo usuario
- `PATCH /api/v1/users/{id}` - Actualizar usuario

### 🚚 3. Gestión de Operadores (`OperatorController`)

Funcionalidad específica para gestionar operadores de grúas.

#### Características:
- **Perfiles de operadores**: Información específica de conductores de grúas
- **Geolocalización**: Actualización y seguimiento de ubicación en tiempo real
- **Vinculación con usuarios**: Relación entre usuarios y perfiles de operador

#### Endpoints:
- `GET /api/v1/operators` - Obtener operador por ID de usuario
- `PUT /api/v1/operators/{id}/location` - Actualizar ubicación del operador

### 💰 4. Sistema de Precios (`CranePricingController`)

Gestión de tarifas y precios para diferentes tipos de servicios de grúas.

#### Características:
- **Precios diferenciados**: Tarifas urbanas y extraurbanas
- **Categorías de peso**: Precios según capacidad de la grúa
- **Estado activo/inactivo**: Control de vigencia de tarifas
- **Búsqueda y filtrado**: Consulta de precios con filtros

#### Endpoints:
- `GET /api/v1/crane-pricing` - Buscar precios con filtros
- `GET /api/v1/crane-pricing/{id}` - Obtener precio específico

### 📧 5. Sistema de Comunicaciones (`EmailController`)

Funcionalidad para envío de correos electrónicos y comunicaciones.

#### Características:
- **Correos de contacto**: Envío de mensajes desde formularios web
- **Notificaciones automáticas**: Sistema de alertas por email
- **Templates personalizados**: Plantillas para diferentes tipos de comunicación

#### Endpoints:
- `POST /api/v1/emails/contact` - Enviar correo de contacto

### 📝 6. Formularios de Contacto (`ContactFormController`)

Sistema de captura y gestión de consultas de clientes.

#### Características:
- **Formularios web**: Captura de consultas desde la web
- **Validación de datos**: Verificación de información ingresada
- **Respuestas automáticas**: Sistema de confirmación de recepción

#### Endpoints:
- `POST /api/v1/contact-forms` - Crear formulario de contacto

### 🔐 7. Integración con Supabase (`AuthController`)

Sistema de autenticación y gestión de usuarios integrado con Supabase.

#### Características:
- **Autenticación externa**: Integración completa con Supabase Auth
- **Recuperación de contraseñas**: Sistema de reset de contraseñas via email
- **Cambio de contraseñas**: Actualización segura de credenciales
- **Gestión de tokens**: Manejo de tokens de acceso y recuperación
- **Cliente Feign**: Comunicación optimizada con APIs de Supabase

#### Endpoints:
- `POST /api/v1/auth/recover` - Iniciar recuperación de contraseña
- `PUT /api/v1/auth/change-password` - Cambiar contraseña del usuario

### 🔄 8. Comunicación en Tiempo Real

Sistema de comunicación bidireccional entre frontend y backend.

#### Características:
- **Polling optimizado**: Comunicación eficiente mediante polling entre frontend y backend
- **Actualizaciones en tiempo real**: Estado actualizado de demandas y operadores
- **Notificaciones automáticas**: Sistema de alertas y notificaciones
- **Logs de WebSocket**: Tracking y monitoreo de conexiones
- **Gestión de estado**: Sincronización automática de datos

#### Funcionalidades:
- Actualización automática de estado de demandas
- Notificaciones de nuevas asignaciones
- Tracking de ubicación de operadores en tiempo real
- Alertas de sistema y comunicaciones

## Arquitectura y Patrones

### 🏗️ Estructura del Proyecto

```
src/main/java/com/gruastremart/api/
├── Application.java           # Clase principal de Spring Boot
├── config/                    # Configuraciones (Security, CORS, etc.)
├── controller/               # Controladores REST
├── dto/                      # Data Transfer Objects
├── exception/                # Manejo de excepciones
├── mapper/                   # Mappers entre entidades y DTOs
├── persistance/              # Repositorios y entidades
├── service/                  # Lógica de negocio
└── utils/                    # Utilidades y herramientas
```

### 🔒 Seguridad

- **JWT Authentication**: Autenticación basada en tokens
- **Spring Security**: Autorización y control de acceso
- **CORS**: Configuración para aplicaciones web
- **Validación de datos**: Validación automática de requests

### 📊 Base de Datos

- **MongoDB**: Base de datos NoSQL para flexibilidad en el esquema
- **Geolocalización**: Soporte nativo para consultas geoespaciales
- **Indexación**: Índices optimizados para búsquedas frecuentes

## Configuración y Despliegue

### 🔧 Variables de Entorno

La aplicación requiere las siguientes variables de entorno:

```env
# Perfil de Spring Boot
SPRING_PROFILES_ACTIVE=des

# Base de datos MongoDB
MONGODB_DEV_URL=mongodb+srv://...
MONGODB_TEST_URL=mongodb+srv://...

# Configuración de Supabase
SUPABASE_SECURITY_SECRET_KEY=your_secret_key
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your_anon_key

# Configuración de email
EMAIL_USER=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
EMAIL_FORGOT_PASSWORD_LINK=https://your-frontend.com/reset-password
```

### 🐳 Docker

La aplicación incluye un `Dockerfile` multi-stage optimizado:

```bash
# Construir imagen
docker build -t gruastremart-core-api:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=des \
  -e MONGODB_DEV_URL=your_mongodb_url \
  gruastremart-core-api:latest
```

### 🚀 Despliegue Automático con GitHub Actions

El proyecto utiliza GitHub Actions para CI/CD automático. El workflow (`.github/workflows/deploy.yml`) realiza:

#### 🔄 Proceso de Despliegue:

1. **Build**: Compila la aplicación con Maven
2. **Docker**: Construye la imagen Docker
3. **Transfer**: Copia la imagen al VPS via SCP
4. **Deploy**: Despliega en Kubernetes (k3s) en el VPS

#### 📋 Configuración Requerida:

**Repository Secrets en GitHub:**
- `VPS_SSH_HOST`: IP o dominio del VPS
- `VPS_SSH_USERNAME`: Usuario SSH del VPS
- `GPG_PASSPHRASE`: Passphrase para descifrar la clave SSH
- `ENV_FILE`: Archivo `.env` con variables de entorno (opcional)

**Archivos en el VPS:**
- `/home/k3s-deployments/gruastremart-core-api/.env` - Variables de entorno
- `/home/k3s-deployments/gruastremart-core-api/deploy-gruastremart-core-api.yml` - Configuración de Kubernetes

#### 🔐 Gestión de Secrets en Kubernetes:

El workflow automáticamente:
- Crea/actualiza el Secret `gruastremart-core-env` desde el archivo `.env`
- Aplica la configuración de deployment
- Reinicia el deployment para aplicar cambios

```bash
# El Secret se crea automáticamente con:
kubectl create secret generic gruastremart-core-env \
  --from-env-file=/path/to/.env
```

### ⚙️ Configuración de Entornos

La aplicación soporta múltiples perfiles:

- **local**: Desarrollo local
- **test**: Pruebas automatizadas  
- **des**: Desarrollo/Staging
- **prod**: Producción

### 🏥 Health Checks

La aplicación incluye endpoints de salud:
- `/gruastremart-core-api/actuator/health` - Estado general
- `/gruastremart-core-api/actuator/info` - Información de la aplicación

## Documentación API

La API está completamente documentada con **Swagger/OpenAPI 3**, accesible en:
```
http://localhost:8080/swagger-ui.html
```

Incluye:
- Descripción detallada de endpoints
- Esquemas de request/response
- Códigos de estado HTTP
- Ejemplos de uso

## Testing

### 🧪 Pruebas Implementadas

- **Unit Tests**: Pruebas unitarias para controladores y servicios
- **Integration Tests**: Pruebas de integración con base de datos
- **Cucumber Tests**: Pruebas BDD para funcionalidades específicas

### 📋 Cobertura de Pruebas

- `CraneDemandControllerTest`
- `UserControllerTest`
- `EmailServiceTest`
- `EmailControllerTest`
- Tests de integración con Cucumber

### 🏃‍♂️ Ejecutar Pruebas

```bash
# Todas las pruebas
mvn test

# Solo pruebas unitarias
mvn test -Dtest="**/*Test"

# Solo pruebas de integración
mvn test -Dtest="**/*IT"
```

## Desarrollo Local

### 📋 Requisitos

- Java 21+
- MongoDB 4.4+
- Maven 3.8+
- Docker (opcional)

### 🚀 Inicio Rápido

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd gruastremart-core-api

# 2. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus configuraciones

# 3. Compilar y ejecutar
mvn clean install
mvn spring-boot:run

# 4. Acceder a la aplicación
# API: http://localhost:8080/gruastremart-core-api
# Swagger: http://localhost:8080/swagger-ui.html
```

## Autenticación de Prueba

Para pruebas y desarrollo:

- **Usuario**: `test@test.com`
- **Contraseña**: `bWlDb250cmFzZcOxYTEyMw==` (Base64 de `miContraseña123`)

## Logs y Monitoreo

- **Logback**: Sistema de logging configurable
- **Logs WebSocket**: Tracking de conexiones en tiempo real
- **Structured Logging**: Logs estructurados para análisis
- **Log Level**: Configurable por entorno

## Estado del Proyecto

### ✅ Funcionalidades Completadas

- ✅ Gestión completa de demandas de grúas
- ✅ Sistema de usuarios y operadores
- ✅ Autenticación y autorización JWT
- ✅ Sistema de precios configurable
- ✅ Comunicaciones por email
- ✅ Formularios de contacto
- ✅ Geolocalización y búsquedas por proximidad
- ✅ Documentación completa con Swagger
- ✅ Testing automatizado
- ✅ Despliegue automatizado con GitHub Actions
- ✅ Integración completa con Supabase Auth
- ✅ Comunicación en tiempo real via polling optimizado
- ✅ Sistema de notificaciones y alertas automáticas
- ✅ Recuperación y cambio de contraseñas
- ✅ Cliente Feign para APIs externas

### 🚧 En Desarrollo

- 🚧 Dashboard de métricas y analytics
- 🚧 Integración con sistemas de pago
- 🚧 API de reportes y estadísticas
- 🚧 Sistema de calificaciones y reviews

## Contribución

Para contribuir al proyecto:

1. Clone el repositorio
2. Configure el entorno local
3. Ejecute las pruebas: `mvn test`
4. Implemente nuevas funcionalidades
5. Asegúrese de mantener la cobertura de pruebas
6. Cree un Pull Request

## Dependencias Principales

- **Spring Boot**: `3.3.3`
- **SpringDoc OpenAPI**: `2.3.0`
- **Lombok**: `1.18.30`
- **MapStruct**: `1.5.3.Final`
- **JWT**: `0.11.5`
- **Caffeine Cache**: `3.1.8`

### Dependencias de Testing:
- **JUnit Jupiter**: `5.9.3`
- **Cucumber**: `7.18.0`
- **Embedded MongoDB**: `4.6.1`

---

**Versión**: 1.0-SNAPSHOT  
**Última actualización**: Agosto 2025  
**Mantenido por**: Equipo GruasTreMart/WebTechnologySoftware