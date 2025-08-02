package app.sigorotalk.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 모든 Origin 허용
        // (credentials=true 하고도 와일드카드 패턴을 쓸 수 있도록 allowedOriginPatterns 사용)
        config.setAllowedOriginPatterns(List.of("*"));
        // 모든 HTTP 메서드 허용
        config.setAllowedMethods(List.of("*"));
        // 모든 헤더 허용
        config.setAllowedHeaders(List.of("*"));
        // 쿠키, Authorization 헤더 등 인증 정보 허용
        config.setAllowCredentials(true);
        // preflight 캐싱 시간 (초)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 CORS 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
