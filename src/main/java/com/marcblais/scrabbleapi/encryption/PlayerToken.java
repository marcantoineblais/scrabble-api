package com.marcblais.scrabbleapi.encryption;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class PlayerToken {
    private static final String SECRET = "this is my secret key";

    public static String createJwtForPlayer(String username) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer("scrabble cheetah")
                .withClaim("username", username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 minutes
                .sign(algorithm);
    }

    public static String getUsernameFromJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("scrabble cheetah").build();
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getClaim("username").asString();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
