package com.aluguelcarros.model.entity;

import com.aluguelcarros.model.exception.PedidoException;
import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Representa o agente financeiro do sistema (Banco ou Empresa).
 *
 * <p>O agente é responsável por analisar, aprovar ou reprovar os pedidos
 * de aluguel submetidos pelos clientes, e por registrar contratos.
 *
 * <p>Herda de {@link Usuario} via herança SINGLE_TABLE.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Introspected
@Entity
@DiscriminatorValue("AGENTE")
public class Agente extends Usuario {

    @Column(nullable = false, length = 150)
    private String nome;

    /**
     * Tipo do agente financeiro. Valores esperados: "BANCO" ou "EMPRESA".
     */
    @Column(length = 20)
    private String tipo;

    @Column(length = 100)
    private String contato;

    // ─────────────────────────────────────────────
    // Métodos de domínio (conforme diagrama de classes)
    // ─────────────────────────────────────────────

    /**
     * Registra que o agente iniciou a análise do pedido,
     * transitando o status para EM_ANALISE.
     *
     * @param pedido pedido a ser analisado
     * @return {@code true} se o pedido foi aceito para análise
     * @throws PedidoException se o pedido não estiver no status ENVIADO
     */
    public boolean analisarPedido(PedidoImpl pedido) {
        if (pedido.getStatus() != com.aluguelcarros.model.enums.PedidoStatus.ENVIADO) {
            throw new PedidoException(
                "Somente pedidos com status ENVIADO podem ser analisados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(com.aluguelcarros.model.enums.PedidoStatus.EM_ANALISE);
        return true;
    }

    /**
     * Aprova o pedido e gera um {@link Contrato} a partir dele.
     *
     * @param pedido pedido a ser aprovado
     * @return contrato gerado (ainda não persistido; delegar ao {@code PedidoService})
     * @throws PedidoException se o pedido não estiver em EM_ANALISE
     */
    public Contrato aprovarPedido(PedidoImpl pedido) {
        if (pedido.getStatus() != com.aluguelcarros.model.enums.PedidoStatus.EM_ANALISE) {
            throw new PedidoException(
                "Somente pedidos EM_ANALISE podem ser aprovados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(com.aluguelcarros.model.enums.PedidoStatus.APROVADO);

        Contrato contrato = new Contrato();
        contrato.setTipo(this.tipo);
        contrato.setDataAssinatura(LocalDate.now());
        contrato.setValorFinanciado(pedido.calcularValor());
        contrato.vincularPedido(pedido);
        return contrato;
    }

    /**
     * Reprova o pedido, impedindo a geração de contrato.
     *
     * @param pedido pedido a ser reprovado
     * @throws PedidoException se o pedido não estiver em EM_ANALISE
     */
    public void reprovarPedido(PedidoImpl pedido) {
        if (pedido.getStatus() != com.aluguelcarros.model.enums.PedidoStatus.EM_ANALISE) {
            throw new PedidoException(
                "Somente pedidos EM_ANALISE podem ser reprovados. Status atual: "
                + pedido.getStatus());
        }
        pedido.atualizarStatus(com.aluguelcarros.model.enums.PedidoStatus.REPROVADO);
    }
}
