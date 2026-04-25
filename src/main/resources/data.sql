-- ============================================
-- BAZAROU - Categorías iniciales
-- Se cargan automáticamente al arrancar la app
-- ============================================

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
