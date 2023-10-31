package br.com.cad.digital.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.cad.digital.models.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long>{

    Page<UsuarioModel> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Optional<UsuarioModel> findByEmail(String email);
    
}
