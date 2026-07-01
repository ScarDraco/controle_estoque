package dev.java.controle_estoque.controller;

import dev.java.controle_estoque.model.Categoria;
import dev.java.controle_estoque.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping("/novo")
    public String novaCategoriaForm(HttpSession session) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }
        return "categorias/formulario";
    }
   
    @PostMapping("/salvar")
    public String salvarCategoria(@RequestParam String nome, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogado") == null) {
            return "redirect:/login";
        }

        if (nome == null || nome.trim().isEmpty()) {
            model.addAttribute("erro", "O nome da categoria não pode estar vazio!");
            return "categorias/formulario";
        }

        try {
            Categoria categoria = new Categoria(nome.trim());
            categoriaRepository.save(categoria);
            return "redirect:/produtos"; 
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar! Talvez essa categoria já exista.");
            return "categorias/formulario";
        }
    }
}