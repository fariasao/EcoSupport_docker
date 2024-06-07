package com.TPC.ocean.repository;

import com.TPC.ocean.model.Exibicao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExibicaoRepository extends JpaRepository<Exibicao, Long> {
    Page<Exibicao> findById(String id, Pageable pageable);
}
