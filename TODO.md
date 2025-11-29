# TODO: Migración a Render Gratuito - Eliminando Eureka/Config

## Información Recopilada
- Proyecto Spring Boot Maven Java 17.
- Arquitectura actual: 7 servicios (config, eureka, gateway, user, task, note, event).
- Para gratuito: Eliminar config y eureka, usar URLs fijas internas y vars de entorno.
- BD: Migrar de Oracle a PostgreSQL (agregar dependencia, actualizar configs).
- Gateway: Desactivar Eureka, rutas a nombres internos (ej. http://msvc-user:8080).
- Docker: Dockerfile genérico multi-stage, build sin tests.
- Render: render.yaml con DB PostgreSQL gratuita, inyección automática de vars.

## Plan
- [x] Actualizar pom.xml: Agregar PostgreSQL, remover Oracle si existe.
- [x] Crear Dockerfile genérico en raíz.
- [x] Modificar application.yml del gateway: Desactivar Eureka, agregar rutas manuales.
- [x] Actualizar application.properties en servicios: Usar driver PostgreSQL y vars de entorno.
- [x] Actualizar render.yaml: Incluir DB, servicios con vars inyectadas.

## Followup
- [x] Verificar cambios críticos: pom.xml, Dockerfile, gateway config, render.yaml.
- [x] Testing crítico: Solo elementos clave (build, configs básicas).
