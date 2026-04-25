package com.bazarou.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "producto_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @CreationTimestamp
    @Column(name = "fecha_agregado", updatable = false)
    private LocalDateTime fechaAgregado;
}
