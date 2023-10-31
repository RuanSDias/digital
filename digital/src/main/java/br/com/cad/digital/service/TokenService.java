package br.com.cad.digital.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.cad.digital.models.Credencial;
import br.com.cad.digital.models.Token;
import br.com.cad.digital.models.UsuarioModel;
import br.com.cad.digital.repository.UsuarioRepository;
import jakarta.validation.Valid;

@Service
public class TokenService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Value("${jwt.secret}")
    String secret;

    public Token generateToken(@Valid Credencial credencial) {
        Algorithm alg = Algorithm.HMAC256(secret);
        String token =  JWT.create()
                    .withSubject(credencial.email())
                    .withIssuer("Digital")
                    .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                    .sign(alg)
                    ;
        return new Token(token, "JWT", "Bearer");
    }

    public UsuarioModel getValidateUser(String token) {
        Algorithm alg = Algorithm.HMAC256(secret);
        var email =  JWT.require(alg)
                    .withIssuer("Digital")
                    .build()
                    .verify(token)
                    .getSubject();
        return usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new JWTVerificationException("Usuário inválido"));
    }
    
}
