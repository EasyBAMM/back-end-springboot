package com.mycompany.webapp.security;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
	// 비밀키(노출이 되면 안됨)
	private static final String secretKey = "12345";

	/* JWT 생성 */
	// 개인정보 JWT 저장 금지
	// https://jwt.io/
	public static String createToken(String mid, String authority) {
		log.info("실행");
		String result = null;
		try {
			String token = Jwts.builder()
					// 헤더 설정
					.setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT")
					// 토큰의 유효기간
					.setExpiration(new Date(new Date().getTime() + 1000 * 60 * 60 * 24))
					// 페이로드 설정
					.claim("mid", mid).claim("authority", authority)
					// 서명 설정
					.signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
					// 토큰 생성
					.compact();
			result = token;
		} catch (Exception e) {
		}
		return result;
	}

	/* JWT 유효성 검사 */
	public static Claims validateToken(String token) {
		log.info("실행");
		Claims result = null;
		try {
			Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes("UTF-8")).parseClaimsJws(token).getBody();
			result = claims;
		} catch (Exception e) {
		}
		return result;
	}

	/* JWT에서 정보 얻기 */
	public static String getMid(Claims claims) {
		log.info("실행");
		return claims.get("mid", String.class);
	}

	public static String getAuthority(Claims claims) {
		log.info("실행");
		return claims.get("authority", String.class);
	}

	/* JWT 갱신 */
	public static String refreshToken(String token, Claims claims) {
		log.info("실행");
		long remainMillSecs = claims.getExpiration().getTime() - new Date().getTime();
		long seconds = remainMillSecs / 1000;
		log.info("" + remainMillSecs / 1000 + "초 남았음");
		if (seconds <= 0) {
			// 만료시간이 지났을 경우
			return null;
		} else if (seconds <= 60 * 10) {
			// 10분 이내로 남았을 경우 다시 토큰 생성
			String mid = claims.get("mid", String.class);
			String authority = claims.get("authority", String.class);
			return createToken(mid, authority);
		} else {
			// 10분 이상 남았을 경우 그대로 리턴
			return token;
		}
	}

	// 확인
//	public static void main(String[] args) throws Exception {
//		// 토큰 생성
//		String mid = "user";
//		String mrole = "ROLE_USER";
//		String jwt = createToken(mid, mrole);
//		log.info("jwt: " + jwt);
//
//		// 토큰 유효성 검사
//		Claims claims = validateToken(jwt);
//		if (claims != null) {
//			log.info("유효한 토큰");
//			log.info("mid: " + getMid(claims));
//			log.info("mrole: " + getAuthority(claims));
//		} else {
//			log.info("유효하지 않은 토큰");
//		}
//	}
}
