# Etapa de construcción: compila la aplicación usando Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Actualizar apk e instalar Maven
RUN apk update && apk add --no-cache maven

# Copiar los archivos de configuración y código
COPY pom.xml .
COPY src ./src

# Compilar el proyecto omitiendo los tests
RUN mvn clean package -DskipTests

# Etapa final: imagen para ejecutar la aplicación
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Instalar curl para descargar el agente de OpenTelemetry
RUN apk add --no-cache curl

# Descargar el agente de OpenTelemetry
ARG OTEL_AGENT_VERSION=2.9.0
RUN curl -L -o opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent-${OTEL_AGENT_VERSION}.jar"

# Copiar el JAR generado desde la etapa build
COPY --from=build /app/target/gruastremart-core-api-1.0-SNAPSHOT.jar app.jar

# Variables de entorno por defecto para OpenTelemetry (pueden ser sobrescritas en K8s)
ENV OTEL_SERVICE_NAME=gruastremart-core-api
ENV OTEL_SERVICE_VERSION=1.0-SNAPSHOT
ENV OTEL_RESOURCE_ATTRIBUTES="service.name=gruastremart-core-api,service.version=1.0-SNAPSHOT"
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_INSTRUMENTATION_MICROMETER_ENABLED=true
ENV OTEL_INSTRUMENTATION_SPRING_WEB_ENABLED=true
ENV OTEL_INSTRUMENTATION_SPRING_WEBMVC_ENABLED=true
ENV OTEL_INSTRUMENTATION_MONGO_ENABLED=true
ENV OTEL_INSTRUMENTATION_HTTP_CLIENT_ENABLED=true
ENV OTEL_INSTRUMENTATION_JDBC_ENABLED=true

EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]
