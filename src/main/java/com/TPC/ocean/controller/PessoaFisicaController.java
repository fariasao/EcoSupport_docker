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

import com.TPC.ocean.model.PessoaFisica;
import com.TPC.ocean.repository.PessoaFisicaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pessoas-fisicas")
@CacheConfig(cacheNames = "pessoas-fisicas")
@Tag(name = "Pessoas Físicas", description = "Gerenciamento de pessoas físicas")
public class PessoaFisicaController {
    @Autowired
    PessoaFisicaRepository repository;

    @Autowired
    PagedResourcesAssembler<PessoaFisica> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Pessoas Físicas",
        description = "Retorna uma lista paginada de pessoas físicas"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Pessoas Físicas listadas"),
        @ApiResponse(responseCode = "404", description = "Pessoas Físicas não encontradas")
    })
    public PagedModel<EntityModel<PessoaFisica>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<PessoaFisica> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Pessoa Física por ID",
        description = "Retorna uma pessoa física específica"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Pessoa Fisica listada"),
        @ApiResponse(responseCode = "404", description = "Pessoa Fisica não encontrada")
    })
    public EntityModel<PessoaFisica> show(@PathVariable Long id) {
        PessoaFisica pessoaFisica = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa Fisica não encontrada")
        );
        return pessoaFisica.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Pessoa Física",
        description = "Cadastra uma nova pessoa física"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pessoa Física criada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<PessoaFisica> create(@RequestBody @Valid PessoaFisica pessoaFisica) {
        repository.save(pessoaFisica);
        return ResponseEntity
                .created(pessoaFisica.toEntityModel().getRequiredLink("self").toUri())
                .body(pessoaFisica);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Pessoa Física",
        description = "Deleta uma pessoa física específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pessoa Física deletada"),
        @ApiResponse(responseCode = "404", description = "Pessoa Física não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa Física não encontrada")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Pessoa Física",
        description = "Atualiza uma pessoa física específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pessoa Física atualizada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Pessoa Física não encontrada")
    })
    public ResponseEntity<PessoaFisica> update(@PathVariable Long id, @RequestBody @Valid PessoaFisica pessoaFisicaAtualizado) {
        PessoaFisica pessoaFisica = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa Física não encontrada")
        );
        
        pessoaFisica.setNome(pessoaFisicaAtualizado.getNome());
        pessoaFisica.setCpf(pessoaFisicaAtualizado.getCpf());
        pessoaFisica.setEmail(pessoaFisicaAtualizado.getEmail());
        pessoaFisica.setSenha(pessoaFisicaAtualizado.getSenha());
        
        repository.save(pessoaFisica);
        
        return ResponseEntity.ok(pessoaFisica);
    }
}
