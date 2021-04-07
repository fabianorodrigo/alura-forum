package br.com.alura.forum.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class Seguranca extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private AutenticacaoService autenticacaoService;

    //configurações de autenticação
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
    }

    //configurações de autorização
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //permite GET em /topicos e /topicos/* pra qualquer um
        //e as demais demandará estar autenticado
        //Se não estiver autenticado, abre o form login oferecido pelo Spring
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/topicos").permitAll()
        .antMatchers(HttpMethod.GET, "/topicos/*").permitAll()
        .anyRequest().authenticated().and().formLogin();
    }

    //configurações de recursos estáticos (arquivos css, imagens, js)
    @Override
    public void configure(WebSecurity web) throws Exception {
        // TODO Auto-generated method stub
        super.configure(web);
    }
}
