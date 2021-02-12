package com.rufino.server.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.rufino.server.model.Token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;

@Configuration
public class JwtConfig {

    private Algorithm algorithm;
    private Dotenv dotenv;
    private String jwtSecret;

    public JwtConfig() {
        dotenv = Dotenv.configure().ignoreIfMissing().load();
        jwtSecret = dotenv.get("JWT_SECRET");
    }

    @Bean
    public Token getToken() {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        return new Token(JWT.create().withClaim("rufino", 181818).sign(algorithm));
    }

    @Bean
    public JWTVerifier verifier(){
        this.algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.require(algorithm).build();
    }
}
