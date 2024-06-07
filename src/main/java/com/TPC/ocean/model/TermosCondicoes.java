package com.TPC.ocean.model;

import java.time.LocalDate;

import org.springframework.hateoas.EntityModel;
import com.TPC.ocean.controller.TermosCondicoesController;
import com.TPC.ocean.util.HateoasHelper;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_termos_condicoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermosCondicoes {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_Usuario", nullable = false)
    @NotNull(message = "{termosCondicoes.idUsuario.notnull}")
    private Usuario usuario;

    @NotBlank(message = "{termosCondicoes.aceitou.notblank}")
    @Pattern(regexp = "0|1", message = "{contrato.aceitou.invalid}")
    private char aceitou;

    @NotNull(message = "{termosCondicoes.dataAceite.notnull}")
    private LocalDate dataAceite;

    public EntityModel<TermosCondicoes> toEntityModel() {
        return HateoasHelper.createModelWithLinks(this, TermosCondicoesController.class, id);
    }
}
