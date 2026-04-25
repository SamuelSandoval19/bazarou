package com.bazarou.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Producto en venta dentro de Bazarou.
 */
@Entity
@Table(name = "productos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String descripcion;

    @NotNull @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Estado estado = Estado.USADO_BUENO;

    @Column(length = 50)
    private String marca;

    @Column(length = 30)
    private String talla; // para ropa/calzado

    @Column(length = 30)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Category categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", nullable = false)
    private User vendedor;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_imagenes", joinColumns = @JoinColumn(name = "producto_id"))
    @Column(name = "url_imagen", length = 500)
    @OrderColumn(name = "orden")
    @Builder.Default
    private List<String> imagenes = new ArrayList<>();

    @Column(name = "acepta_ofertas", nullable = false)
    @Builder.Default
    private Boolean aceptaOfertas = true;

    @Column(name = "compra_protegida", nullable = false)
    @Builder.Default
    private Boolean compraProtegida = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Disponibilidad disponibilidad = Disponibilidad.DISPONIBLE;

    @Column(name = "ciudad_envio", length = 100)
    private String ciudadEnvio;

    @Column(name = "vistas")
    @Builder.Default
    private Integer vistas = 0;

    @Column(name = "favoritos")
    @Builder.Default
    private Integer favoritos = 0;

    @CreationTimestamp
    @Column(name = "fecha_publicacion", updatable = false)
    private LocalDateTime fechaPublicacion;

    public enum Estado {
        NUEVO_CON_ETIQUETA("Nuevo con etiqueta"),
        NUEVO_SIN_ETIQUETA("Nuevo sin etiqueta"),
        USADO_BUENO("Usado en buen estado"),
        USADO_CON_DETALLES("Usado con detalles");

        private final String label;
        Estado(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    public enum Disponibilidad {
        DISPONIBLE, RESERVADO, VENDIDO
    }
}
