package com.circularmarket.demo.repository;

import com.circularmarket.demo.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    boolean existsByTransaccionId(String transaccionId);
}