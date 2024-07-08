package com.example.demo.services;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ResourceNotFound;
import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@Service
public class UserServices {

	 
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String obterNomeDoUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); // Use UserDetails instead of UserDetailsService
        String userInfo = userDetails.toString();
        return userInfo.substring(userInfo.indexOf("Username=") + 9,userInfo.indexOf(','));
    }
    
    public String mostrarPermissoes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        StringBuilder roles = new StringBuilder();
        for (GrantedAuthority authority : authorities) {
            roles.append(authority.getAuthority()).append(" ");
        }

        return roles.toString().trim();
    }
    
    @CacheEvict(value = "usuarios", allEntries = true)
    public User saveUser(String username, String rawPassword, String role) {
        
    	String permissao = this.mostrarPermissoes();
    	if( permissao.equals("ROLE_USER") && !role.equals(permissao)) {
    		
    		throw new UnsuportedOp("Usuários não podem cadastrar administradores sua permissao é de " + permissao);
    	}
    	List<User> usuarios = this.listarUsuarios();
    	for(User usuario : usuarios) {
    		
    		if(usuario.getUsername().equals(username)) {
    			
    			throw new UnsuportedOp("Usuário já cadastrado");
    		}
    	}
    	User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }
    
    @Cacheable(value = "usuarios")
    public List<User> listarUsuarios(){
    	
    	return userRepository.findAll();
    }
    
    public User buscarUsuario(String id){
    	
    	User usuario = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));
		return usuario;
    }
    
    @CacheEvict(value = "usuarios", allEntries = true)
	public void excluirUsuario(String id) {
		
    	String nome = this.obterNomeDoUsuario();
    	String permissao = this.mostrarPermissoes();
		User usuario = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));
		if(permissao.equals("ROLE_USER") && !nome.equals(usuario.getUsername())) {
			
			throw new UnsuportedOp("Somente Admins podem deletar outros usuários");
		}
		userRepository.delete(usuario);
		
	}
	
    @CacheEvict(value = "usuarios", allEntries = true)
	public void atualizarUsuario(String id, User subject) {
		
    	User usuario = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));
    	String nome = this.obterNomeDoUsuario();
    	String permissao = this.mostrarPermissoes();
		if(permissao.equals("ROLE_USER") && !nome.equals(usuario.getUsername())) {
			
			throw new UnsuportedOp("Somente Admins podem alterar informações de outros usuários ");
		}
		if(permissao.equals("ROLE_USER") && !subject.getRole().equals(permissao)) {
    		
    		throw new UnsuportedOp("Usuários não podem alterar suas permissões");
    	}
		subject.setId(Long.parseLong(id));
		subject.setPassword(passwordEncoder.encode(subject.getPassword()));
		userRepository.save(subject);
	}

}
