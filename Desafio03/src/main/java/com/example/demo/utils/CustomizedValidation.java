package com.example.demo.utils;
import java.util.List;

import com.example.demo.models.Produto;
import com.example.demo.models.User;


public class CustomizedValidation {
	

	

	public static boolean validProduct(Produto produto){
		
		 if(produto.getPreco() > 0 && produto.getEstoque() > 0) {
		    	return true;
		    	}
		
		return false;
		
		
	}
	
	public static boolean validVenda(List<Long> produtos){
		
		 if(produtos.size() == 0 ) {
		    	return false;
		    	}
		 
		
		return true;
		
		
	}
	
	public static boolean validUsuario(List<User> usuarios, User usuario){
		
		if(usuario.getFullName().isBlank() || usuario.getPassword().isBlank() || usuario.getFullName().length() > 40 || usuario.getPassword().length() > 40 || !(usuario.getRole().equals("ROLE_USER") || usuario.getRole().equals("ROLE_ADMIN"))){
			
			return false;
		}
		for(User usuarioListado : usuarios) {
			
			if(usuarioListado == usuario)
				
				return false;
		}
		
		return true;
		
		
	}
}
