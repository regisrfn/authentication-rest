package com.rufino.server.domain;

import static com.rufino.server.constant.SecurityConst.AUTHORITIES;
import static com.rufino.server.constant.SecurityConst.EXPIRATION_TIME;
import static com.rufino.server.constant.SecurityConst.RUFINO_LLC;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.rufino.server.enumeration.Authority;
import com.rufino.server.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.github.cdimascio.dotenv.Dotenv;

@Component
public class JwtToken {

    private Algorithm algorithm;
    private Dotenv dotenv;
    private String jwtSecret;

    @Autowired
    public JwtToken(Dotenv dotenv) {
        this.dotenv = dotenv;
        this.jwtSecret = this.dotenv.get("JWT_SECRET");
        this.algorithm = Algorithm.HMAC256(jwtSecret);
    }

    public JWTVerifier getVerifier() {
        return JWT.require(this.algorithm).withIssuer(RUFINO_LLC).build();
    }

    public String generateToken(User user) {
        String[] claims = getClaimsFromUser(user);
        Date currentDate = new Date();
        return JWT.create()
                  .withIssuedAt(currentDate)
                  .withIssuer(RUFINO_LLC)
                  .withSubject(user.getUsername())
                  .withArrayClaim(AUTHORITIES, claims)
                  .withExpiresAt(new Date(currentDate.getTime() + EXPIRATION_TIME))
                  .sign(this.algorithm);
    }

    public boolean isTokenValid(String username, String token) {
        return StringUtils.hasText(username) && verifyJwtToken(token);
    }

    public String getSubject(String jwt) {
        JWTVerifier verifier = getVerifier();
        return verifier.verify(jwt).getSubject();
    }

    public List<String> getClaimsFromToken(String token) {
        JWTVerifier verifier = getVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asList(String.class);
    }
    
    //////////////////////////// PRIVATE////////////////
    private boolean verifyJwtToken(String token) {
        JWTVerifier verifier = getVerifier();
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromUser(User user) {
        List<String> authorities = new ArrayList<>();
        for (Authority grantedAuthority : user.getAuthorityList())
            authorities.add(grantedAuthority.name());
        return authorities.toArray(new String[0]);
    }
}
