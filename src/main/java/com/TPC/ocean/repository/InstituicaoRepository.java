package com.TPC.ocean.repository;

import com.TPC.ocean.model.Instituicao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    Page<Instituicao> findById(String id, Pageable pageable);
}
