package com.marcblais.scrabbleapi.encryption;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class EmailToken {
    private static final String SECRET = "this is my secret key";

    public static String createJwtForEmail(String username, String email) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withIssuer("scrabble cheetah")
                .withClaim("username", username)
                .withClaim("email", email)
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

    public static String getEmailFromJwt(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("scrabble cheetah").build();
            DecodedJWT jwt = verifier.verify(token);

            return jwt.getClaim("email").asString();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
