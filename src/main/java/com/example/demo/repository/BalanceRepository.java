package com.example.demo.repository;


import com.example.demo.repository.entity.BalanceEntity;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, Long> {

    Optional<BalanceEntity> findByCurrency(String currency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT b FROM BalanceEntity b WHERE b.currency = :currency")
    Optional<BalanceEntity> findByCurrencyForUpdate(@Param("currency") String currency);

    @Query(value =
            "INSERT INTO balance (currency, amount, created_at) VALUES (:currency, 0.00, CURRENT_TIMESTAMP) ON CONFLICT (currency) DO NOTHING RETURNING id",
            nativeQuery = true)
    Long insertIfNotExists(@Param("currency") String currency);
}
