package dev.java.controle_estoque.repository;

import dev.java.controle_estoque.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}