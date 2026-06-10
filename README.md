# CircularMarket

CircularMarket es una web de tienda online creada como proyecto final de Desarrollo de Aplicaciones Web.

La aplicación permite registrarse, iniciar sesión, ver productos, añadirlos al carrito y realizar pedidos con una pasarela de pago en modo de pruebas. También tiene un panel de administración para gestionar usuarios, productos, categorías y pedidos.

Actualmente, el proyecto está enfocado en la venta de productos gestionados desde la propia tienda. Como mejora futura, se plantea añadir un sistema de compraventa C2C de productos de segunda mano, donde los usuarios puedan publicar y comprar productos entre ellos.

---

## Índice

* [Descripción del proyecto](#descripción-del-proyecto)
* [Funcionalidades](#funcionalidades)
* [Tecnologías utilizadas](#tecnologías-utilizadas)
* [Diseño visual](#diseño-visual)
* [Estructura del repositorio](#estructura-del-repositorio)
* [Base de datos](#base-de-datos)
* [Configuración](#configuración)
* [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
* [Partes principales del proyecto](#partes-principales-del-proyecto)
* [Seguridad](#seguridad)
* [Pagos](#pagos)
* [Estadísticas con Python](#estadísticas-con-python)
* [Mejoras futuras](#mejoras-futuras)
* [Autor](#autor)

---

## Descripción del proyecto

CircularMarket está pensada para una tienda local que quiere vender sus productos por internet.

El proyecto tiene dos partes principales:

* Una zona para usuarios, donde pueden ver productos, usar el carrito y hacer pedidos.
* Una zona de administración, desde donde se gestionan los datos principales de la tienda.

La idea principal es tener una web sencilla, clara y funcional para gestionar una tienda online básica. Más adelante, el proyecto podría ampliarse con una parte C2C para que los usuarios también puedan vender productos de segunda mano entre ellos.

---

## Funcionalidades

### Usuario

* Registro de usuario.
* Inicio de sesión con email y contraseña.
* Inicio de sesión con Google.
* Recuperación de contraseña por correo.
* Consulta de productos.
* Búsqueda de productos.
* Vista detalle de cada producto.
* Añadir productos al carrito.
* Cambiar cantidades del carrito.
* Eliminar productos del carrito.
* Realizar pedidos.
* Pago con Stripe en modo de pruebas.
* Consulta de pedidos realizados.

### Administrador

* Acceso a un panel privado.
* Gestión de usuarios.
* Gestión de productos.
* Gestión de categorías.
* Gestión de pedidos.
* Cambio de estado de pedidos.
* Consulta de datos básicos del sistema.
* Visualización de estadísticas.

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

* Stripe

### Correo

* Spring Mail
* SMTP de Gmail

### Estadísticas

* Python
* pandas
* matplotlib
* MySQL Connector

### Herramientas

* Maven
* Git
* GitHub
* IntelliJ IDEA / VS Code
* MySQL Workbench

---

## Diseño visual

La interfaz de CircularMarket usa un diseño claro, con fondos suaves, tarjetas blancas, tonos azules para botones y grises para textos y bordes.

### Paleta de colores

<p align="center">
  <img src="docs/img/paleta-colores.png" alt="Paleta de colores de CircularMarket" width="900">
</p>

### Tipografía

La web usa una tipografía sans-serif mediante TailwindCSS, principalmente con la clase `font-sans`.

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

### Carpetas principales

* `docs/`: imágenes y recursos usados en la documentación.
* `demo/`: proyecto principal de Spring Boot.
* `config/`: configuración del proyecto.
* `controller/`: rutas y controladores de la web.
* `dto/`: clases usadas para mover datos entre partes del proyecto.
* `model/`: entidades de la base de datos.
* `repository/`: consultas y acceso a la base de datos.
* `security/`: configuración de seguridad, login y roles.
* `service/`: lógica principal de la aplicación.
* `templates/`: vistas HTML con Thymeleaf.
* `static/`: imágenes, JavaScript y otros archivos estáticos.
* `estadisticas_python/`: scripts de Python para generar estadísticas.

---

## Base de datos

El proyecto usa MySQL.

Tablas principales:

* `usuarios`
* `rolusuarios`
* `productos`
* `categoriaproductos`
* `carritos`
* `itemscarrito`
* `pedidos`
* `pagos`

Estas tablas guardan la información de usuarios, productos, categorías, carritos, pedidos y pagos.

---

## Configuración

El archivo principal de configuración está en:

```text
demo/src/main/resources/application.properties
```

En este archivo se configura:

* Conexión con MySQL.
* Stripe.
* Correo electrónico.
* Login con Google.
* Configuración de JPA/Hibernate.

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/circularmarket
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

stripe.secret.key=TU_CLAVE_DE_STRIPE
stripe.public.key=TU_CLAVE_PUBLICA_DE_STRIPE

spring.mail.username=TU_CORREO
spring.mail.password=TU_PASSWORD_DE_APLICACION

spring.security.oauth2.client.registration.google.client-id=TU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=TU_CLIENT_SECRET
```

---

## Cómo ejecutar el proyecto

### Requisitos

* Java instalado.
* Maven instalado.
* MySQL instalado.
* Base de datos `circularmarket` creada.
* Archivo `application.properties` configurado.

### Pasos

Entrar en la carpeta del proyecto:

```bash
cd demo
```

Ejecutar Spring Boot:

```bash
mvn spring-boot:run
```

Abrir la web en el navegador:

```text
http://localhost:8080
```

---

## Partes principales del proyecto

### Inicio y productos

Muestra la página principal, el listado de productos y la vista detalle de cada producto.

### Usuarios

Gestiona el registro, login, cierre de sesión, recuperación de contraseña y acceso con Google.

### Carrito

Permite añadir productos, cambiar cantidades, eliminar productos y revisar el pedido antes de pagar.

### Pedidos

Guarda los pedidos realizados por los usuarios.

### Pagos

Integra Stripe en modo de pruebas para simular el pago de los pedidos.

### Panel administrativo

Permite al administrador controlar usuarios, productos, categorías y pedidos.

### Estadísticas

Incluye scripts de Python para generar gráficas a partir de datos de la base de datos.

---

## Seguridad

La seguridad se gestiona con Spring Security.

El proyecto incluye:

* Login con email y contraseña.
* Login con Google.
* Contraseñas cifradas.
* Control de acceso por roles.
* Rutas protegidas para el panel de administración.
* Validaciones en formularios.
* Operaciones importantes controladas desde el servidor.

---

## Pagos

El proyecto usa Stripe en modo de pruebas.

Flujo básico:

1. El usuario revisa el carrito.
2. Pulsa para pagar.
3. Se abre el checkout de Stripe.
4. Stripe confirma si el pago se ha completado.
5. Se guarda el pedido.
6. Se guarda la información del pago.
7. Se vacía el carrito.

---

## Estadísticas con Python

El proyecto incluye una carpeta de Python para generar estadísticas.

Se usa para consultar datos de la base de datos y crear gráficas, por ejemplo sobre usuarios registrados.

Tecnologías usadas:

* Python
* pandas
* matplotlib
* MySQL Connector

---

## Mejoras futuras

Algunas mejoras que se podrían añadir más adelante:

* Sistema de compraventa C2C para productos de segunda mano entre usuarios.
* Verificación completa del correo al registrarse.
* Valoraciones y reseñas de productos.
* Apartado de incidencias dentro de la web.
* Más métodos de pago, como Bizum o transferencia.
* Notificaciones para usuarios.
* Más estadísticas en el panel de administración.
* Despliegue en un servidor real.
* Dominio propio y HTTPS.
* Automatización del despliegue.

---

## Autor

Proyecto desarrollado por Ángel Nieto Cordero para el ciclo de Desarrollo de Aplicaciones Web.
