package com.example.demo.utils;
import java.util.List;

import com.example.demo.models.Produto;


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
}
