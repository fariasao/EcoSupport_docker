package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.ExibicaoController;
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
@Table(name = "tb_exibicoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exibicao {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Transacao", nullable = false)
    private Transacao transacao;

    @NotNull(message = "{exibicao.valor.notnull}")
    private Double valor;

    @NotNull(message = "{exibicao.dataExibicao.notnull}")
    private LocalDate dataExibicao;

    @NotBlank(message = "{exibicao.descricao.notblank}")
    private String descricao;

    public EntityModel<Exibicao> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, ExibicaoController.class, id);
    }
}
