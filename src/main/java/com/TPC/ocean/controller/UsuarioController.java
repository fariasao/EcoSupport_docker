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

import com.TPC.ocean.model.Usuario;
import com.TPC.ocean.repository.UsuarioRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@CacheConfig(cacheNames = "usuarios")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {
    @Autowired
    UsuarioRepository repository;

    @Autowired
    PagedResourcesAssembler<Usuario> assembler;

    @GetMapping
    @Cacheable
    @Operation(
        summary = "Listar Usuários",
        description = "Retorna uma lista paginada de usuários"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Usuários listados"),
        @ApiResponse(responseCode = "404", description = "Usuários não encontrados")
    })
    public PagedModel<EntityModel<Usuario>> index(
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "asc") String direction) 
    {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortBy = Sort.by(sortDirection, sort);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortBy);

        Page<Usuario> page = repository.findAll(sortedPageable);
        return assembler.toModel(page);
    }

    @GetMapping("{id}")
    @Operation(
        summary = "Listar Usuário por ID",
        description = "Retorna um usuário específico"
    )
    @ApiResponses({ 
        @ApiResponse(responseCode = "200", description = "Usuário listado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public EntityModel<Usuario> show(@PathVariable Long id) {
        Usuario usuario = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
        );
        return usuario.toEntityModel();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Cadastrar Usuário",
        description = "Cadastra um novo usuário"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário criado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    public ResponseEntity<Usuario> create(@RequestBody @Valid Usuario usuario) {
        repository.save(usuario);
        return ResponseEntity
                .created(usuario.toEntityModel().getRequiredLink("self").toUri())
                .body(usuario);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Deletar Usuário",
        description = "Deleta um usuário específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
        );
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    @Operation(
        summary = "Atualizar Usuário",
        description = "Atualiza um usuário específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody @Valid Usuario usuarioAtualizado) {
        Usuario usuario = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
        );
        
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setSenha(usuarioAtualizado.getSenha());
        usuario.setTipo(usuarioAtualizado.getTipo());
        usuario.setEmpresa(usuarioAtualizado.getEmpresa());
        usuario.setInstituicao(usuarioAtualizado.getInstituicao());
        usuario.setPessoaFisica(usuarioAtualizado.getPessoaFisica());
        
        repository.save(usuario);
        
        return ResponseEntity.ok(usuario);
    }
}
