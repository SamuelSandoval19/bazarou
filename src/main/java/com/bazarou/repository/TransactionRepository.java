package com.bazarou.repository;

import com.bazarou.model.Transaction;
import com.bazarou.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByCodigoSeguimiento(String codigo);
    List<Transaction> findByCompradorOrderByFechaCompraDesc(User comprador);
    List<Transaction> findByVendedorOrderByFechaCompraDesc(User vendedor);
}
