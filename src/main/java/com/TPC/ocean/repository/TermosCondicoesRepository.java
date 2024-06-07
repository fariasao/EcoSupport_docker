package com.TPC.ocean.repository;

import com.TPC.ocean.model.TermosCondicoes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermosCondicoesRepository extends JpaRepository<TermosCondicoes, Long> {
    Page<TermosCondicoes> findById(String id, Pageable pageable);
}
