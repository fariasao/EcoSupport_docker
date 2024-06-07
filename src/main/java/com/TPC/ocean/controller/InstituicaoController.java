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

import com.TPC.ocean.model.Instituicao;
import com.TPC.ocean.repository.InstituicaoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/instituicoes")
@CacheConfig(cacheNames = "instituicoes")
@Tag(name = "Instituições", description = "Gerenciamento de instituições")
public class InstituicaoController {
    @Autowired
    InstituicaoRepository repository;

    @Autowired
    PagedResourcesAssembler<Instituicao> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Instituições",
        description = "Retorna uma lista paginada de instituições"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Instituições listadas"),
        @ApiResponse(responseCode = "404", description = "Instituições não encontradas")
    })
    public PagedModel<EntityModel<Instituicao>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Instituicao> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Instituição por ID",
        description = "Retorna uma instituição específica"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Instituição listada"),
        @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public EntityModel<Instituicao> show(@PathVariable Long id) {
        Instituicao instituicao = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituição não encontrada")
        );
        return instituicao.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Instituição",
        description = "Cria uma nova instituição"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Instituição criada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Instituicao> create(@RequestBody @Valid Instituicao instituicao) {
        repository.save(instituicao);
        return ResponseEntity
                .created(instituicao.toEntityModel().getRequiredLink("self").toUri())
                .body(instituicao);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Instituição",
        description = "Deleta uma instituição específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Instituição deletada"),
        @ApiResponse(responseCode = "404", description = "Instituição não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituição não encontrada")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Instituição",
        description = "Atualiza uma instituição específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Instituição atualizada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Instituição não encontrada")
    })
    public ResponseEntity<Instituicao> update(@PathVariable Long id, @RequestBody @Valid Instituicao instituicaoAtualizada) {
        Instituicao instituicao = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituição não encontrada")
        );
        
        instituicao.setNome(instituicaoAtualizada.getNome());
        instituicao.setCnpj(instituicaoAtualizada.getCnpj());
        instituicao.setEmail(instituicaoAtualizada.getEmail());
        instituicao.setTelefone(instituicaoAtualizada.getTelefone());
        instituicao.setEndereco(instituicaoAtualizada.getEndereco());
        
        repository.save(instituicao);
        
        return ResponseEntity.ok(instituicao);
    }
}
