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
- `POST /api/v1/auth/forgot-password` - Iniciar recuperación de contraseña
- `POST /api/v1/auth/reset-password` - Restablecer contraseña
- `POST /api/v1/auth/change-password` - Cambiar contraseña del usuario

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

### 💳 9. Sistema de Pagos (`PaymentController`)

Gestión de pagos del servicio con comprobantes y flujo de verificación.

#### Características:
- **Pago pre-servicio** (`submit-pre-service`): pago antes de la ejecución del servicio.
- **Pago post-servicio** (`register`): flujo retrocompatible posterior al servicio.
- **Comprobantes**: carga de imagen de pago vía `multipart/form-data` y almacenamiento en Cloudinary.
- **Verificación/Rechazo**: el administrador aprueba (`verify`) o rechaza (`reject`) cada pago.
- **Consultas**: listado general, por operador, y detalle.
- **Estados**: `PENDING · VERIFIED · REJECTED`. **Tipos**: `PRE_SERVICE · POST_SERVICE`.

#### Endpoints:
- `POST /api/v1/payments/register` - Registrar pago post-servicio con comprobante
- `POST /api/v1/payments/submit-pre-service` - Registrar pago pre-servicio
- `GET /api/v1/payments` - Listar pagos con filtros
- `GET /api/v1/payments/operator/{operatorId}` - Pagos de un operador
- `GET /api/v1/payments/all` - Todos los pagos
- `GET /api/v1/payments/{id}` - Detalle de un pago
- `PATCH /api/v1/payments/{id}/verify` - Verificar/aprobar pago
- `PATCH /api/v1/payments/{id}/reject` - Rechazar pago

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

La aplicación usa **un único `application.yml`**: todas las propiedades se resuelven desde variables de entorno (el perfil ya no se usa). Configuración completa en [`.env.example`](./.env.example).

```env
# Base de datos MongoDB
MONGODB_URI=mongodb+srv://...
MONGODB_TEST_URL=mongodb+srv://...   # Solo usada por tests de integración (Cucumber)

# SMTP / email
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
EMAIL_USER=your_email@gmail.com
EMAIL_PASSWORD=your_app_password
EMAIL_FORGOT_PASSWORD_LINK=https://your-frontend.com/reset-password
MAIL_DEBUG=false

# Supabase
SUPABASE_SECURITY_SECRET_KEY=your_secret_key
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your_anon_key

# Cloudinary (app.image.storage=cloudinary)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
IMAGE_STORAGE=cloudinary

# Mailer
MAILER_FROM=your_email@gmail.com
MAILER_TO=tremartca@gmail.com
MAILER_CONTACT_SUBJECT=Nuevo mensaje de contacto
MAILER_DEMAND_SUBJECT=Solicitud de Atención Asignada

# Observabilidad (OpenTelemetry -> SigNoz)
OTEL_EXPORTER_OTLP_ENDPOINT=http://signoz-otel-collector:4317
```

### 🐳 Docker

La aplicación incluye un `Dockerfile` multi-stage que incorpora el agente **OpenTelemetry Java**:

```bash
# Construir imagen
docker build -t gruastremart-core-api:latest .

# Ejecutar contenedor
docker run -p 8080:8080 \
  -e MONGODB_URI=your_mongodb_url \
  -e EMAIL_USER=your_email@gmail.com \
  -e EMAIL_PASSWORD=your_app_password \
  -e SUPABASE_SECURITY_SECRET_KEY=your_secret_key \
  -e SUPABASE_URL=https://your-project.supabase.co \
  -e SUPABASE_ANON_KEY=your_anon_key \
  -e EMAIL_FORGOT_PASSWORD_LINK=https://your-frontend.com/reset-password \
  -e CLOUDINARY_CLOUD_NAME=your_cloud_name \
  -e CLOUDINARY_API_KEY=your_api_key \
  -e CLOUDINARY_API_SECRET=your_api_secret \
  -e MAILER_FROM=your_email@gmail.com \
  -e MAILER_TO=tremartca@gmail.com \
  -e MAILER_CONTACT_SUBJECT="Nuevo mensaje de contacto" \
  -e MAILER_DEMAND_SUBJECT="Solicitud de Atención Asignada" \
  -e OTEL_EXPORTER_OTLP_ENDPOINT=http://signoz-otel-collector:4317 \
  gruastremart-core-api:latest
```

### 📈 Observabilidad (SigNoz)

La telemetría (trazas + métricas) se envía vía OTLP al colector de **SigNoz** usando el agente OpenTelemetry incluido en la imagen:

- El `Dockerfile` descarga `opentelemetry-javaagent.jar` y arranca la JVM con `-javaagent`.
- El endpoint del colector se configura con `OTEL_EXPORTER_OTLP_ENDPOINT` (default `http://signoz-otel-collector:4317`, gRPC).
- Instrumentación automática: Spring Web/MVC, MongoDB, HTTP client, Micrometer.
- En **Coolify**, el endpoint depende de cómo se expone el colector:
  - **Misma red Docker**: `OTEL_EXPORTER_OTLP_ENDPOINT=http://signoz-otel-collector:4317` con `OTEL_EXPORTER_OTLP_PROTOCOL=grpc`.
  - **Vía proxy de Coolify (URL pública)**: `OTEL_EXPORTER_OTLP_ENDPOINT=http://otelcollectorhttp-<hash>.<ip>.sslip.io` con `OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf` (el proxy expone el 4318 del colector en el puerto 80).
- Logs con `traceId`/`spanId` para correlación (patrón en `application.yml`).

### 🤖 MCP de SigNoz (opencode/Claude)

Para consultar SigNoz desde el agente vía MCP (binario `signoz-mcp-server`):

```jsonc
"signoz": {
  "type": "local",
  "command": ["/path/to/signoz-mcp-server"],
  "environment": {
    "SIGNOZ_URL": "http://<tu-instancia-signoz>",
    "SIGNOZ_API_KEY": "<api-key>",
    "LOG_LEVEL": "info"
  },
  "enabled": true
}
```

- La API key se crea en SigNoz → **Settings → API Keys**.
- Herramientas disponibles: `signoz_list_services`, `signoz_query_metrics`, `signoz_search_traces`, `signoz_search_logs`, `signoz_list_alerts`, dashboards, etc.
- Verifica con: *"lista los servicios en SigNoz"*.

### 🏥 Health Checks

La aplicación incluye un endpoint de salud expuesto públicamente (whitelisted en Spring Security):

- `/gruastremart-core-api/actuator/health` - Estado general

> Nota: el resto de endpoints de Actuator (`prometheus`, `metrics`, `traces`, `info`) se deshabilitaron; solo `health` queda expuesto.

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
- ✅ Sistema de pagos (pre-servicio y post-servicio) con comprobantes vía Cloudinary y flujo de verificación/rechazo
- ✅ Cancelación y completado de demandas

### 🚧 En Desarrollo / Pendiente

- 🚧 Dashboard de métricas y analytics
- 🚧 API de reportes y estadísticas
- 🚧 Sistema de calificaciones y reviews
- 🚧 Migración de polling a WebSocket/SSE para tiempo real
- 🚧 Tests unitarios de `PaymentService`/`PaymentController`

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
**Última actualización**: Agosto 2026  
**Mantenido por**: Equipo GruasTreMart/WebTechnologySoftware