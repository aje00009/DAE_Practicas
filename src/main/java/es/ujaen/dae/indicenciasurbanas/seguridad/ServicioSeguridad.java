package es.ujaen.dae.indicenciasurbanas.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .sessionManagement(session -> session.disable()) // Sin estado
                .addFilterAfter(new FiltroAutenticacionJwt(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        // Endpoints Públicos
                        .requestMatchers(HttpMethod.POST, "/incidencias/autenticacion").permitAll() // LOGIN
                        .requestMatchers(HttpMethod.POST, "/incidencias/usuarios").permitAll() // REGISTRO
                        .requestMatchers(HttpMethod.GET, "/incidencias").permitAll() // LISTAR

                        // Endpoints Admin (Crear/Borrar Tipos)
                        .requestMatchers("/incidencias/tipos/**").hasRole("ADMIN")

                        // Endpoints Autenticados (Usuarios y Admin)
                        .requestMatchers(HttpMethod.POST, "/incidencias").authenticated() // Crear incidencia
                        .requestMatchers(HttpMethod.DELETE, "/incidencias/{id}").authenticated() // Borrar incidencia
                        .requestMatchers(HttpMethod.PUT, "/incidencias/{id}").hasRole("ADMIN") // Modificar estado (Solo admin o personal)

                        // Cualquier otra cosa
                        .anyRequest().authenticated()
                )
                .build();
    }
}
