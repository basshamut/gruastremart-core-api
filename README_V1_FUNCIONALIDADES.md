# GruasTreMart Core API - Funcionalidades V1

## Descripción General

**GruasTreMart Core API** es una aplicación Spring Boot que proporciona servicios backend para una plataforma de gestión de grúas. La API permite gestionar demandas de grúas, operadores, usuarios, precios y comunicaciones entre clientes y operadores de grúas.

## Stack Tecnológico

- **Java 21**
- **Spring Boot 3.3.3**
- **MongoDB** (Base de datos NoSQL)
- **JWT Authentication** (Autenticación basada en tokens)
- **Spring Security** (Seguridad y autorización)
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

## Configuración de Entornos

La aplicación soporta múltiples entornos:

- **local**: Desarrollo local
- **test**: Pruebas automatizadas
- **des**: Desarrollo/Staging
- **prod**: Producción

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

## Despliegue

### 🐳 Docker

La aplicación incluye:
- `Dockerfile` para contenedorización
- Configuración multi-stage para optimización
- Variables de entorno para configuración

### ⚙️ Requisitos del Sistema

- Java 21+
- MongoDB 4.4+
- Maven 3.8+
- Docker (opcional)

## Autenticación de Prueba

Para pruebas y desarrollo:

- **Usuario**: `test@test.com`
- **Contraseña**: `bWlDb250cmFzZcOxYTEyMw==` (Base64 de `miContraseña123`)

## Logs y Monitoreo

- **Logback**: Sistema de logging configurable
- **Logs WebSocket**: Tracking de conexiones en tiempo real
- **Structured Logging**: Logs estructurados para análisis

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

### 🚧 En Desarrollo

- 🚧 WebSocket para comunicación en tiempo real
- 🚧 Sistema de notificaciones push
- 🚧 Dashboard de métricas
- 🚧 Integración con sistemas de pago

## Contribución

Para contribuir al proyecto:

1. Clone el repositorio
2. Configure el entorno local
3. Ejecute las pruebas: `mvn test`
4. Implemente nuevas funcionalidades
5. Asegúrese de mantener la cobertura de pruebas

---

**Versión**: 1.0-SNAPSHOT  
**Última actualización**: Agosto 2025  
**Mantenido por**: Equipo GruasTreMart
