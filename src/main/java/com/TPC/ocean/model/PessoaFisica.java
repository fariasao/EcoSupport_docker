package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.PessoaFisicaController;
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
@Table(name = "tb_pessoas_fisicas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaFisica {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{pessoafisica.nome.notblank}")
    private String nome;

    @NotBlank(message = "{pessoafisica.cpf.notblank}")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "{pessoafisica.cpf.pattern}")
    private String cpf;

    @NotBlank(message = "{pessoafisica.email.notblank}")
    @Email(message = "{pessoafisica.email.pattern}")
    private String email;

    @NotBlank(message = "{pessoafisica.senha.notblank}")
    @Size(min = 8, message = "{pessoafisica.senha.size}")
    private String senha;

    public EntityModel<PessoaFisica> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, PessoaFisicaController.class, id);
    }
}
