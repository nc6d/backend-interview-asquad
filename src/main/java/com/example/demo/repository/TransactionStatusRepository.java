package com.example.demo.repository;

import com.example.demo.repository.entity.TransactionStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionStatusRepository extends JpaRepository<TransactionStatusEntity, Long> {
}
