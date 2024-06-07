package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.InstituicaoController;
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
@Table(name = "tb_instituicoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instituicao {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{instituicao.nome.notblank}")
    private String nome;

    @NotBlank(message = "{instituicao.cnpj.notblank}")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "{instituicao.cnpj.pattern}")
    private String cnpj;

    @Email
    @NotBlank (message = "{instituicao.email.notblank}")
    @Size(max = 50, message = "{instituicao.email.size}")
    private String email;

    @NotBlank
    @Size(max = 15, message = "{instituicao.telefone.size}")
    private String telefone;

    @NotBlank (message = "{instituicao.ender.notnull}")
    @Size(max = 255, message = "{instituicao.ender.size}")
    private String endereco;

    public EntityModel<Instituicao> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, InstituicaoController.class, id);
    }
}
