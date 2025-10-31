# Sistema de Gestión de Alojamientos Rurales

API REST para la gestión de reservas y alojamientos rurales, construida con Spring Boot siguiendo arquitectura hexagonal (puertos y adaptadores).

## Tecnologías

- **Java** (Spring Boot)
- **Maven** - Gestión de dependencias
- **PostgreSQL** - Base de datos
- **Arquitectura Hexagonal** - Arquitectura de puertos y servicios

## Funcionalidades

### Gestión de Alojamientos
- Crear y actualizar alojamientos rurales
- Búsqueda y consulta de disponibilidad
- Gestión de tipos de alojamiento y capacidad

### Gestión de Reservas
- Crear reservas con validación de disponibilidad
- Actualizar y cancelar reservas
- Control de conflictos de fechas
- Verificación de capacidad

### Gestión de Usuarios
- **Clientes**: Registro y gestión de usuarios
- **Administradores**: Gestión del sistema

## Arquitectura

El proyecto sigue los principios de **Arquitectura Hexagonal**:

- **Domain**: Modelos, value objects, excepciones
- **Application**: Puertos (interfaces) y servicios
- **Infrastructure**: Controllers, DTOs, configuración
 
## Estado del Proyecto
En desarrollo activo

## Autor
Antonio Ortega


### NOTA:

⚠️ Nota: Este documento está sujeto a cambios. 
La información contenida aquí puede actualizarse o modificarse en futuras versiones del proyecto.