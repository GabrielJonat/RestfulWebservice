package com.example.demo.controllers;

import static com.example.demo.utils.CustomizedValidation.validUsuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.User;
import com.example.demo.services.UserServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserServices userService;

    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody User usuario) throws Exception{
        
    	
    	List<User> usuarios = userService.listarUsuarios();
    	if(validUsuario(usuarios, usuario)) {
    	userService.saveUser(usuario.getUsername(), usuario.getPassword(), usuario.getRole());
    	return ResponseEntity.created(null).build();
    	}
        throw new UnsuportedOp("Usuário inválido");
    }

    @GetMapping
	@Operation(summary = "Listar todos os Usuarios", description = "Finds all Products",
	tags = {"User"},
	responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
					mediaType= "application/json")}),
			@ApiResponse(description = "Bad Request", responseCode = "400",content =  @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401",content =  @Content),
			@ApiResponse(description = "Not Found", responseCode = "404",content =  @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500",content =  @Content)
	})
	public List<User> listarProdutos(){
		
		return userService.listarUsuarios();
		
	}

    @GetMapping("/{id}")
    public User buscarUsuarioPorId(@PathVariable (value = "id") String id) {
    	
    	return userService.buscarUsuario(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable(value = "id") String id, @RequestBody User usuario) {
        
    	List<User> usuarios = userService.listarUsuarios();
    	if(validUsuario(usuarios, usuario)) {
    	userService.atualizarUsuario(id, usuario);
    	return ResponseEntity.ok().build();
    	}
    	throw new UnsuportedOp("Usuário inválido");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUsuario(@PathVariable(value = "id") String id) {
       
    	userService.excluirUsuario(id);
        return ResponseEntity.noContent().build();
    }

}