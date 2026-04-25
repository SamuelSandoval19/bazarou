package com.bazarou.service;

import com.bazarou.dto.ProductDTO;
import com.bazarou.model.Category;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import com.bazarou.repository.CategoryRepository;
import com.bazarou.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${bazarou.uploads.dir}")
    private String uploadsDir;

    @Transactional
    public Product crear(ProductDTO dto, List<MultipartFile> imagenes, User vendedor) throws IOException {
        Category cat = categoryRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no válida"));

        Product p = Product.builder()
                .titulo(dto.getTitulo().trim())
                .descripcion(dto.getDescripcion().trim())
                .precio(dto.getPrecio())
                .estado(dto.getEstado())
                .marca(dto.getMarca())
                .talla(dto.getTalla())
                .color(dto.getColor())
                .ciudadEnvio(dto.getCiudadEnvio())
                .aceptaOfertas(Boolean.TRUE.equals(dto.getAceptaOfertas()))
                .compraProtegida(Boolean.TRUE.equals(dto.getCompraProtegida()))
                .categoria(cat)
                .vendedor(vendedor)
                .imagenes(new ArrayList<>())
                .build();

        // Guardar imágenes
        if (imagenes != null) {
            for (MultipartFile file : imagenes) {
                if (file != null && !file.isEmpty()) {
                    String ruta = guardarImagen(file);
                    p.getImagenes().add(ruta);
                }
            }
        }

        return productRepository.save(p);
    }

    private String guardarImagen(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".")
                ? original.substring(original.lastIndexOf('.'))
                : ".jpg";
        String filename = UUID.randomUUID() + ext;
        Path destino = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), destino);
        return "/uploads/" + filename;
    }

    public Product porId(Long id) {
        return productRepository.findByIdConRelaciones(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    @Transactional
    public void incrementarVistas(Product p) {
        p.setVistas(p.getVistas() + 1);
        productRepository.save(p);
    }

    public Page<Product> listarDisponibles(int page, int size, String sort) {
        Sort sortObj = switch (sort) {
            case "precio_asc" -> Sort.by("precio").ascending();
            case "precio_desc" -> Sort.by("precio").descending();
            case "populares" -> Sort.by("favoritos").descending().and(Sort.by("vistas").descending());
            default -> Sort.by("fechaPublicacion").descending();
        };
        Pageable pg = PageRequest.of(page, size, sortObj);
        return productRepository.findByDisponibilidad(Product.Disponibilidad.DISPONIBLE, pg);
    }

    public Page<Product> porCategoria(Category c, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("fechaPublicacion").descending());
        return productRepository.findByCategoriaAndDisponibilidad(c, Product.Disponibilidad.DISPONIBLE, pg);
    }

    public Page<Product> buscar(String q, int page, int size) {
        Pageable pg = PageRequest.of(page, size, Sort.by("fechaPublicacion").descending());
        return productRepository.buscar(q, pg);
    }

    public List<Product> recientes(int n) {
        return productRepository.findRecientes(PageRequest.of(0, n));
    }

    public List<Product> populares(int n) {
        return productRepository.findPopulares(PageRequest.of(0, n));
    }

    public List<Product> delVendedor(User u) {
        return productRepository.findByVendedorOrderByFechaPublicacionDesc(u);
    }
}
