package com.bazarou.dto;

import com.bazarou.model.Product;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    @NotBlank(message = "Ponle un título a tu producto")
    @Size(max = 120)
    private String titulo;

    @NotBlank(message = "Cuéntales a los compradores cómo está tu producto")
    @Size(max = 2000)
    private String descripcion;

    @NotNull @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull
    private Long categoriaId;

    @NotNull
    private Product.Estado estado;

    private String marca;
    private String talla;
    private String color;
    private String ciudadEnvio;

    private Boolean aceptaOfertas = true;
    private Boolean compraProtegida = true;
}
