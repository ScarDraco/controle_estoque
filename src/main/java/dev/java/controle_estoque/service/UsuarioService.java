package dev.java.controle_estoque.service;

import dev.java.controle_estoque.model.Usuario;
import dev.java.controle_estoque.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }


 public Usuario cadastrar(Usuario usuario) {
    
    Optional<Usuario> usuarioExistente = usuarioRepository.findByLogin(usuario.getLogin());
    if (usuarioExistente.isPresent()) {
        throw new IllegalArgumentException("Este login já está em uso!");
    }

   
    if (usuario.getSenha() == null || usuario.getSenha().trim().length() < 6) {
        throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres!");
    }

    if (usuario.getLogin() == null || usuario.getLogin().trim().length() < 5) {
        throw new IllegalArgumentException("O login deve ter no mínimo 5 caracteres!");
    }

    return usuarioRepository.save(usuario);
}

    
    public Usuario autenticar(String login, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login);
        
        if (usuarioOpt.isPresent() && usuarioOpt.get().getSenha().equals(senha)) {
            return usuarioOpt.get();
        }
        
        throw new IllegalArgumentException("Login ou senha inválidos!");
    }
}