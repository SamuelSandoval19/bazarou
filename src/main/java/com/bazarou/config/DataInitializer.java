package com.bazarou.config;

import com.bazarou.model.Category;
import com.bazarou.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Categorias ya existen, no se cargan datos iniciales.");
            return;
        }

        log.info("Cargando categorias iniciales...");

        List<Category> categorias = List.of(
                cat("Ropa y accesorios", "ropa", "👕", "Camisas, pantalones, zapatos, bolsas y mas"),
                cat("Electronicos", "electronicos", "📱", "Celulares, laptops, audifonos, gadgets"),
                cat("Hogar y jardin", "hogar", "🪴", "Muebles, decoracion, plantas, cocina"),
                cat("Libros y revistas", "libros", "📚", "Libros nuevos y usados, revistas, comics"),
                cat("Belleza y cuidado", "belleza", "💄", "Maquillaje, perfumes, productos de cuidado"),
                cat("Niños y bebes", "ninos", "🧸", "Ropa, juguetes y articulos para los chamacos"),
                cat("Deportes", "deportes", "⚽", "Ropa deportiva, equipo, bicicletas"),
                cat("Coleccionismo", "coleccionismo", "🎴", "Cartas, figuras, vinilos, antiguedades"),
                cat("Arte y artesanias", "arte", "🎨", "Cuadros, alebrijes, talavera, hechos a mano"),
                cat("Musica e instrumentos", "musica", "🎸", "Instrumentos, equipo de audio, discos"),
                cat("Mascotas", "mascotas", "🐾", "Accesorios para perros, gatos y demas"),
                cat("Antojitos y otros", "antojitos", "🌶️", "Lo que no entra en otra categoria")
        );

        categoryRepository.saveAll(categorias);
        log.info("Se cargaron {} categorias.", categorias.size());
    }

    private Category cat(String nombre, String slug, String emoji, String descripcion) {
        return Category.builder()
                .nombre(nombre)
                .slug(slug)
                .emoji(emoji)
                .descripcion(descripcion)
                .build();
    }
}
