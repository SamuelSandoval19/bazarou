package com.bazarou.repository;

import com.bazarou.model.Offer;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByProductoOrderByFechaOfertaDesc(Product producto);
    List<Offer> findByCompradorOrderByFechaOfertaDesc(User comprador);
    List<Offer> findByProducto_VendedorOrderByFechaOfertaDesc(User vendedor);
    long countByProductoAndEstado(Product producto, Offer.EstadoOferta estado);
}
