package br.com.cad.digital.controllers;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cad.digital.exception.RestNotFoundException;
import br.com.cad.digital.models.Credencial;
import br.com.cad.digital.models.UsuarioModel;
import br.com.cad.digital.repository.UsuarioRepository;
import br.com.cad.digital.service.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    Logger log = LoggerFactory.getLogger(UsuarioController.class);

    List<UsuarioModel> usuarios = new ArrayList<>();

    @Autowired
    UsuarioRepository repository;

    @Autowired
    PagedResourcesAssembler<UsuarioModel> assembler;

    @Autowired
    AuthenticationManager manager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    TokenService tokenService;

    @GetMapping
    public PagedModel<EntityModel<UsuarioModel>> index(@RequestParam(required = false) String nome,@PageableDefault(size = 3) Pageable pageable) {
        Page<UsuarioModel> usuarios = (nome == null)? 
            repository.findAll(pageable): 
            repository.findByNomeContainingIgnoreCase(nome, pageable);
        
        return assembler.toModel(usuarios);
        //return usuarios.map(UsuarioModel::toEntityModel);
    }

    @GetMapping("{id}")
    public EntityModel<UsuarioModel> show(@PathVariable Long id) {
        log.info("Buscando usuário com o id: " + id);
        return getUsuario(id).toEntityModel();
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UsuarioModel usuario) {
        log.info("cadastrando usuário" + usuario);
        usuario.setSenha(encoder.encode(usuario.getSenha()));
        repository.save(usuario);
        return ResponseEntity
                .created(usuario.toEntityModel().getRequiredLink("self").toUri())
                .body(usuario.toEntityModel());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid Credencial credencial){
        manager.authenticate(credencial.toAuthentication());
        var token = tokenService.generateToken(credencial);
        return ResponseEntity.ok(token);
    }

    @PutMapping("{id}")
    public EntityModel<UsuarioModel> update(@PathVariable Long id, @RequestBody @Valid UsuarioModel usuario) {
        log.info("Atualizando usuário com o id: " + id);
        getUsuario(id);
        usuario.setId(id);
        repository.save(usuario);
        return usuario.toEntityModel();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UsuarioModel> destroy(@PathVariable Long id) {
        log.info("Apagando usuário com o id: " + id);
        repository.delete(getUsuario(id));
        return ResponseEntity.noContent().build();
    }

    private UsuarioModel getUsuario(Long id) {
        return repository.findById(id).orElseThrow
        (() -> new RestNotFoundException("Usuário não encontrado"));
    }
}
