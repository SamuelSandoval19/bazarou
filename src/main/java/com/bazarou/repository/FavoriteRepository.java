package com.bazarou.repository;

import com.bazarou.model.Favorite;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUsuarioAndProducto(User usuario, Product producto);
    List<Favorite> findByUsuarioOrderByFechaAgregadoDesc(User usuario);
    boolean existsByUsuarioAndProducto(User usuario, Product producto);
    void deleteByUsuarioAndProducto(User usuario, Product producto);
}
