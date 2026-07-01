package dev.java.controle_estoque.service;

import dev.java.controle_estoque.enums.TipoMovimentacao;
import dev.java.controle_estoque.model.Movimentacao;
import dev.java.controle_estoque.model.Produto;
import dev.java.controle_estoque.model.Usuario;
import dev.java.controle_estoque.repository.MovimentacaoRepository;
import dev.java.controle_estoque.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository, ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Movimentacao registrarMovimentacao(Long produtoId, TipoMovimentacao tipo, Integer quantidade, Usuario usuario) {
 
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado!"));

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero!");
        }

        if (tipo == TipoMovimentacao.ENTRADA) {
            produto.setQuantidade(produto.getQuantidade() + quantidade);
        } else if (tipo == TipoMovimentacao.SAIDA) {
            if (produto.getQuantidade() < quantidade) {
                throw new IllegalArgumentException("Estoque insuficiente! Estoque atual: " + produto.getQuantidade());
            }
            produto.setQuantidade(produto.getQuantidade() - quantidade);
        }

       
        produto.setUsuarioAlteracao(usuario);

      
        produtoRepository.save(produto);

      
        Movimentacao movimentacao = new Movimentacao(tipo, quantidade, produto, usuario);
        return movimentacaoRepository.save(movimentacao);
    }
}