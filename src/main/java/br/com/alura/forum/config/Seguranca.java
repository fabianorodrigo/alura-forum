package br.com.alura.forum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.alura.forum.repository.IUsuarioRepository;

@EnableWebSecurity
@Configuration
public class Seguranca extends WebSecurityConfigurerAdapter {

    @Autowired
    private AutenticacaoService autenticacaoService;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    // configurações de autenticação
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
    }

    // O Spring não consegue fazer a injeção de dependência da
    // classe AuthenticationManager, a menos que sobreescrevamos este método
    // com a anotação @Bean
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    // configurações de autorização
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // permite GET em /topicos e /topicos/* pra qualquer um
        // e as demais demandará estar autenticado
        // Se não estiver autenticado, abre o form login oferecido pelo Spring
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/topicos").permitAll()
                .antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
                // liberando acesso ao endpoint do Actuator (monitoramento)
                .antMatchers(HttpMethod.GET, "/actuator/*").permitAll()
                // liberando o endpoint para que alguém não autenticado consiga acessá-la para
                // mandar seus dados de autenticação. Sem essa linha (até o .permiteAll()), vai
                // gerar um 403
                .antMatchers(HttpMethod.POST, "/auth").permitAll().anyRequest().authenticated()
                // O .and().formLogin() é para manter sessão via Cookie (JSessionId)
                // .and().formLogin();

                // Via token não precisa da proteção CSRF
                .and().csrf().disable()
                // Com a linha abaixo colocamos a API como stateless (princípio REST)
                // Com isso, perde-se o form de login já oferecido pelo Spring.
                // Assim, vai ser necessário criar um controller para fazer a autenticação que,
                // neste projeto, implementamos no AutenticacaoController.java
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // a linha abaixo coloca um filtro customizado que implementa a lógica de
                // autenticação por token antes do filtro padrão do Spring
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(new AutenticacaoViaTokenFilter(this.tokenService, this.usuarioRepository),
                        UsernamePasswordAuthenticationFilter.class);
    }

    // configurações de recursos estáticos (arquivos css, imagens, js)
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**.html", "/v2/api-docs", "/webjars/**", "/configuration/**",
                "/swagger-resources/**");
    }
}
