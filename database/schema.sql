-- ============================================
-- BAZAROU - Schema completo de base de datos
-- ============================================
-- Hibernate crea las tablas automáticamente con `ddl-auto=update`,
-- pero si prefieres crear todo a mano desde phpMyAdmin, ejecuta este script.
--
-- IMPORTANTE: La aplicación crea la BD automáticamente con
-- `createDatabaseIfNotExist=true`, así que normalmente no necesitas correr esto.

CREATE DATABASE IF NOT EXISTS bazarou
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE bazarou;

-- ----- USUARIOS -----
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    bio VARCHAR(500),
    foto_perfil VARCHAR(255),
    ciudad VARCHAR(100),
    estado VARCHAR(50),
    telefono VARCHAR(15),
    rol VARCHAR(20) NOT NULL DEFAULT 'USUARIO',
    calificacion_promedio DOUBLE DEFAULT 0.0,
    total_ventas INT DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----- CATEGORÍAS -----
CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE,
    slug VARCHAR(60) NOT NULL UNIQUE,
    emoji VARCHAR(50),
    descripcion VARCHAR(200)
) ENGINE=InnoDB;

-- ----- PRODUCTOS -----
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    marca VARCHAR(50),
    talla VARCHAR(30),
    color VARCHAR(30),
    categoria_id BIGINT,
    vendedor_id BIGINT NOT NULL,
    acepta_ofertas BOOLEAN NOT NULL DEFAULT TRUE,
    compra_protegida BOOLEAN NOT NULL DEFAULT TRUE,
    disponibilidad VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    ciudad_envio VARCHAR(100),
    vistas INT DEFAULT 0,
    favoritos INT DEFAULT 0,
    fecha_publicacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS producto_imagenes (
    producto_id BIGINT NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    orden INT NOT NULL,
    PRIMARY KEY (producto_id, orden),
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----- OFERTAS -----
CREATE TABLE IF NOT EXISTS ofertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    monto_ofrecido DECIMAL(10,2) NOT NULL,
    mensaje VARCHAR(300),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    contra_oferta DECIMAL(10,2),
    respuesta_vendedor VARCHAR(300),
    fecha_oferta DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (comprador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- ----- TRANSACCIONES -----
CREATE TABLE IF NOT EXISTS transacciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_seguimiento VARCHAR(30) NOT NULL UNIQUE,
    producto_id BIGINT NOT NULL,
    comprador_id BIGINT NOT NULL,
    vendedor_id BIGINT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    comision_bazarou DECIMAL(10,2),
    costo_envio DECIMAL(10,2),
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGO_RETENIDO',
    direccion_envio VARCHAR(500),
    guia_envio VARCHAR(100),
    paqueteria VARCHAR(50),
    fecha_compra DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_envio DATETIME,
    fecha_entrega DATETIME,
    fecha_liberacion_pago DATETIME,
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (comprador_id) REFERENCES usuarios(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- ----- FAVORITOS -----
CREATE TABLE IF NOT EXISTS favoritos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    fecha_agregado DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unq_user_prod (usuario_id, producto_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----- RESEÑAS -----
CREATE TABLE IF NOT EXISTS resenas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaccion_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    evaluado_id BIGINT NOT NULL,
    calificacion INT NOT NULL,
    comentario VARCHAR(500),
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transaccion_id) REFERENCES transacciones(id),
    FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    FOREIGN KEY (evaluado_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- ----- CATEGORÍAS INICIALES -----
INSERT IGNORE INTO categorias (nombre, slug, emoji, descripcion) VALUES
('Ropa y accesorios', 'ropa', '👕', 'Camisas, pantalones, zapatos, bolsas y más'),
('Electrónicos',      'electronicos', '📱', 'Celulares, laptops, audífonos, gadgets'),
('Hogar y jardín',    'hogar', '🪴', 'Muebles, decoración, plantas, cocina'),
('Libros y revistas', 'libros', '📚', 'Libros nuevos y usados, revistas, comics'),
('Belleza y cuidado', 'belleza', '💄', 'Maquillaje, perfumes, productos de cuidado'),
('Niños y bebés',     'ninos', '🧸', 'Ropa, juguetes y artículos para los chamacos'),
('Deportes',          'deportes', '⚽', 'Ropa deportiva, equipo, bicicletas'),
('Coleccionismo',     'coleccionismo', '🎴', 'Cartas, figuras, vinilos, antigüedades'),
('Arte y artesanías', 'arte', '🎨', 'Cuadros, alebrijes, talavera, hechos a mano'),
('Música e instrumentos', 'musica', '🎸', 'Instrumentos, equipo de audio, discos'),
('Mascotas',          'mascotas', '🐾', 'Accesorios para perros, gatos y demás'),
('Antojitos y otros', 'antojitos', '🌶️', 'Lo que no entra en otra categoría');
