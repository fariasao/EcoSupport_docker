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

import com.TPC.ocean.model.Servico;
import com.TPC.ocean.repository.ServicoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
@CacheConfig(cacheNames = "servicos")
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
public class ServicoController {
    @Autowired
    ServicoRepository repository;

    @Autowired
    PagedResourcesAssembler<Servico> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Serviços",
        description = "Retorna uma lista paginada de serviços"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Serviços listados"),
        @ApiResponse(responseCode = "404", description = "Serviços não encontrados")
    })
    public PagedModel<EntityModel<Servico>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Servico> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Serviço por ID",
        description = "Retorna um serviço específico"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Serviço listado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public EntityModel<Servico> show(@PathVariable Long id) {
        Servico servico = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado")
        );
        return servico.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Serviço",
        description = "Cadastra um novo serviço"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Serviço criado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Servico> create(@RequestBody @Valid Servico servico) {
        repository.save(servico);
        return ResponseEntity
                .created(servico.toEntityModel().getRequiredLink("self").toUri())
                .body(servico);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Serviço",
        description = "Deleta um serviço"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Serviço deletado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Serviço",
        description = "Atualiza um serviço específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Serviço atualizado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    public ResponseEntity<Servico> update(@PathVariable Long id, @RequestBody @Valid Servico servicoAtualizado) {
        Servico servico = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado")
        );
        
        servico.setEmpresa(servicoAtualizado.getEmpresa());
        servico.setDataServico(servicoAtualizado.getDataServico());
        servico.setDescricao(servicoAtualizado.getDescricao());
        servico.setStatus(servicoAtualizado.getStatus());
        
        repository.save(servico);
        
        return ResponseEntity.ok(servico);
    }
}
