package br.com.alura.forum.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.IUsuarioRepository;

//Implementação de um filtro para autenticação via filtro.
//Herdando de OncePerRequestFilter, ele será solicitado em todo request e somente uma vez
//PS: Não existe anotação para registrar o filtro. Esse registro é feito manualmente
// no método "configure" da nossa especialização de "WebSecurityConfigurerAdapter"
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

    private IUsuarioRepository usuarioRepository;
    private TokenService tokenService;

    // ATENÇÃO: Classes filtros não conseguem ter injeção, por isso não rola
    // @AutoWired no UsuarioRepository nem no tokenService acima.
    // Como o filtro foi instanciado manualmente por nós, na classe
    // SecurityConfigurations, o Spring não consegue realizar injeção de
    // dependências via @Autowired
    // Assim, precisamos criar um construtor para passar uma instância deste
    public AutenticacaoViaTokenFilter(TokenService tokenService, IUsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recuperarToken(request);
        boolean valido = tokenService.isTokenValido(token);
        if (valido) {
            // Se for um token válido, chama o método autenticar que ...
            autenticarCliente(token);
        }
        filterChain.doFilter(request, response);
    }

    private void autenticarCliente(String token) {
        // busca o id do usuário que está no token (pois quando foi criado, nós
        // colocamos o ID lá)
        Long idUsuario = tokenService.getIdUsuario(token);
        // Buscamos o usuário pelo ID do token
        Usuario usuario = usuarioRepository.findById(idUsuario).get();
        // Força a autenticação. FAla pro Spring que o usuário passado está autenticado
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recuperarToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring("Bearer ".length());
    }

}
