package dev.java.controle_estoque.controller;

import dev.java.controle_estoque.enums.TipoMovimentacao;
import dev.java.controle_estoque.model.Usuario;
import dev.java.controle_estoque.repository.ProdutoRepository;
import dev.java.controle_estoque.service.MovimentacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final ProdutoRepository produtoRepository;

    public MovimentacaoController(MovimentacaoService movimentacaoService, ProdutoRepository produtoRepository) {
        this.movimentacaoService = movimentacaoService;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping("/novo")
    public String novaMovimentacaoForm(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("tipos", TipoMovimentacao.values());
        return "movimentacoes/formulario";
    }

    @PostMapping("/salvar")
    public String salvarMovimentacao(@RequestParam Long produtoId,
                                     @RequestParam TipoMovimentacao tipo,
                                     @RequestParam Integer quantidade,
                                     HttpSession session, Model model) {
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            movimentacaoService.registrarMovimentacao(produtoId, tipo, quantidade, usuarioLogado);
            return "redirect:/produtos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("produtos", produtoRepository.findAll());
            model.addAttribute("tipos", TipoMovimentacao.values());
            return "movimentacoes/formulario";
        }
    }
}