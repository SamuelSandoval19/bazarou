package com.bazarou.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transacción con Compra Protegida: Bazarou retiene el dinero
 * hasta que el comprador confirma la recepción del producto.
 */
@Entity
@Table(name = "transacciones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String codigoSeguimiento; // ej. BZR-2025-000123

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprador_id", nullable = false)
    private User comprador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "comision_bazarou", precision = 10, scale = 2)
    private BigDecimal comisionBazarou; // 5% por compra protegida

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Estado estado = Estado.PAGO_RETENIDO;

    @Column(name = "direccion_envio", length = 500)
    private String direccionEnvio;

    @Column(name = "guia_envio", length = 100)
    private String guiaEnvio;

    @Column(name = "paqueteria", length = 50)
    private String paqueteria;

    @CreationTimestamp
    @Column(name = "fecha_compra", updatable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "fecha_liberacion_pago")
    private LocalDateTime fechaLiberacionPago;

    public enum Estado {
        PAGO_RETENIDO,    // El dinero está en custodia de Bazarou
        ENVIADO,          // El vendedor ya envió
        ENTREGADO,        // El comprador confirmó recepción
        COMPLETADO,       // Pago liberado al vendedor
        EN_DISPUTA,       // El comprador reportó un problema
        REEMBOLSADO,      // Se devolvió el dinero al comprador
        CANCELADO
    }
}
