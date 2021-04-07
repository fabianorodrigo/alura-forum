package br.com.alura.forum.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.IUsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService{

    @Autowired
    private IUsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = repository.findByEmail(username);
        if(usuario.isPresent()){
            return usuario.get();
        }
        throw new UsernameNotFoundException("Dados inválidos");
    }
    
   /* public static void main(String[] args){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }*/
}
