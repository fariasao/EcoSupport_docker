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

import com.TPC.ocean.model.Empresa;
import com.TPC.ocean.repository.EmpresaRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/empresas")
@CacheConfig(cacheNames = "empresas")
@Tag(name = "Empresas", description = "Gerenciamento de empresas")
public class EmpresaController {
    @Autowired
    EmpresaRepository repository;

    @Autowired
    PagedResourcesAssembler<Empresa> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Empresas",
        description = "Retorna uma lista paginada de empresas"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Empresas listadas"),
        @ApiResponse(responseCode = "404", description = "Empresas não encontradas")
    })
    public PagedModel<EntityModel<Empresa>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Empresa> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Empresa por ID",
        description = "Retorna uma empresa específica"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Empresa listada"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public EntityModel<Empresa> show(@PathVariable Long id) {
        Empresa empresa = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada")
        );
        return empresa.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Empresa",
        description = "Cadastra uma nova empresa"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Empresa criada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Empresa> create(@RequestBody @Valid Empresa empresa) {
        repository.save(empresa);
        return ResponseEntity
                .created(empresa.toEntityModel().getRequiredLink("self").toUri())
                .body(empresa);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Empresa",
        description = "Deleta uma empresa específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Empresa deletada"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Empresa",
        description = "Atualiza uma empresa específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empresa atualizada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Empresa não encontrada")
    })
    public ResponseEntity<Empresa> update(@PathVariable Long id, @RequestBody @Valid Empresa empresaAtualizada) {
        Empresa empresa = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada")
        );
        
        empresa.setNome(empresaAtualizada.getNome());
        empresa.setCnpj(empresaAtualizada.getCnpj());
        empresa.setEmail(empresaAtualizada.getEmail());
        empresa.setTelefone(empresaAtualizada.getTelefone());
        empresa.setEndereco(empresaAtualizada.getEndereco());
        
        repository.save(empresa);
        
        return ResponseEntity.ok(empresa);
    }
}
