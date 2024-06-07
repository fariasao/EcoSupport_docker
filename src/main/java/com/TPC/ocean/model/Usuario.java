package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.UsuarioController;
import com.TPC.ocean.util.HateoasHelper;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{usuario.nome.notblank}")
    private String nome;

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.pattern}")
    private String email;

    @NotBlank(message = "{usuario.senha.notblank}")
    @Size(min = 8, message = "{usuario.senha.size}")
    private String senha;

    @NotBlank(message = "{usuario.tipo.notblank}")
    @Pattern(regexp = "pf|empresa|instituicao", message = "{usuario.tipo.invalid}")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "ID_Empresa", nullable = true)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "ID_Instituicao", nullable = true)
    private Instituicao instituicao;

    @ManyToOne
    @JoinColumn(name = "ID_Pessoa_Fisica", nullable = true)
    private PessoaFisica pessoaFisica;

    public EntityModel<Usuario> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, UsuarioController.class, id);
    }
}
