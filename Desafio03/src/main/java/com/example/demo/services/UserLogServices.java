package com.example.demo.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.exception.AuthException;
import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.User;
import com.example.demo.models.UserLog;
import com.example.demo.repositories.UserLogRepository;

@Service
public class UserLogServices {

	 
    @Autowired
    private UserLogRepository userLogRepository;
    
    @Autowired
    private UserServices userService;

    public String obterNomeDoUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); 
        User user = (User) userDetails;
        String userInfo = user.toString();
        return userInfo.substring(userInfo.indexOf("fullName")+9,userInfo.indexOf("email")-2);
    }
    
    public String mostrarPermissoes() {
    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         UserDetails userDetails = (UserDetails) authentication.getPrincipal(); 
         User user = (User) userDetails;
         String userInfo = user.toString();
         return userInfo.substring(userInfo.indexOf("role")+5,userInfo.indexOf("createdAt")-2);
    }
    
    @CacheEvict(value = "registros", allEntries = true)
    public void registrarLog(String acao) {
        
    	UserLog log = new UserLog();
    	log.setAcao(acao);
    	log.setData(LocalDateTime.now());
    	log.setUserName(this.obterNomeDoUsuario());
    	userLogRepository.save(log);
    }
    
    @Cacheable(value = "registros")
    public List<UserLog> listarLogsDeUsuarios(){
    	
    	String permissao = this.mostrarPermissoes();
    	if(permissao.equals("ADMIN")) {
    	
    		this.registrarLog("Listagem de logs");
    		return userLogRepository.findAll();
    	}
    	throw new UnsuportedOp("Acesso autorizado somente a Admins");
    }
    
    public List<UserLog> buscarLogsDeUsuario(String username){
    	
    	String permissao = this.mostrarPermissoes();
    	if(permissao.equals("ADMIN")) {
    	List<UserLog> registros = userLogRepository.findAll();
    	List<UserLog> registroDeUsuario = registros.stream().filter(reg -> reg.getUserName().equals(username)).collect(Collectors.toList());
    	this.registrarLog("Listagem de logs de usuário");
		return registroDeUsuario;
    	}
    	throw new AuthException("Acesso autorizado somente a Admins");
    }
    
 public List<UserLog> filtrarLogsPorData(LocalDateTime startDate, LocalDateTime endDate){
        
	 String permissao = this.mostrarPermissoes();
 	if(permissao.equals("ADMIN")) {
    	if(startDate.isAfter(LocalDateTime.now())) {
    		
    		throw new UnsuportedOp("O período da filtragemdata deve estar no passado");
    	}
    	if(startDate.isAfter(endDate)) {
    		
    		throw new AuthException("A data inicial deve ser anterior a data final");
    	}
    	
    	this.registrarLog("Listagem filtrada de logs de usuário");
    	return userLogRepository.findByDataBetween(startDate, endDate);
    }
 
 	throw new AuthException("Acesso autorizado somente a Admins");
 
 }
 
}
