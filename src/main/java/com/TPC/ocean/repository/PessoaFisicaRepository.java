package com.TPC.ocean.repository;

import com.TPC.ocean.model.PessoaFisica;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, Long> {
    Page<PessoaFisica> findById(String id, Pageable pageable);
}
