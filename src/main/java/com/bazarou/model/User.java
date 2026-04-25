package com.bazarou.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Usuario de Bazarou: puede comprar, vender, hacer ofertas y recibir reseñas.
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 30)
    @Column(unique = true, nullable = false, length = 30)
    private String username;

    @NotBlank @Email(message = "Correo no válido")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(name = "nombre_completo", length = 100)
    private String nombreCompleto;

    @Column(length = 500)
    private String bio;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 50)
    private String estado;

    @Column(length = 15)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role rol = Role.USUARIO;

    @Column(name = "calificacion_promedio")
    @Builder.Default
    private Double calificacionPromedio = 0.0;

    @Column(name = "total_ventas")
    @Builder.Default
    private Integer totalVentas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Product> productos = new HashSet<>();

    public enum Role {
        USUARIO, ADMIN
    }
}
