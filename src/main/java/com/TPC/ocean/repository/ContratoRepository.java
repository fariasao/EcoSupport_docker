package com.TPC.ocean.repository;

import com.TPC.ocean.model.Contrato;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    Page<Contrato> findById(String id, Pageable pageable);
}
