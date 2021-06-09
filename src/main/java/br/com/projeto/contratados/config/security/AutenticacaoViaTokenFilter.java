package br.com.projeto.contratados.config.security;

import br.com.projeto.contratados.domain.entity.usuario.Usuario;
import br.com.projeto.contratados.domain.repository.usuario.UsuarioRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private UsuarioRepository usuarioRepository;

    public AutenticacaoViaTokenFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String token = recuperarToken(httpServletRequest);
        Boolean valido = tokenService.tokenIsValid(token);
        if(valido){
            autenticarUsuario(token);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void autenticarUsuario(String token) {
        String emailUsuario = tokenService.getEmailUsuario(token); //Antes era findById
        Usuario usuario = usuarioRepository.findByUserEmail(emailUsuario).get();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getUser().getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recuperarToken(HttpServletRequest httpServletRequest) {

        String token = httpServletRequest.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer")){
            return null;
        }
        return token.substring(7, token.length());
    }

}
