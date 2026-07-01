package dev.java.controle_estoque.controller;

import dev.java.controle_estoque.model.Produto;
import dev.java.controle_estoque.model.Usuario;
import dev.java.controle_estoque.repository.CategoriaRepository;
import dev.java.controle_estoque.repository.ProdutoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final dev.java.controle_estoque.repository.MovimentacaoRepository movimentacaoRepository;

    public ProdutoController(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, dev.java.controle_estoque.repository.MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @GetMapping
    public String listarProdutos(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("usuarioLogado", usuarioLogado);

        return "produtos/lista"; 
    }

    @GetMapping("/novo")
    public String novoProdutoForm(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "produtos/formulario";
    }

    @PostMapping("/salvar")
    @org.springframework.transaction.annotation.Transactional
    public String salvarProduto(@RequestParam String nome,
                                @RequestParam Integer quantidade,
                                @RequestParam Double preco,
                                @RequestParam Long categoriaId,
                                HttpSession session, Model model) {
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        if (nome == null || nome.trim().isEmpty() || quantidade < 0 || preco < 0) {
            model.addAttribute("erro", "Dados inválidos! Verifique os valores digitados.");
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "produtos/formulario";
        }

        var categoriaOpt = categoriaRepository.findById(categoriaId);
        if (categoriaOpt.isEmpty()) {
            model.addAttribute("erro", "Categoria não encontrada.");
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "produtos/formulario";
        }

        Produto produto = new Produto(nome.trim(), quantidade, preco, usuarioLogado, categoriaOpt.get());
        Produto produtoSalvo = produtoRepository.save(produto);

        if (quantidade > 0) {
            dev.java.controle_estoque.model.Movimentacao movimentacaoInicial = new dev.java.controle_estoque.model.Movimentacao(
                dev.java.controle_estoque.enums.TipoMovimentacao.ENTRADA,
                quantidade,
                produtoSalvo,
                usuarioLogado
            );
            movimentacaoRepository.save(movimentacaoInicial);
        }
        
        
        return "redirect:/produtos";
    }


   @GetMapping("/deletar/{id}")
    @org.springframework.transaction.annotation.Transactional
    public String deletarProduto(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        var produtoOpt = produtoRepository.findById(id);
        if (produtoOpt.isPresent()) {
           
            movimentacaoRepository.deleteByProdutoId(id);
            
          
            produtoRepository.delete(produtoOpt.get());
        }

        return "redirect:/produtos";
    }
}