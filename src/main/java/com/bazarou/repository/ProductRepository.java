package com.bazarou.repository;

import com.bazarou.model.Category;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN FETCH p.vendedor LEFT JOIN FETCH p.categoria WHERE p.id = :id")
    Optional<Product> findByIdConRelaciones(@Param("id") Long id);

    Page<Product> findByDisponibilidad(Product.Disponibilidad d, Pageable pageable);

    Page<Product> findByCategoriaAndDisponibilidad(Category c, Product.Disponibilidad d, Pageable pageable);

    List<Product> findByVendedorOrderByFechaPublicacionDesc(User vendedor);

    @Query("SELECT p FROM Product p WHERE p.disponibilidad = 'DISPONIBLE' " +
           "AND (LOWER(p.titulo) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "  OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Product> buscar(@Param("q") String q, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.disponibilidad = 'DISPONIBLE' ORDER BY p.fechaPublicacion DESC")
    List<Product> findRecientes(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.disponibilidad = 'DISPONIBLE' ORDER BY p.favoritos DESC, p.vistas DESC")
    List<Product> findPopulares(Pageable pageable);
}
