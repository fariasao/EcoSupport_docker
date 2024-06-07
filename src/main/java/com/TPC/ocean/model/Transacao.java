package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.TransacaoController;
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
@Table(name = "tb_transacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Contrato", nullable = false)
    private Contrato contrato;

    @NotNull(message = "{transacao.data.notnull}")
    private LocalDate data;

    @NotNull(message = "{transacao.valor.notnull}")
    private Double valor;

    @NotBlank(message = "{transacao.descricao.notblank}")
    private String descricao;

    public EntityModel<Transacao> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, TransacaoController.class, id);
    }
}
