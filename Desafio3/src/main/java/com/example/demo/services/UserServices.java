package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFound;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;

@Service
public class UserServices {

	 
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @CacheEvict(value = "usuarios", allEntries = true)
    public User saveUser(String username, String rawPassword, String role) {
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
    	
    	User usuario = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("User not found"));
		return usuario;
    }
    
    @CacheEvict(value = "usuarios", allEntries = true)
	public void excluirUsuario(String id) {
		
		User usuario = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("User not found"));
		userRepository.delete(usuario);
		
	}
	
    @CacheEvict(value = "usuarios", allEntries = true)
	public void atualizarUsuario(String id, User subject) {
		
		subject.setId(Long.parseLong(id));
		subject.setPassword(passwordEncoder.encode(subject.getPassword()));
		userRepository.save(subject);
	}

}
