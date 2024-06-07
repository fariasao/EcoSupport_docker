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

import com.TPC.ocean.model.Transacao;
import com.TPC.ocean.repository.TransacaoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transacoes")
@CacheConfig(cacheNames = "transacoes")
@Tag(name = "Transações", description = "Gerenciamento de transações")
public class TransacaoController {
    @Autowired
    TransacaoRepository repository;

    @Autowired
    PagedResourcesAssembler<Transacao> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Transações",
        description = "Retorna uma lista paginada de transações"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Transações listadas"),
        @ApiResponse(responseCode = "404", description = "Transações não encontradas")
    })
    public PagedModel<EntityModel<Transacao>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Transacao> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Transação por ID",
        description = "Retorna uma transação específica"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Transação listada"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    public EntityModel<Transacao> show(@PathVariable Long id) {
        Transacao transacao = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada")
        );
        return transacao.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Transação",
        description = "Cadastra uma nova transação"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transação criada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Transacao> create(@RequestBody @Valid Transacao transacao) {
        repository.save(transacao);
        return ResponseEntity
                .created(transacao.toEntityModel().getRequiredLink("self").toUri())
                .body(transacao);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Transação",
        description = "Deleta uma transação específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Transação deletada"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Transação",
        description = "Atualiza uma transação específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transação atualizada"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    public ResponseEntity<Transacao> update(@PathVariable Long id, @RequestBody @Valid Transacao transacaoAtualizada) {
        Transacao transacao = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada")
        );
        
        transacao.setContrato(transacaoAtualizada.getContrato());
        transacao.setData(transacaoAtualizada.getData());
        transacao.setValor(transacaoAtualizada.getValor());
        transacao.setDescricao(transacaoAtualizada.getDescricao());
        
        repository.save(transacao);
        
        return ResponseEntity.ok(transacao);
    }
}
