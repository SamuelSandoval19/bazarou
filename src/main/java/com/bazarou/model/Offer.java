package com.bazarou.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Oferta (regateo) de un comprador hacia el vendedor por un producto.
 * El comprador propone un precio, el vendedor acepta, rechaza o contraoferta.
 */
@Entity
@Table(name = "ofertas")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprador_id", nullable = false)
    private User comprador;

    @NotNull @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montoOfrecido;

    @Column(length = 300)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoOferta estado = EstadoOferta.PENDIENTE;

    /** Si el vendedor contraoferta, este es su monto */
    @Column(name = "contra_oferta", precision = 10, scale = 2)
    private BigDecimal contraOferta;

    @Column(name = "respuesta_vendedor", length = 300)
    private String respuestaVendedor;

    @CreationTimestamp
    @Column(name = "fecha_oferta", updatable = false)
    private LocalDateTime fechaOferta;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    public enum EstadoOferta {
        PENDIENTE, ACEPTADA, RECHAZADA, CONTRAOFERTA, EXPIRADA
    }
}
