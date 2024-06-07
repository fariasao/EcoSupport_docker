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

import com.TPC.ocean.model.Contrato;
import com.TPC.ocean.repository.ContratoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/contratos")
@CacheConfig(cacheNames = "contratos")
@Tag(name = "Contratos", description = "Gerenciamento de contratos")
public class ContratoController {
    @Autowired
    ContratoRepository repository;

    @Autowired
    PagedResourcesAssembler<Contrato> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Contratos",
        description = "Retorna uma lista paginada de contratos"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Contratos listados"),
        @ApiResponse(responseCode = "404", description = "Contratos não encontrados")
    })
    public PagedModel<EntityModel<Contrato>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Contrato> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Contrato por ID",
        description = "Retorna um contrato específico"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Contrato listado"),
        @ApiResponse(responseCode = "404", description = "Contrato não encontrado")
    })
    public EntityModel<Contrato> show(@PathVariable Long id) {
        Contrato contrato = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado")
        );
        return contrato.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Contrato",
        description = "Cadastra um novo contrato"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contrato criado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Contrato> create(@RequestBody @Valid Contrato contrato) {
        repository.save(contrato);
        return ResponseEntity
                .created(contrato.toEntityModel().getRequiredLink("self").toUri())
                .body(contrato);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Contrato",
        description = "Deleta um contrato específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Contrato deletado"),
        @ApiResponse(responseCode = "404", description = "Contrato não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Contrato",
        description = "Atualiza um contrato específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contrato atualizado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Contrato não encontrado")
    })
    public ResponseEntity<Contrato> update(@PathVariable Long id, @RequestBody @Valid Contrato contratoAtualizado) {
        Contrato contrato = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado")
        );
        
        contrato.setEmpresa(contratoAtualizado.getEmpresa());
        contrato.setTipoContrato(contratoAtualizado.getTipoContrato());
        contrato.setDataInicio(contratoAtualizado.getDataInicio());
        contrato.setDataFim(contratoAtualizado.getDataFim());
        contrato.setValor(contratoAtualizado.getValor());
        contrato.setStatus(contratoAtualizado.getStatus());
        contrato.setAssinaturaPendente(contratoAtualizado.getAssinaturaPendente());
        
        repository.save(contrato);
        
        return ResponseEntity.ok(contrato);
    }
}
