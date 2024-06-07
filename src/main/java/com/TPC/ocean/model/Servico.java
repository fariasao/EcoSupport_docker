package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.ServicoController;
import com.TPC.ocean.util.HateoasHelper;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_servicos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servico {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Empresa", nullable = false)
    private Empresa empresa;

    @NotNull(message = "{servico.dataServico.notnull}")
    private LocalDate dataServico;

    @NotBlank(message = "{servico.descricao.notblank}")
    private String descricao;

    @NotBlank(message = "{servico.status.notblank}")
    private String status;

    public EntityModel<Servico> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, ServicoController.class, id);
    }
}
