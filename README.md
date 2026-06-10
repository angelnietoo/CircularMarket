# CircularMarket

CircularMarket es una aplicación web de comercio electrónico orientada a una tienda local. El proyecto permite a los usuarios registrarse, iniciar sesión, consultar productos, añadir artículos al carrito y realizar pedidos mediante una pasarela de pago integrada en modo de pruebas.

El sistema también incluye un panel administrativo desde el que se pueden gestionar usuarios, productos, categorías, pedidos e indicadores básicos del sistema.

---

## Índice

* [Descripción del proyecto](#descripción-del-proyecto)
* [Funcionalidades principales](#funcionalidades-principales)
* [Tecnologías utilizadas](#tecnologías-utilizadas)
* [Diseño visual](#diseño-visual)
* [Estructura del repositorio](#estructura-del-repositorio)
* [Base de datos](#base-de-datos)
* [Configuración del proyecto](#configuración-del-proyecto)
* [Ejecución en local](#ejecución-en-local)
* [Módulos principales](#módulos-principales)
* [Seguridad](#seguridad)
* [Pagos](#pagos)
* [Análisis de datos](#análisis-de-datos)
* [Mejoras futuras](#mejoras-futuras)

---

## Descripción del proyecto

CircularMarket nace como una solución web para digitalizar la venta de productos de una tienda local. La aplicación centraliza el catálogo de productos, la gestión de usuarios, el carrito de compra, los pedidos y el pago online.

El objetivo principal del proyecto es desarrollar una plataforma funcional, sencilla y segura, donde el usuario pueda comprar productos de forma clara y donde el administrador pueda controlar el funcionamiento general de la tienda desde un panel privado.

---

## Funcionalidades principales

### Usuario

* Registro de nuevos usuarios.
* Inicio de sesión con email y contraseña.
* Inicio de sesión mediante Google OAuth2.
* Recuperación de contraseña mediante correo electrónico.
* Consulta del catálogo de productos.
* Búsqueda y filtrado de productos.
* Visualización del detalle de cada producto.
* Añadir productos al carrito.
* Modificar cantidades del carrito.
* Vaciar el carrito.
* Realizar pedidos.
* Pago mediante Stripe en modo de pruebas.
* Consulta de pedidos realizados.

### Administrador

* Acceso a panel administrativo protegido.
* Gestión de usuarios.
* Gestión de productos.
* Gestión de categorías.
* Gestión de pedidos.
* Cambio de estado de pedidos.
* Visualización de indicadores básicos del sistema.
* Consulta de estadísticas relacionadas con usuarios.

---

## Tecnologías utilizadas

### Front-end

* Thymeleaf
* HTML5
* TailwindCSS
* JavaScript

### Back-end

* Java
* Spring Boot
* Spring MVC
* Spring Security
* Spring Data JPA
* Hibernate

### Base de datos

* MySQL

### Pagos

* Stripe en modo de pruebas

### Autenticación y seguridad

* Spring Security
* OAuth2 con Google
* Control de acceso por roles
* Cifrado de contraseñas

### Correo electrónico

* Spring Mail
* SMTP de Gmail

### Análisis de datos

* Python
* pandas
* matplotlib
* MySQL Connector

### Gestión del proyecto

* Maven
* Git
* GitHub

---

## Diseño visual

CircularMarket utiliza una interfaz limpia, moderna y responsive. El diseño se basa en una paleta de colores clara, tonos azules para acciones principales y una tipografía sans-serif sencilla y legible.

### Paleta de colores

La paleta de colores del proyecto se basa principalmente en tonos claros, azules y grises. Los tonos claros se utilizan para fondos, tarjetas y superficies. Los azules se emplean en botones, enlaces, acciones principales y elementos destacados. También se utilizan colores de apoyo para mensajes de éxito, avisos y estados importantes.

<p align="center">
  <img src="docs/img/paleta-colores.png" alt="Paleta de colores de CircularMarket" width="900">
</p>

### Tipografía

La tipografía utilizada en CircularMarket se basa en una fuente sans-serif mediante las clases de TailwindCSS, principalmente `font-sans`, que utiliza la familia tipográfica del sistema. Esto permite mantener una apariencia limpia, legible y coherente en toda la aplicación.

La jerarquía tipográfica diferencia entre títulos principales, subtítulos, texto de cuerpo, botones, etiquetas y elementos secundarios de la interfaz.

<p align="center">
  <img src="docs/img/tipografia.png" alt="Guía tipográfica de CircularMarket" width="900">
</p>

---

## Estructura del repositorio

```text
CircularMarket/
│
├── README.md
├── docs/
│   └── img/
│       ├── paleta-colores.png
│       └── tipografia.png
│
└── demo/
    ├── pom.xml
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/circularmarket/demo/
    │   │   │       ├── config/
    │   │   │       ├── controller/
    │   │   │       ├── dto/
    │   │   │       ├── model/
    │   │   │       ├── repository/
    │   │   │       ├── security/
    │   │   │       └── service/
    │   │   │
    │   │   └── resources/
    │   │       ├── static/
    │   │       ├── templates/
    │   │       └── application.properties
    │   │
    │   └── test/
    │
    └── estadisticas_python/
```

### Explicación de carpetas principales

* `docs/`: contiene recursos utilizados para la documentación del proyecto.
* `docs/img/`: contiene las imágenes utilizadas en este README.
* `demo/`: contiene la aplicación Spring Boot.
* `demo/src/main/java/`: contiene el código Java del back-end.
* `config/`: configuración general del proyecto.
* `controller/`: controladores encargados de gestionar las rutas de la aplicación.
* `dto/`: objetos utilizados para transferir datos entre capas.
* `model/`: entidades principales del sistema.
* `repository/`: interfaces de acceso a base de datos.
* `security/`: configuración relacionada con autenticación, autorización y roles.
* `service/`: lógica de negocio de la aplicación.
* `templates/`: vistas HTML desarrolladas con Thymeleaf.
* `static/`: recursos estáticos como imágenes, JavaScript o archivos generados.
* `estadisticas_python/`: scripts de Python utilizados para generar estadísticas.

---

## Base de datos

El proyecto utiliza MySQL como sistema gestor de base de datos.

Principales tablas del sistema:

* `usuarios`
* `rolusuarios`
* `productos`
* `categoriaproductos`
* `carritos`
* `itemscarrito`
* `pedidos`
* `pagos`

La base de datos permite almacenar la información necesaria para gestionar usuarios, roles, productos, categorías, carritos, pedidos y pagos.

---

## Configuración del proyecto

Antes de ejecutar el proyecto, es necesario configurar el archivo:

```text
demo/src/main/resources/application.properties
```

Este archivo debe contener la configuración de conexión con MySQL, correo electrónico, OAuth2 y Stripe.

Ejemplo orientativo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/circularmarket
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

stripe.secret.key=TU_CLAVE_SECRETA_DE_STRIPE
stripe.public.key=TU_CLAVE_PUBLICA_DE_STRIPE

spring.mail.username=TU_CORREO
spring.mail.password=TU_CONTRASEÑA_DE_APLICACION

spring.security.oauth2.client.registration.google.client-id=TU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=TU_CLIENT_SECRET
```

Por seguridad, las claves reales no deben subirse al repositorio público.

---

## Ejecución en local

### Requisitos previos

* Java instalado.
* Maven instalado.
* MySQL instalado.
* Base de datos `circularmarket` creada.
* Claves de configuración añadidas en `application.properties`.

### Pasos de ejecución

Desde la raíz del repositorio:

```bash
cd demo
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

Una vez iniciada, la aplicación estará disponible en:

```text
http://localhost:8080
```

---

## Módulos principales

### Inicio y catálogo

Permite mostrar la página principal, los productos disponibles y el detalle de cada producto.

### Usuarios y autenticación

Gestiona el registro, inicio de sesión, cierre de sesión, recuperación de contraseña y autenticación mediante Google.

### Carrito

Permite añadir productos, modificar cantidades, eliminar artículos y revisar el resumen de compra.

### Pedidos

Gestiona la creación de pedidos una vez completado el proceso de compra.

### Pagos

Integra Stripe en modo de pruebas para simular pagos online.

### Panel administrativo

Permite al administrador gestionar usuarios, productos, categorías y pedidos desde una zona privada.

### Estadísticas

Incluye scripts de Python para generar datos y gráficas relacionadas con el uso del sistema.

---

## Seguridad

El proyecto implementa seguridad mediante Spring Security.

Medidas principales:

* Contraseñas cifradas.
* Control de acceso por roles.
* Rutas públicas y privadas diferenciadas.
* Panel administrativo protegido.
* Autenticación con email y contraseña.
* Autenticación externa con Google OAuth2.
* Validación de formularios.
* Protección de operaciones críticas desde el servidor.

---

## Pagos

CircularMarket utiliza Stripe como pasarela de pago en modo de pruebas.

El flujo de pago permite:

1. Revisar el carrito.
2. Iniciar el checkout.
3. Redirigir al usuario a Stripe.
4. Confirmar el resultado del pago.
5. Registrar el pedido.
6. Guardar la información básica del pago.
7. Vaciar el carrito tras la compra.

---

## Análisis de datos

El proyecto incluye una parte de análisis de datos mediante Python.

Se utilizan scripts para obtener información de la base de datos y generar estadísticas relacionadas con los usuarios registrados.

Tecnologías utilizadas en esta parte:

* Python
* pandas
* matplotlib
* MySQL Connector

Las gráficas generadas pueden utilizarse como apoyo para el panel administrativo o para la documentación del proyecto.

---

## Mejoras futuras

Algunas mejoras planteadas para futuras versiones son:

* Verificación completa de correo electrónico.
* Sistema de valoraciones y reseñas.
* Gestión de incidencias dentro de la plataforma.
* Integración con otros métodos de pago como Bizum o transferencia.
* Sistema de notificaciones.
* Mejoras avanzadas de analítica.
* Despliegue en servidor real con dominio y HTTPS.
* Automatización del despliegue.
* Optimización del rendimiento y escalabilidad.

---

## Autor

Proyecto desarrollado por Ángel Nieto Cordero como Proyecto Intermodular del ciclo de Desarrollo de Aplicaciones Web.
