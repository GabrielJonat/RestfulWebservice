package com.example.demo.services;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFound;
import com.example.demo.exception.UnsuportedOp;
//import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.Produto;
import com.example.demo.models.Venda;
//import com.example.demo.models.ProdutoVenda;
import com.example.demo.repositories.ProdutoRepository;

@Service
public class ProdutoServices {
	
	private Logger logger = Logger.getLogger(ProdutoServices.class.getName());
	
	@Autowired
	private ProdutoRepository repo;
	
	@Autowired
	private VendaServices serviceVendas;

	public Produto findById(String id) {
		
		logger.info("Finding one product!");
	
		Produto produto = repo.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("Product not found"));
		return produto;
		
	}
	
	 @Cacheable(value = "produtos")
	public List<Produto> findAll() {
		
		logger.info("Finding all products!");
	
		List<Produto> produtos = repo.findAll();
		
		return produtos;
		
	}
	
	@CacheEvict(value = "produtos", allEntries = true)
	public void save(Produto produto) {
		
		logger.info("Saving one product!");

		List<Produto> subject = repo.findAll().stream().filter(prod -> prod.getNome().equals(produto.getNome())).collect(Collectors.toList());
		if(!subject.isEmpty()) {
			
			throw new UnsuportedOp("Produto já cadastrado");
		}
		repo.save(produto);
		
	}
	
	 @CacheEvict(value = "produtos", allEntries = true)
	public void delete(String id) {
		
		Produto produto = repo.findById(Long.parseLong(id)).orElseThrow(() -> new ResourceNotFound("Product not found"));
		List<Venda> vendas = serviceVendas.listarVendas();
		for(Venda venda : vendas) {
			
			if(venda.getProdutos().contains(produto)) {
				
				throw new UnsuportedOp("Produto já inserido na venda, desative-o ao invés disto");
			}
		}
		logger.info("Deleting one product!");
		repo.delete(produto);
		
	}
	
	@CacheEvict(value = "produtos", allEntries = true)
	public void update(String id, Produto subject) {
		
		subject.setId(Long.parseLong(id));
		logger.info("Updating one product!");
		repo.save(subject);
	
	}
	
	@CacheEvict(value = "produtos", allEntries = true)
	public void unactivate(String id) {
		
		Produto subject = this.findById(id);
		subject.setAtivo(false);
		logger.info("Unactivating one product!");
		repo.save(subject);
	}
	
}
