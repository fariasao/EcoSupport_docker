package com.TPC.ocean.controller;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.TPC.ocean.model.TermosCondicoes;
import com.TPC.ocean.repository.TermosCondicoesRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/termos-condicoes")
@CacheConfig(cacheNames = "termos-condicoes")
@Tag(name = "Termos e Condições", description = "Gerenciamento de termos e condições")
public class TermosCondicoesController {
    @Autowired
    TermosCondicoesRepository repository;

    @Autowired
    PagedResourcesAssembler<TermosCondicoes> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Termos e Condições",
        description = "Retorna uma lista paginada de Termos e Condições"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Termos e Condições listados"),
        @ApiResponse(responseCode = "404", description = "Termos e Condições não encontrados")
    })
    public PagedModel<EntityModel<TermosCondicoes>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<TermosCondicoes> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Termos e Condições por ID",
        description = "Retorna uma informação de termos e confições específica"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Termos e Condições listados"),
        @ApiResponse(responseCode = "404", description = "Termos e Condições não encontrados")
    })
    public EntityModel<TermosCondicoes> show(@PathVariable Long id) {
        TermosCondicoes termosCondicoes = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Informações de Termos e Condições não encontradas")
        );
        return termosCondicoes.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Termos e Condições",
        description = "Cadastra uma nova informação de termos e condições"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Termos e Condições criado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<TermosCondicoes> create(@RequestBody @Valid TermosCondicoes termosCondicoes) {
        repository.save(termosCondicoes);
        return ResponseEntity
                .created(termosCondicoes.toEntityModel().getRequiredLink("self").toUri())
                .body(termosCondicoes);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Termos e Condições",
        description = "Deleta uma informação de Termos e Condições específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Termos e Condições deletados"),
        @ApiResponse(responseCode = "404", description = "Termos e Condições não encontrados"),
        @ApiResponse(responseCode = "401", description = "Sem autorização")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Termos e Condições não encontrados")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Termos e Condições",
        description = "Atualiza as informações de Termos e Condições específicas"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Termos e Condições atualizado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Sem autorização"),
        @ApiResponse(responseCode = "404", description = "Termos e Condições não encontrados")
    })
    public ResponseEntity<TermosCondicoes> update(@PathVariable Long id, @RequestBody @Valid TermosCondicoes termosCondicoesAtualizado) {
        TermosCondicoes termosCondicoes = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Termos e Condições não encontrados")
        );
        
        termosCondicoes.setUsuario(termosCondicoesAtualizado.getUsuario());
        termosCondicoes.setAceitou(termosCondicoesAtualizado.getAceitou());
        termosCondicoes.setDataAceite(termosCondicoesAtualizado.getDataAceite());
        
        repository.save(termosCondicoes);
        
        return ResponseEntity.ok(termosCondicoes);
    }
}
