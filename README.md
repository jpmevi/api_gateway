# MAXI CLONE

**MAXI CLONE** es un sistema de microservicios diseñado para gestionar un ecosistema de comercio electrónico orientado a la logística, incluyendo la administración de pedidos, envíos, devoluciones, incidencias y notificaciones. Este proyecto aprovecha la infraestructura de Google Cloud Platform (GCP) y se despliega en Google Kubernetes Engine (GKE) para asegurar alta disponibilidad, escalabilidad y balanceo de carga mediante la integración con Eureka y un API Gateway.

---

## Índice

- [Arquitectura General](#arquitectura-general)
- [Descripción de los Componentes](#descripción-de-los-componentes)
- [Microservicios](#microservicios)
- [Colas y Mensajería](#colas-y-mensajería)
- [Diagrama de Base de Datos](#diagrama-de-base-de-datos)
- [Diagrama de Arquitectura](#diagrama-de-arquitectura)
- [Diagrama del Planteamiento](#diagrama-del-planteamiento)
- [Detalles de Conexión](#detalles-de-conexión)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso](#uso)
- [Contribuciones](#contribuciones)
- [Licencia](#licencia)

---

## Arquitectura General

MAXI CLONE está diseñado con una arquitectura de microservicios desacoplada, donde cada funcionalidad principal (usuarios, productos, pedidos, envíos, devoluciones e incidencias) está manejada por un microservicio independiente. Todos los microservicios están desplegados como pods en **Google Kubernetes Engine (GKE)** y se comunican a través de un **API Gateway** con balanceo de carga. El descubrimiento de servicios es gestionado por **Eureka**.

### Componentes Clave

1. **Google Kubernetes Engine (GKE)**: Alojamiento y orquestación de contenedores.
2. **App Engine para el Frontend**: Interfaz de usuario accesible desde una IP pública.
3. **Eureka Service Discovery**: Módulo de descubrimiento de servicios que permite la localización dinámica de microservicios.
4. **API Gateway**: Puerta de enlace que facilita la comunicación con los microservicios, implementando un balanceo de carga.
5. **RabbitMQ**: Maneja las colas de mensajes para las notificaciones y pedidos, asegurando comunicación asíncrona y confiable.
6. **Autenticación con JWT**: Servicio de autenticación centralizada que emite tokens JWT para validar las credenciales de acceso a los microservicios.

---

## Descripción de los Componentes

MAXI CLONE integra diversos componentes especializados para gestionar tareas críticas del sistema:

- **Authentication Service**: Verifica la identidad del usuario y proporciona tokens JWT. Todos los microservicios verifican el token JWT antes de procesar las solicitudes, garantizando que solo los usuarios autenticados puedan interactuar con el sistema.
- **API Gateway**: Conexión central para todos los servicios. Este gateway no solo distribuye la carga de tráfico, sino que también gestiona la autenticación y el routing de las peticiones a los microservicios correctos.

Cada microservicio está desplegado como un `Pod` en GKE y está diseñado para escalar horizontalmente de acuerdo con la demanda.

---

## Microservicios

### User Service
- **Funcionalidades**: CRUD de usuarios, autenticación y asignación de roles (Store, Supervisor, Warehouse, Administrator).
- **Descripción**: Este servicio maneja toda la lógica relacionada con los usuarios y sus roles. Además, permite la exportación de reportes en PDF basados en el tipo de usuario.

### Catalog Service
- **Funcionalidades**: CRUD de productos, validación de stock, y gestión del catálogo general y local.
- **Descripción**: Gestiona el inventario y asegura que el catálogo esté sincronizado con la disponibilidad de productos en las tiendas.

### Store Service
- **Funcionalidades**: CRUD de almacenes y tiendas.
- **Descripción**: Proporciona la capacidad de administrar las tiendas físicas y almacenes dentro del sistema.

### Order Service
- **Funcionalidades**: CRUD de pedidos, aprobación o rechazo de órdenes.
- **Descripción**: Facilita la administración de órdenes desde su creación hasta su estado final. Incluye la capacidad de exportar reportes de órdenes.

### Shipment Service
- **Funcionalidades**: CRUD de envíos, generación y recepción de envíos.
- **Descripción**: Administra el flujo de envíos y recibe actualizaciones de estado en tiempo real. Permite la generación de reportes de envíos.

### Incident Service
- **Funcionalidades**: CRUD de incidencias, apertura y cierre de incidentes.
- **Descripción**: Registro y manejo de incidencias asociadas con productos, envíos, y devoluciones.

### Devolution Service
- **Funcionalidades**: CRUD de devoluciones, aceptación o rechazo de devoluciones.
- **Descripción**: Gestiona el ciclo de vida de las devoluciones de productos, desde su creación hasta su resolución.

### Notification Service
- **Funcionalidades**: Envío de notificaciones y correos electrónicos.
- **Descripción**: Responsable de enviar notificaciones y alertas a los usuarios del sistema, utilizando colas de mensajes para optimizar la comunicación.

### Report Service
- **Funcionalidades**: Generación de reportes en PDF.
- **Descripción**: Centraliza la generación de reportes y permite exportarlos en formatos como PDF utilizando plantillas personalizadas.

---

## Colas y Mensajería

- **RabbitMQ** se utiliza para la comunicación asíncrona entre los microservicios. Las colas de **Notificaciones** y **Pedidos** ayudan a distribuir los eventos de manera eficiente, permitiendo que los microservicios funcionen de manera independiente sin bloquear operaciones críticas.

---

## Diagrama de Base de Datos

![Untitled](https://github.com/user-attachments/assets/1daa92ed-860f-436a-89aa-9f76ab7ec0c2)


---

## Diagrama de Arquitectura

![MAXI CLONE drawio](https://github.com/user-attachments/assets/f0343de4-0754-4205-b1ce-d17c5635b659)


---

## Diagrama del Planteamiento

![image](https://github.com/user-attachments/assets/434da3d7-540d-4924-88c9-0f1ad5242568)


---

## Detalles de Conexión

- **Frontend (App Engine)**: [35.196.192.110](http://35.196.192.110)
- **RabbitMQ**: [34.73.156.126:15672](http://34.73.156.126:15672)
- **Eureka**: [34.138.194.76:8761](http://34.138.194.76:8761)
- **API Gateway**: [35.237.124.228](http://35.237.124.228)

---

## Requisitos Previos

- **Google Cloud SDK**: Para gestionar los recursos en GCP.
- **kubectl**: Para la administración de clusters en GKE.
- **Docker**: Para construir y desplegar las imágenes de los microservicios.
- **RabbitMQ**: Para la configuración de colas de mensajes en el sistema.

---

## Instalación y Configuración

### Clonar el Repositorio

```bash
git clone https://github.com/jpmevi/api_gateway.git
cd api_gateway
