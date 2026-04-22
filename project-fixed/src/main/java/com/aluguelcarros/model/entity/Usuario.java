package com.aluguelcarros.model.entity;

import io.micronaut.core.annotation.Introspected;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade abstrata raiz da hierarquia de usuários do sistema.
 *
 * <p>Utiliza herança {@code SINGLE_TABLE}: {@link Cliente} e {@link Agente} são
 * armazenados na mesma tabela {@code usuarios}, diferenciados pela coluna
 * discriminadora {@code tipo_usuario}.
 *
 * <p><strong>Segurança:</strong> o campo {@code senha} deve armazenar apenas hashes
 * BCrypt. Nunca persista senhas em texto puro em ambiente de produção.
 */
@Data
@NoArgsConstructor
@Introspected
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String login;

    /**
     * Armazena o hash da senha (BCrypt).
     * Em produção, utilize {@code BCryptPasswordEncoder} ou similar antes de persistir.
     */
    @Column(nullable = false)
    private String senha;

    /**
     * Perfil de acesso: "CLIENTE" ou "AGENTE".
     * Usado pelo sistema de autorização para controle de rotas.
     */
    @Column(nullable = false, length = 20)
    private String perfil;

    /**
     * Verifica se a senha fornecida corresponde à senha armazenada.
     *
     * @param senhaFornecida senha em texto puro (em produção deve ser verificada contra o hash)
     * @return {@code true} se a autenticação for bem-sucedida
     */
    public boolean autenticar(String senhaFornecida) {
        // PROTOTYPE: comparação direta. Em produção: BCrypt.checkpw(senhaFornecida, this.senha)
        return this.senha != null && this.senha.equals(senhaFornecida);
    }

    /**
     * Atualiza a senha do usuário.
     *
     * @param novaSenha nova senha (em produção deve ser hashada antes de chamar este método)
     */
    public void atualizarSenha(String novaSenha) {
        if (novaSenha == null || novaSenha.isBlank()) {
            throw new IllegalArgumentException("Nova senha não pode ser vazia.");
        }
        this.senha = novaSenha;
    }
}
