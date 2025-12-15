package es.ujaen.dae.indicenciasurbanas.seguridad;

import es.ujaen.dae.indicenciasurbanas.entidades.Usuario;
import es.ujaen.dae.indicenciasurbanas.repositorios.RepositorioUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioCredencialesUsuario implements UserDetailsService {

    @Autowired
    RepositorioUsuarios repositorioUsuarios;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuarios.buscar(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String rol = usuario.email().contains("admin") ? "ADMIN" : "USUARIO";

        return User.withUsername(usuario.email())
                .password(usuario.clave())
                .roles(rol)
                .build();
    }
}
