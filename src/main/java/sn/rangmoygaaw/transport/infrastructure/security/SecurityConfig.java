package sn.rangmoygaaw.transport.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // La création de comptes ADMIN doit rester protégée : elle est
                // déclarée AVANT la règle permitAll ci-dessous, sinon celle-ci
                // l'emporterait (le premier matcher qui correspond gagne).
                .requestMatchers("/api/v1/auth/register-admin").hasRole("ADMIN")
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/ws/**").permitAll()

                // Documentation OpenAPI accessible en développement
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                 "/v3/api-docs/**").permitAll()

                // Le rappel de paiement est appelé par les serveurs PayTech,
                // qui ne présentent pas de jeton : l'authenticité est vérifiée
                // par la signature du payload (voir TicketRestController).
                .requestMatchers(HttpMethod.POST, "/api/v1/transport/tickets/ipn").permitAll()

                .requestMatchers(HttpMethod.POST,  "/api/v1/transport/bus/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,   "/api/v1/transport/bus/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/transport/bus/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/transport/bus/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST,  "/api/v1/transport/lignes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,   "/api/v1/transport/lignes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/transport/lignes/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST,  "/api/v1/transport/chauffeurs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,   "/api/v1/transport/chauffeurs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/transport/chauffeurs/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/transport/chauffeurs/**").hasRole("ADMIN")

               // .requestMatchers(HttpMethod.PUT,   "/api/v1/transport/incidents/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/transport/incidents/**").hasRole("ADMIN")
               // .requestMatchers(HttpMethod.DELETE,"/api/v1/transport/incidents/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(Arrays.asList(
            "GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}