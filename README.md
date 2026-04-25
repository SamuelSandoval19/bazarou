# 🌶️ Bazarou — El tianguis digital de México

> Compra, vende y regatea como en el mercado de toda la vida, pero con la seguridad de una plataforma moderna.

Bazarou es un marketplace al estilo de Vinted **diseñado para el mercado mexicano**: regateo (ofertas/contraofertas) integrado, compra protegida con escrow, y una identidad visual cálida inspirada en los tianguis y bazares.

---

## 🛠️ Stack tecnológico

| Capa            | Tecnología |
|-----------------|------------|
| Backend         | **Java 17 + Spring Boot 3.3** |
| Plantillas      | **Thymeleaf** (server-side rendering) |
| Frontend        | **HTML + CSS + JavaScript + jQuery 3.7** |
| Base de datos   | **MySQL 8** (compatible con MariaDB de XAMPP) |
| ORM             | **Spring Data JPA / Hibernate** |
| Seguridad       | **Spring Security** + BCrypt |
| Build           | **Maven** |
| Contenedores    | **Docker + docker-compose** |

---

## ✨ Funcionalidades

- **Registro y login** con email/usuario y BCrypt
- **Publicación de productos** con hasta 8 fotos, drag & drop, categorías, marca, talla, color, ciudad de envío
- **Sistema de regateo (ofertas)**: el comprador propone, el vendedor acepta, rechaza o contraoferta. Mínimo 50% del precio publicado para evitar abuso
- **Compra protegida (escrow)**: el pago queda en custodia hasta que el comprador confirma la entrega
- **Estados de transacción**: `PAGO_RETENIDO → ENVIADO → COMPLETADO` (con disputas y reembolsos)
- **Código de seguimiento único**: `BZR-2025-XXXXXXXX`
- **Búsqueda y filtros**: por categoría, ordenamiento por precio/recientes/populares
- **Favoritos** con contador
- **Perfiles públicos** con calificación y total de ventas
- **12 categorías precargadas** con emojis (Ropa, Electrónicos, Hogar, Libros, Antojitos, etc.)
- **Diseño responsive** con identidad visual mexicana (rosa mexicano + cempasúchil + verde Talavera)

---

## 🚀 Cómo correrlo (XAMPP — tu setup actual)

### 1) Prerrequisitos
- **Java 17+** ([Adoptium](https://adoptium.net/))
- **Maven 3.8+**
- **XAMPP** corriendo con MySQL/MariaDB en el puerto `3306`

### 2) Crear la base de datos
Abre **phpMyAdmin** (`http://localhost/phpmyadmin`) y crea una BD llamada `bazarou`.
> No es estrictamente necesario: la app la crea sola gracias a `createDatabaseIfNotExist=true`. Pero si prefieres ejecutar el script, está en `database/schema.sql`.

### 3) Verificar credenciales
Por defecto XAMPP usa usuario `root` **sin contraseña**. Si tu setup es diferente, edita `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=
```

### 4) Compilar y arrancar
```bash
cd bazarou
mvn spring-boot:run
```

Abre [http://localhost:8080](http://localhost:8080) 🎉

> Las categorías se cargan automáticamente desde `data.sql` la primera vez.

---

## 🐳 Cómo correrlo con Docker

```bash
docker-compose up --build
```

Esto levanta:
- **MySQL 8** en el puerto `3307` del host (para no chocar con XAMPP)
- **Bazarou** en el puerto `8080`

Detener todo:
```bash
docker-compose down
```

---

## 📂 Estructura del proyecto

```
bazarou/
├── pom.xml                              # Dependencias de Maven
├── Dockerfile                           # Build multi-stage
├── docker-compose.yml                   # App + MySQL
├── database/
│   └── schema.sql                       # Schema completo (opcional)
├── src/main/
│   ├── java/com/bazarou/
│   │   ├── BazarouApplication.java      # Entry point
│   │   ├── config/
│   │   │   ├── SecurityConfig.java      # Spring Security
│   │   │   └── WebConfig.java           # Recursos estáticos + currentUser global
│   │   ├── controller/
│   │   │   ├── HomeController.java
│   │   │   ├── AuthController.java
│   │   │   ├── ProductController.java   # Listado / detalle / publicar
│   │   │   ├── OfferController.java     # Sistema de regateo
│   │   │   ├── TransactionController.java # Compra protegida
│   │   │   └── ProfileController.java
│   │   ├── model/                       # Entidades JPA
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Category.java
│   │   │   ├── Offer.java
│   │   │   ├── Transaction.java
│   │   │   ├── Favorite.java
│   │   │   └── Review.java
│   │   ├── repository/                  # Spring Data JPA
│   │   ├── service/                     # Lógica de negocio
│   │   ├── security/
│   │   │   └── CustomUserDetailsService.java
│   │   └── dto/
│   └── resources/
│       ├── application.properties       # Configuración
│       ├── data.sql                     # Categorías iniciales
│       ├── static/
│       │   ├── css/styles.css           # Sistema de diseño completo
│       │   └── js/main.js               # jQuery para interacciones
│       └── templates/                   # Vistas Thymeleaf
│           ├── index.html
│           ├── auth/{login,registro}.html
│           ├── products/{list,detail,new}.html
│           ├── offers/{recibidas,enviadas}.html
│           ├── transactions/{checkout,mis-compras,mis-ventas}.html
│           ├── user/{perfil,editar,favoritos}.html
│           ├── fragments/layout.html    # Navbar + footer reutilizables
│           ├── como-funciona.html
│           ├── sobre-nosotros.html
│           └── error.html
```

---

## 🔑 Rutas principales

| Ruta                            | Descripción                                       |
|---------------------------------|---------------------------------------------------|
| `GET  /`                        | Página principal con productos recientes          |
| `GET  /buscar?q=...`            | Búsqueda con filtros                              |
| `GET  /categoria/{slug}`        | Productos de una categoría                        |
| `GET  /producto/{id}`           | Detalle de producto                               |
| `GET  /vender`                  | Formulario de publicación (auth)                  |
| `POST /vender`                  | Publicar producto                                 |
| `POST /ofertas/nueva`           | Hacer una oferta (regatear)                       |
| `POST /ofertas/{id}/aceptar`    | El vendedor acepta                                |
| `POST /ofertas/{id}/rechazar`   | El vendedor rechaza                               |
| `POST /ofertas/{id}/contraoferta`| El vendedor contraoferta                         |
| `GET  /compra/checkout/{id}`    | Pantalla de compra                                |
| `POST /compra/confirmar`        | Confirma compra protegida                         |
| `POST /compra/{id}/enviar`      | Vendedor marca como enviado (con guía)            |
| `POST /compra/{id}/confirmar-recepcion` | Comprador confirma → libera el pago       |
| `GET  /perfil/{username}`       | Perfil público                                    |
| `GET  /favoritos`               | Mis favoritos                                     |
| `POST /favoritos/toggle/{id}`   | Marcar/desmarcar favorito                         |

---

## 🎨 Sistema de diseño

| Token            | Valor         | Uso |
|------------------|---------------|-----|
| `--rosa-mexicano`| `#E4007C`     | Color principal de marca |
| `--cempasuchil`  | `#F2A900`     | Acento amarillo de Día de Muertos |
| `--talavera`     | `#00875A`     | Verde de azulejos poblanos (success, compra protegida) |
| `--cobalto`      | `#1B4F8C`     | Azul Talavera |
| `--terracota`    | `#C8553D`     | Errores |
| `--crema`        | `#FAF6F0`     | Fondo principal |
| `--carbon`       | `#1F1B16`     | Texto y CTAs |

**Tipografía:**
- **Display:** [Fraunces](https://fonts.google.com/specimen/Fraunces) (serif moderno con personalidad)
- **Body:** [DM Sans](https://fonts.google.com/specimen/DM+Sans) (sans-serif limpio)
- **Acentos:** [Caveat](https://fonts.google.com/specimen/Caveat) (firmas y frases tipo "rotulado de tianguis")

---

## 🔮 Para conectar después (APIs)

Tu proyecto está pensado para integrar más adelante:

- **Google Maps API** → cálculo de envío por distancia, autocompletado de direcciones
- **OpenLibrary API** → autocompletar info al publicar libros (ISBN → título, autor, portada)
- **Pasarelas de pago** → MercadoPago, OpenPay o Conekta para pagos reales (México)
- **Paqueterías** → Estafeta, FedEx, DHL para etiquetas y rastreo en vivo

Las puertas están abiertas en `TransactionService` y `ProductService`.

---

## 📌 Próximos pasos sugeridos

- [ ] Sistema de mensajería interna entre comprador y vendedor
- [ ] Reseñas con estrellas tras completar transacción
- [ ] Sistema de denuncias y panel de administración
- [ ] Notificaciones por email (verificación + ofertas)
- [ ] Recuperación de contraseña
- [ ] Integración real de pasarela de pagos
- [ ] App móvil (puede ser PWA reutilizando estos endpoints)

---

## 📝 Licencia

Proyecto educativo. ¡Úsalo, modifícalo, vuélvelo tuyo!

**Hecho con ❤️ desde México 🇲🇽**
