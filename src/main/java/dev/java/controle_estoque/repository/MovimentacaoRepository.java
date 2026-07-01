package dev.java.controle_estoque.repository;

import dev.java.controle_estoque.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    void deleteByProdutoId(Long produtoId);

}