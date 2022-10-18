package com.javainuse.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2952250982822412549L;
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	@Value("${jwt.secret}")
	private String secret;

	public String generateToken(UserDetails userDetails){
		Map<String,Object> claims=new HashMap<>();
		return doGenerateToken(claims,userDetails.getUsername());
	}
     public String doGenerateToken(Map<String,Object> claims,String subject){
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALIDITY*1000))
				.signWith(SignatureAlgorithm.HS512,secret).compact();
			 }

	//for retrieveing any information from token we will need the secret key
	public Claims getAllClaimsFromToken(String token){
		Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
		return claims;
	}
	public <T> T getClaimFromToken(String token,Function<Claims,T> claimsResolver){
		Claims claims = getAllClaimsFromToken(token);
		T apply = claimsResolver.apply(claims);
		return apply;
	}

	//retrieve username from jwt token
	public String getUsernameFromToken(String token){
		String userName = getClaimFromToken(token, Claims::getSubject);
		System.out.println(userName);
		return userName;
	}

	//validate token
	public Boolean validateToken(String token,UserDetails userDetails){
		String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	public Date getIssuedAtDateFromToken(String token){
		Date claimFromToken = getClaimFromToken(token, Claims::getIssuedAt);
		return claimFromToken;
	}

	//retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token){
		Date claimFromToken = getClaimFromToken(token, Claims::getExpiration);
		return claimFromToken;
	}

	//check if the token has expired
	private Boolean isTokenExpired(String token){
		Date expirationDateFromToken = getExpirationDateFromToken(token);
		boolean before = expirationDateFromToken.before(new Date());
		return before;
	}
	private Boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

}
