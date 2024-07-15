package com.example.demo.controllers;

import static com.example.demo.utils.CustomizedValidation.validProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.Produto;
import com.example.demo.services.ProdutoServices;
import com.example.demo.services.UserLogServices;
import com.example.demo.services.UserServices;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoServices produtoService;

    @Autowired
    private UserLogServices logService;
    
    @Autowired UserServices userService;
    
    @PostMapping
    public ResponseEntity<?> criarProduto(@RequestBody Produto produto) throws Exception{
        if(validProduct(produto) && userService.mostrarPermissoes().equals("ADMIN")) {
    	produtoService.save(produto);
    	logService.registrarLog("Cadastro de produto");
    	return ResponseEntity.created(null).build();
    	}
        throw new UnsuportedOp("O preço do produto, assim como o estoque devem ser positivos e deve-se ter permissão de administrador para cadastrar um produto");
    }

    @GetMapping
	@Operation(summary = "Listar todos os Produtos", description = "Finds all Products",
	tags = {"Product"},
	responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = {@Content(
					mediaType= "application/json")}),
			@ApiResponse(description = "Bad Request", responseCode = "400",content =  @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401",content =  @Content),
			@ApiResponse(description = "Not Found", responseCode = "404",content =  @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500",content =  @Content)
	})
	public List<Produto> listarProdutos(){
		
    	logService.registrarLog("Listagem de produto");
		return produtoService.findAll();
		
	}

    @GetMapping("/{id}")
    public Produto buscarProdutoPorId(@PathVariable (value = "id") String id) {
    	
    	logService.registrarLog("Pesquisa por produto");
    	return produtoService.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(@PathVariable(value = "id") String id, @RequestBody Produto produto) {
        
    	if(validProduct(produto) && userService.mostrarPermissoes().equals("ADMIN")) {
    	produtoService.update(id, produto);
    	logService.registrarLog("Atualização de informações de produto");
    	return ResponseEntity.ok().build();
    	}
    	throw new UnsuportedOp("O preço do produto, assim como o estoque devem ser positivos e deve-se ter permissão de administrador para atualizar um produto");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProduto(@PathVariable(value = "id") String id) {
       
    	if(userService.mostrarPermissoes().equals("ADMIN")) {
    	produtoService.delete(id);
    	logService.registrarLog("Exclusão de produto");
        return ResponseEntity.noContent().build();
    	}
    	throw new UnsuportedOp("Deve-se ter permissão de administrador para deletar um produto");
    }

    @PatchMapping("/inativar/{id}")
    public ResponseEntity<?> inativarProduto(@PathVariable (value = "id") String id) {
       
    	if(userService.mostrarPermissoes().equals("ADMIN")) {
    		produtoService.unactivate(id);
            logService.registrarLog("Inativação de produto");
            return ResponseEntity.ok().build();
        	}
        	throw new UnsuportedOp("Deve-se ter permissão de administrador para deletar um produto");
    }
}