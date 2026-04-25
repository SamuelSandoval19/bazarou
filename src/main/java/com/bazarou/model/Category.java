package com.bazarou.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categorias")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 60)
    private String nombre;

    @Column(unique = true, nullable = false, length = 60)
    private String slug;

    @Column(length = 50)
    private String emoji; // emoji o ícono representativo

    @Column(length = 200)
    private String descripcion;
}
