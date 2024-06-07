package com.TPC.ocean.repository;

import com.TPC.ocean.model.Empresa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Page<Empresa> findById(String id, Pageable pageable);
}
