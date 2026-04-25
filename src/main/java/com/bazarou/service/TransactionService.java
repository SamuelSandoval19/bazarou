package com.bazarou.service;

import com.bazarou.model.Product;
import com.bazarou.model.Transaction;
import com.bazarou.model.User;
import com.bazarou.repository.ProductRepository;
import com.bazarou.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    private static final BigDecimal COMISION_PORCENTAJE = new BigDecimal("0.05"); // 5%

    @Transactional
    public Transaction comprar(Product producto, User comprador, BigDecimal montoFinal,
                                String direccion, BigDecimal costoEnvio) {
        if (producto.getVendedor().getId().equals(comprador.getId())) {
            throw new IllegalStateException("No puedes comprar tu propio producto");
        }
        if (producto.getDisponibilidad() == Product.Disponibilidad.VENDIDO) {
            throw new IllegalStateException("Este producto ya fue vendido");
        }

        BigDecimal comision = montoFinal.multiply(COMISION_PORCENTAJE).setScale(2, RoundingMode.HALF_UP);

        Transaction t = Transaction.builder()
                .codigoSeguimiento(generarCodigo())
                .producto(producto)
                .comprador(comprador)
                .vendedor(producto.getVendedor())
                .monto(montoFinal)
                .comisionBazarou(comision)
                .costoEnvio(costoEnvio == null ? BigDecimal.ZERO : costoEnvio)
                .direccionEnvio(direccion)
                .build();

        // Marcar producto como vendido
        producto.setDisponibilidad(Product.Disponibilidad.VENDIDO);
        productRepository.save(producto);

        return transactionRepository.save(t);
    }

    @Transactional
    public Transaction marcarEnviado(Long id, User vendedor, String paqueteria, String guia) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        if (!t.getVendedor().getId().equals(vendedor.getId())) {
            throw new SecurityException("No autorizado");
        }
        t.setEstado(Transaction.Estado.ENVIADO);
        t.setPaqueteria(paqueteria);
        t.setGuiaEnvio(guia);
        t.setFechaEnvio(LocalDateTime.now());
        return transactionRepository.save(t);
    }

    @Transactional
    public Transaction confirmarRecepcion(Long id, User comprador) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        if (!t.getComprador().getId().equals(comprador.getId())) {
            throw new SecurityException("No autorizado");
        }
        t.setEstado(Transaction.Estado.COMPLETADO);
        t.setFechaEntrega(LocalDateTime.now());
        t.setFechaLiberacionPago(LocalDateTime.now());

        // Sumar al contador de ventas del vendedor
        User vendedor = t.getVendedor();
        vendedor.setTotalVentas(vendedor.getTotalVentas() + 1);

        return transactionRepository.save(t);
    }

    @Transactional
    public Transaction reportarProblema(Long id, User comprador) {
        Transaction t = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
        if (!t.getComprador().getId().equals(comprador.getId())) {
            throw new SecurityException("No autorizado");
        }
        t.setEstado(Transaction.Estado.EN_DISPUTA);
        return transactionRepository.save(t);
    }

    public List<Transaction> compras(User u) {
        return transactionRepository.findByCompradorOrderByFechaCompraDesc(u);
    }

    public List<Transaction> ventas(User u) {
        return transactionRepository.findByVendedorOrderByFechaCompraDesc(u);
    }

    private String generarCodigo() {
        return "BZR-" + Year.now().getValue() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
