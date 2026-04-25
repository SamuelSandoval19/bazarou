package com.bazarou.repository;

import com.bazarou.model.Review;
import com.bazarou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEvaluadoOrderByFechaDesc(User evaluado);

    @org.springframework.data.jpa.repository.Query(
            "SELECT AVG(r.calificacion) FROM Review r WHERE r.evaluado = :u")
    Double promedioPorUsuario(@org.springframework.data.repository.query.Param("u") User u);
}
