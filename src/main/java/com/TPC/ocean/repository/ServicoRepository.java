package com.TPC.ocean.repository;

import com.TPC.ocean.model.Servico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    Page<Servico> findById(String id, Pageable pageable);
}
