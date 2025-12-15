package es.ujaen.dae.indicenciasurbanas.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class ServicioSeguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // <--- ¡AQUÍ ESTÁ LA CLAVE!
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new FiltroAutenticacionJwt(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        // Endpoints Públicos
                        .requestMatchers(HttpMethod.POST, "/incidencias/autenticacion").permitAll()
                        .requestMatchers(HttpMethod.POST, "/incidencias/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/incidencias").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Endpoints Admin
                        .requestMatchers("/incidencias/tipos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/incidencias/{id}").hasRole("ADMIN")

                        // Endpoints Autenticados
                        .requestMatchers(HttpMethod.POST, "/incidencias").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/incidencias/{id}").authenticated()

                        .anyRequest().authenticated()
                )
                .build();
    }

    /**
     * Define quién puede entrar. Esto es como la lista de invitados.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Permitimos el origen del frontend (Angular/React suelen usar el 4200 o 3000)
        config.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:3000"));

        // Permitimos los verbos HTTP necesarios
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitimos las cabeceras para enviar el Token
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}