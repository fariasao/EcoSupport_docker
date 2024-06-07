package com.TPC.ocean.repository;

import com.TPC.ocean.model.Transacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    Page<Transacao> findById(String id, Pageable pageable);
}
