package com.bazarou.service;

import com.bazarou.model.Offer;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import com.bazarou.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;

    @Transactional
    public Offer hacerOferta(Product producto, User comprador, BigDecimal monto, String mensaje) {
        if (!producto.getAceptaOfertas()) {
            throw new IllegalStateException("Este vendedor no acepta regateos en este producto");
        }
        if (producto.getVendedor().getId().equals(comprador.getId())) {
            throw new IllegalStateException("No puedes ofertar en tu propio producto");
        }
        if (producto.getDisponibilidad() != Product.Disponibilidad.DISPONIBLE) {
            throw new IllegalStateException("Este producto ya no está disponible");
        }
        // Mínimo 50% del precio publicado para evitar abuso
        BigDecimal minimo = producto.getPrecio().multiply(new BigDecimal("0.50"));
        if (monto.compareTo(minimo) < 0) {
            throw new IllegalArgumentException("La oferta es demasiado baja, prueba con algo más razonable 😅");
        }

        Offer o = Offer.builder()
                .producto(producto)
                .comprador(comprador)
                .montoOfrecido(monto)
                .mensaje(mensaje)
                .build();
        return offerRepository.save(o);
    }

    @Transactional
    public Offer aceptar(Long ofertaId, User vendedor) {
        Offer o = obtener(ofertaId, vendedor);
        o.setEstado(Offer.EstadoOferta.ACEPTADA);
        o.setFechaRespuesta(LocalDateTime.now());
        // Reservar el producto
        o.getProducto().setDisponibilidad(Product.Disponibilidad.RESERVADO);
        return offerRepository.save(o);
    }

    @Transactional
    public Offer rechazar(Long ofertaId, User vendedor, String mensaje) {
        Offer o = obtener(ofertaId, vendedor);
        o.setEstado(Offer.EstadoOferta.RECHAZADA);
        o.setRespuestaVendedor(mensaje);
        o.setFechaRespuesta(LocalDateTime.now());
        return offerRepository.save(o);
    }

    @Transactional
    public Offer contraofertar(Long ofertaId, User vendedor, BigDecimal monto, String mensaje) {
        Offer o = obtener(ofertaId, vendedor);
        o.setEstado(Offer.EstadoOferta.CONTRAOFERTA);
        o.setContraOferta(monto);
        o.setRespuestaVendedor(mensaje);
        o.setFechaRespuesta(LocalDateTime.now());
        return offerRepository.save(o);
    }

    private Offer obtener(Long ofertaId, User vendedor) {
        Offer o = offerRepository.findById(ofertaId)
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));
        if (!o.getProducto().getVendedor().getId().equals(vendedor.getId())) {
            throw new SecurityException("No tienes permiso para responder esta oferta");
        }
        return o;
    }

    public List<Offer> ofertasRecibidas(User vendedor) {
        return offerRepository.findByProducto_VendedorOrderByFechaOfertaDesc(vendedor);
    }

    public List<Offer> ofertasEnviadas(User comprador) {
        return offerRepository.findByCompradorOrderByFechaOfertaDesc(comprador);
    }

    public List<Offer> ofertasDeProducto(Product p) {
        return offerRepository.findByProductoOrderByFechaOfertaDesc(p);
    }
}
