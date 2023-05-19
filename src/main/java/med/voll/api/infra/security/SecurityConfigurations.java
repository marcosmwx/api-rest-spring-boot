package med.voll.api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration   // Spring le como uma classe de configuracao
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    private SecurityFilter securityFilter;
    @Bean  // Devolve um obj para o string
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                //desativa o tratamento de seguranca para csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // habilita a autentitacao Stateless
                // nao mais vai mostrar a tela de login e senha padrao
                // essa configuracao e valida pois utilizaremos token jwt para gerenciar autorizacao do usuario
                .and().authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/login").permitAll() // fazer a liberacao do login
                .anyRequest().authenticated()
                .and().addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) //chama primeiro o meu filtro antes do filtro do spring
                .build();

        /* Opcao Caso queria adicionar uma configuracao onde apenas o admin podera excluir um medico adicione algo como:
        * .requestMatchers(HttpMethod.DELETE, "/medicos").hasRole("ADMIN")
        * .requestMatchers(HttpMethod.DELETE, "/pacientes").hasRole("ADMIN")
        *  */
    }

    // classe para criar um objeto de autentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
