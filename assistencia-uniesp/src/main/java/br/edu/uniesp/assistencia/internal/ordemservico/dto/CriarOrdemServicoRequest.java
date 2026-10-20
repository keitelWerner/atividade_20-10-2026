package br.edu.uniesp.assistencia.internal.ordemservico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CriarOrdemServicoRequest(

        @NotNull(message = "O ID do cliente é obrigatório")
        @Positive(message = "O ID do cliente deve ser maior que zero")
        Long clienteId,

        @NotNull(message = "O ID do equipamento é obrigatório")
        @Positive(message = "O ID do equipamento deve ser maior que zero")
        Long equipamentoId,

        @NotBlank(message = "A descrição do problema é obrigatória")
        @Size(max = 5000, message = "A descrição do problema deve ter no máximo 5000 caracteres")
        String descricaoProblema
) {
}
