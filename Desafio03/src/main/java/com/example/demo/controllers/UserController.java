package com.example.demo.controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChangePasswordDto;
import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.CodigosDeSeguranca;
import com.example.demo.models.User;
import com.example.demo.models.UserLog;
import com.example.demo.services.EmailService;
import com.example.demo.services.TokenServices;
import com.example.demo.services.UserLogServices;
import com.example.demo.services.UserServices;


@RestController
@RequestMapping("/usuarios")
public class UserController {

	private final UserServices userService;
	
	@Autowired
	private UserLogServices logService;

    public UserController(UserServices userService) {
        this.userService = userService;
    }
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TokenServices tokenService;
    
    @Autowired
    BCryptPasswordEncoder encoder;

    @GetMapping("/eu")
    public ResponseEntity<User> authenticatedUser() {
    	
    	logService.registrarLog("Obtenção de informações do usuário logado");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> allUsers() {
    	
    	logService.registrarLog("Listagem de usuários");
        List <User> users = userService.listarUsuarios();

        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{email}")
    public User findUserByEmail(@PathVariable (value = "email") String email) {
    	
    	logService.registrarLog("Recuperando informações de usuário de email "+ email);
        User user = userService.buscarUsuario(email);

        return user;
    }
    
    @PutMapping("/admin/{email}")
    public ResponseEntity<?> giveAdminPermission(@PathVariable (value = "email") String email) {
    	
    	User user = userService.buscarUsuario(email);
    	userService.concederPermissao(user);
    	logService.registrarLog("Alterando permissão de usuário "+ user.getFullName());
    	return ResponseEntity.ok().build();
    }
    
    @PutMapping("/alterarEmail/{newEmail}")
    public ResponseEntity<?> ChangeUserEmail(@PathVariable (value = "newEmail") String newEmail) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

    	logService.registrarLog("Alterando email de usuário "+ user.getFullName());
        userService.atualizarEmail(user,newEmail);
    	return ResponseEntity.ok().build();
    }
    
    @GetMapping("/log")
    public List<UserLog> relatorioDeRegistroDeUsuarios() {
    	
    	return logService.listarLogsDeUsuarios();
    }

    @GetMapping("/log/{userName}")
    public List<UserLog> relatorioDeRegistroDeUsuario(@PathVariable(value = "userName") String userName){
    	
    	return logService.buscarLogsDeUsuario(userName);
    }
    
    @GetMapping("/log/filtrar")
    public List<UserLog> FiltrarDeRegistroDeUsuarios(
    		@RequestParam("DataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("DataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate){
    	
    	return logService.filtrarLogsPorData(startDate, endDate);
    	
    }
    
	    @GetMapping("/mudar-senha")
	    public ResponseEntity<?> requisitarTrocaDeSenha() {
	    	
	    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	         User currentUser = (User) authentication.getPrincipal();
	         
	         String originalString = currentUser.getEmail() + currentUser.getPassword();
 	        MessageDigest digest;
				try {
					digest = MessageDigest.getInstance("SHA-256");
					byte[] encodedHash = digest.digest(originalString.getBytes(StandardCharsets.UTF_8));

					StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
	    	        for (int i = 0; i < encodedHash.length; i++) {
	    	            String hex = Integer.toHexString(0xff & encodedHash[i]);
	    	            if (hex.length() == 1) {
	    	                hexString.append('0');
	    	            }
	    	            hexString.append(hex);
	    	        }
	    	        String hashedValue = hexString.toString();
	    	        tokenService.saveToken(new CodigosDeSeguranca(currentUser.getEmail(),hashedValue,true));
	    	        emailService.sendSimpleMessage(currentUser.getEmail(),"Código para Troca de Senha", hashedValue);
	    	        logService.registrarLog("Requisição de mudança de senha");
	    	        return ResponseEntity.ok("Código de segurança enviado ao seu endereço de Email");
				} catch (NoSuchAlgorithmException e) {
					throw new UnsuportedOp("Erro inesperado");
				}
	}
	    
	    @PostMapping("/mudar-senha")
	    public ResponseEntity<?> trocarSenha(@RequestBody ChangePasswordDto passwordDto) {
	    	
	    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	         User currentUser = (User) authentication.getPrincipal();
	         
	         CodigosDeSeguranca token = tokenService.findByCode(passwordDto.getToken());
	         
	         if(token == null) {
	        	 
	        	 throw new UnsuportedOp("Código inválido");
	         }
	         if(currentUser.getEmail().equals(token.getUserEmail()) && token.getValid()) {
	         
	        	 
	        	 userService.atualizarSenha(currentUser, encoder.encode(passwordDto.getNewPassword()));
	        	 tokenService.updateToken(token);
	        	 logService.registrarLog("alteração de senha");
	        	 return ResponseEntity.ok().build();
	         }
	         throw new UnsuportedOp("Código inativo ou pertecente a outro usuário");
	}
}