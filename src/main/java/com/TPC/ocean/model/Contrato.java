package com.TPC.ocean.model;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.ContratoController;
import com.TPC.ocean.util.HateoasHelper;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tb_contratos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contrato {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Empresa", nullable = false)
    private Empresa empresa;

    @NotBlank(message = "{contrato.tipoContrato.notblank}")
    private String tipoContrato;

    @NotNull(message = "{contrato.dataInicio.notnull}")
    private LocalDate dataInicio;

    @NotNull(message = "{contrato.dataFim.notnull}")
    private LocalDate dataFim;

    @NotNull(message = "{contrato.valor.notnull}")
    private Double valor;

    @NotBlank(message = "{contrato.status.notblank}")
    private String status;

    @NotBlank(message = "{contrato.assinaturaPendente.notblank}")
    @Pattern(regexp = "0|1", message = "{contrato.assinaturaPendente.invalid}")
    private char assinaturaPendente;

    public EntityModel<Contrato> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, ContratoController.class, id);
    }
}
