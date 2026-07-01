package dev.java.controle_estoque.controller;

import dev.java.controle_estoque.model.Usuario;
import dev.java.controle_estoque.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String telaLogin() {
        return "login"; // Vai procurar em templates/login.html
    }

    @PostMapping("/login")
    public String efetuarLogin(@RequestParam String login, @RequestParam String senha, HttpSession session, Model model) {
        try {
            Usuario usuarioLogado = usuarioService.autenticar(login, senha);
            // Salva o usuário na sessão do navegador
            session.setAttribute("usuarioLogado", usuarioLogado);
            return "redirect:/produtos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "login";
        }
    }


    @GetMapping("/cadastro")
    public String telaCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String efetuarCadastro(@RequestParam String login, @RequestParam String senha, Model model) {
        try {
            Usuario novoUsuario = new Usuario(login, senha);
            usuarioService.cadastrar(novoUsuario);
            model.addAttribute("sucesso", "Usuário cadastrado com sucesso! Faça o login.");
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }
}