package com.aluguelcarros.model.entity;

import com.aluguelcarros.model.exception.PedidoException;
import com.aluguelcarros.model.exception.UsuarioException;
import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o cliente do sistema de aluguel de carros.
 *
 * <p>Herda de {@link Usuario} via herança SINGLE_TABLE.
 * Regras de negócio aplicadas nesta entidade:
 * <ul>
 *   <li>Máximo de {@value #MAX_RENDIMENTOS} rendimentos por cliente.</li>
 *   <li>Só é possível cancelar pedidos nos status RASCUNHO ou ENVIADO.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Introspected
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    /** Número máximo de rendimentos que um cliente pode cadastrar. */
    public static final int MAX_RENDIMENTOS = 3;

    private String rg;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 255)
    private String endereco;

    @Column(length = 100)
    private String profissao;

    /**
     * Lista de rendimentos financeiros do cliente.
     * Limitada a {@value #MAX_RENDIMENTOS} itens pela regra de negócio.
     * Excluída do {@code toString()} para evitar loop circular com {@link Rendimento}.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Rendimento> rendimentos = new ArrayList<>();

    // ─────────────────────────────────────────────
    // Métodos de domínio (conforme diagrama de classes)
    // ─────────────────────────────────────────────

    /**
     * Adiciona um rendimento à lista do cliente, respeitando o limite de
     * {@value #MAX_RENDIMENTOS} rendimentos (RN01).
     *
     * @param rendimento rendimento a adicionar
     * @throws UsuarioException se o limite de rendimentos for atingido
     */
    public void adicionarRendimento(Rendimento rendimento) {
        if (this.rendimentos.size() >= MAX_RENDIMENTOS) {
            throw new UsuarioException(
                "Cliente não pode ter mais de " + MAX_RENDIMENTOS + " rendimentos cadastrados.");
        }
        rendimento.setCliente(this);
        this.rendimentos.add(rendimento);
    }

    /**
     * Associa um pedido a este cliente e o coloca no status RASCUNHO.
     * A persistência efetiva é delegada ao {@code PedidoService}.
     *
     * @param pedido pedido a ser cadastrado
     * @return {@code true} se a operação foi bem-sucedida
     */
    public boolean cadastrarPedido(PedidoImpl pedido) {
        pedido.setCliente(this);
        pedido.atualizarStatus(com.aluguelcarros.model.enums.PedidoStatus.RASCUNHO);
        return true;
    }

    /**
     * Cancela um pedido pertencente a este cliente.
     * Somente pedidos em RASCUNHO ou ENVIADO podem ser cancelados.
     *
     * @param pedido pedido a cancelar
     * @throws PedidoException se o status não permitir cancelamento
     */
    public void cancelarPedido(PedidoImpl pedido) {
        com.aluguelcarros.model.enums.PedidoStatus status = pedido.getStatus();
        if (status != com.aluguelcarros.model.enums.PedidoStatus.RASCUNHO &&
            status != com.aluguelcarros.model.enums.PedidoStatus.ENVIADO) {
            throw new PedidoException(
                "Pedido com status " + status + " não pode ser cancelado.");
        }
        pedido.atualizarStatus(com.aluguelcarros.model.enums.PedidoStatus.CANCELADO);
    }

    /**
     * Retorna uma visão imutável dos pedidos deste cliente.
     * A consulta completa é delegada ao {@code PedidoService} / repositórios.
     *
     * @return lista imutável de rendimentos (como exemplo de consulta local)
     */
    public List<Rendimento> consultarRendimentos() {
        return Collections.unmodifiableList(this.rendimentos);
    }
}
