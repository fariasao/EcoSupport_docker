package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.EmpresaController;
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
@Table(name = "tb_empresas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{empresa.nome.notblank}")
    private String nome;

    @NotBlank(message = "{empresa.cnpj.notblank}")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "{empresa.cnpj.pattern}")
    private String cnpj;

    @Email
    @NotBlank (message = "{empresa.email.notnull}")
    @Size(max = 50, message = "{empresa.email.size}")
    private String email;

    @NotBlank
    @Size(max = 15, message = "{empresa.telefone.size}")
    private String telefone;
    
    @NotBlank (message = "{empresa.ender.notnull}")
    @Size(max = 255, message = "{empresa.ender.size}")
    private String endereco;

    public EntityModel<Empresa> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, EmpresaController.class, id);
    }
}
