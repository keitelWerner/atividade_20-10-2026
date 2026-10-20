package br.edu.uniesp.assistencia.internal.ordemservico.service;

import br.edu.uniesp.assistencia.internal.cliente.entity.ClienteEntity;
import br.edu.uniesp.assistencia.internal.cliente.repository.ClienteRepository;
import br.edu.uniesp.assistencia.internal.equipamento.entity.EquipamentoEntity;
import br.edu.uniesp.assistencia.internal.equipamento.repository.EquipamentoRepository;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoRequest;
import br.edu.uniesp.assistencia.internal.ordemservico.dto.CriarOrdemServicoResponse;
import br.edu.uniesp.assistencia.internal.ordemservico.entity.OrdemServicoEntity;
import br.edu.uniesp.assistencia.internal.ordemservico.mapper.OrdemServicoMapper;
import br.edu.uniesp.assistencia.internal.ordemservico.repository.OrdemServicoRepository;
import br.edu.uniesp.assistencia.shared.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteRepository clienteRepository;
    private final EquipamentoRepository equipamentoRepository;

    @Transactional
    public CriarOrdemServicoResponse abrirOrdemServico(CriarOrdemServicoRequest request) {
        ClienteEntity cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente", request.clienteId()));

        EquipamentoEntity equipamento = equipamentoRepository.findById(request.equipamentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Equipamento", request.equipamentoId()));

        // As regras de domínio também são verificadas no construtor da entidade.
        String numeroProtocolo = gerarNumeroProtocolo();
        OrdemServicoEntity ordemServico = OrdemServicoMapper.paraEntidade(
                request, numeroProtocolo, cliente, equipamento);

        OrdemServicoEntity salva = ordemServicoRepository.save(ordemServico);
        return OrdemServicoMapper.paraResposta(salva);
    }

    private String gerarNumeroProtocolo() {
        String data = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // A verificação evita colisões mesmo que o sufixo aleatório se repita.
        for (int tentativa = 0; tentativa < 10; tentativa++) {
            String sufixo = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 4)
                    .toUpperCase(Locale.ROOT);
            String protocolo = "OS-" + data + "-" + sufixo;

            if (!ordemServicoRepository.existsByNumeroProtocolo(protocolo)) {
                return protocolo;
            }
        }

        throw new IllegalStateException("Não foi possível gerar um número de protocolo único.");
    }
}
