package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component   // anotacao para um componente generico
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private  TokenService tokenService;  // chamando a classe de dentro do projeto com anotacao para o spring injetar a classe

    @Autowired
    private UsuarioRepository repository;

    // herda da classe do spring
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    // Classe que garante que vai ser executada uma vez por requisicão

        //logica de recuperar o token ( token vai dentro de um cabecalho autorization)
        var tokenJWT = recuperarToken(request);

        if(tokenJWT != null) {

            var subject = tokenService.getSubject(tokenJWT);
            var usuario = repository.findByLogin(subject);

            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            //instancia o objeto para que possamos passar ele para o spring e dizer que estamos logado fazendo autenticacao

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Logado na req");
        }
        filterChain.doFilter(request, response);
        // necessario para chamar os proximos filtros na aplicacao ou ir direto para o controller
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        /* neste caso, para fazer os testes utilizei o insomnia para efetuar o login
        Passando como json o login e a senha cadastro no banco de dados
        ele retorna o token
        Atraves deste token podemos fazer as proximas requisicoes
         */
        return null;
    }


}
