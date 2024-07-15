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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); 
        User user = (User) userDetails;
        String userInfo = user.toString();
        return userInfo.substring(userInfo.indexOf("role")+5,userInfo.indexOf("createdAt")-2);
   }
    
    @CacheEvict(value = "usuarios", allEntries = true)
    public User saveUser(String username, String rawPassword, String role) {
        
    	String permissao = this.mostrarPermissoes();
    	if( permissao.equals("ROLE_USER") && !role.equals(permissao)) {
    		
    		throw new UnsuportedOp("Usuários não podem cadastrar administradores sua permissao é de " + permissao);
    	}
    	List<User> usuarios = this.listarUsuarios();
    	for(User usuario : usuarios) {
    		
    		if(usuario.getFullName().equals(username)) {
    			
    			throw new UnsuportedOp("Usuário já cadastrado");
    		}
    	}
    	User user = new User();
        user.setFullName(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        return userRepository.save(user);
    }
    
    @Cacheable(value = "usuarios")
    public List<User> listarUsuarios(){
    	
    	return (List<User>) userRepository.findAll();
    }
    
    public User buscarUsuario(String email){
    	
    	User usuario = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));
		return usuario;
    }
    
    @CacheEvict(value = "usuarios", allEntries = true)
	public void excluirUsuario(String email) {
		
    	String nome = this.obterNomeDoUsuario();
    	String permissao = this.mostrarPermissoes();
		User usuario = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("Usuário não encontrado"));
		if(permissao.equals("ROLE_USER") && !nome.equals(usuario.getFullName())) {
			
			throw new UnsuportedOp("Somente Admins podem deletar outros usuários");
		}
		userRepository.delete(usuario);
		
	}
	
    @CacheEvict(value = "usuarios", allEntries = true)
	public void atualizarEmail(User user, String newEmail) {
		
    	user.setEmail(newEmail);
    	userRepository.save(user);
	}
    
    @CacheEvict(value = "usuarios", allEntries = true)
   	public void atualizarSenha(User user, String newPassword) {
   		
       	user.setPassword(newPassword);
       	userRepository.save(user);
   	}
    
    @CacheEvict(value = "usuarios", allEntries = true)
   	public void concederPermissao(User user) {
   		
    	String permissao = this.mostrarPermissoes();
    	if(!permissao.equals("ADMIN")) {
			
			throw new UnsuportedOp("Somente Admins podem conceder permissões a outros usuários sua permissão é de "+ permissao);
		}
       	user.setRole("ADMIN");
       	userRepository.save(user);
   	}

}
