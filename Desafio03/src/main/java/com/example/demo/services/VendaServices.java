package com.example.demo.services;

import static com.example.demo.utils.CustomizedValidation.validVenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.ResourceNotFound;
import com.example.demo.exception.UnsuportedOp;
import com.example.demo.models.Produto;
import com.example.demo.models.User;
import com.example.demo.models.Venda;
import com.example.demo.repositories.ProdutoRepository;
import com.example.demo.repositories.VendaRepository;

@Service
public class VendaServices {
	
	@Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private UserServices userService;

    public Venda salvarVenda(List<Long> produtoIds, User realizador) {
    	 
    	if(!validVenda(produtoIds)) {
       	 
       	 throw new UnsuportedOp("É necessário incluir ao menos um produto para efetuar a venda");
        }
    	List<Produto> produtos = produtoIds.stream()
                 .map(id -> produtoRepository.findById(id)
                         .orElseThrow(() -> new ResourceNotFound("Produto não encontrado: " + id)))
                 .collect(Collectors.toList());

         for (Produto produto : produtos) {
             if (produto.getEstoque() < 1) {
                 throw new UnsuportedOp("Estoque insuficiente para o produto: " + produto.getNome());
             }
             
             if(produto.getAtivo() == false) {
            	 throw new UnsuportedOp("produto "+ produto.getNome() +" se encontra inativo");
             }
             
             produto.setEstoque(produto.getEstoque() - 1);
             produtoRepository.save(produto);
         }

         Venda venda = new Venda();
         venda.setData(LocalDateTime.now());
         venda.setProdutos(produtos);
         venda.setVendedor(realizador.getId()+";"+realizador.getEmail()+";"+realizador.getFullName());
         return vendaRepository.save(venda);
    }

    public Venda atualizarVenda(List<Long> produtoIds, String id) {
   	 
    	if(userService.mostrarPermissoes().equals("ADMIN")) {
    	Venda venda = vendaRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ResourceNotFound("Venda não encontrada"));
        for (Produto produto : venda.getProdutos()) {
            Produto p = produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));
            p.setEstoque(p.getEstoque() + 1);
            produtoRepository.save(p);
        }
    	if(!validVenda(produtoIds)) {
       	 
       	 throw new UnsuportedOp("É necessário incluir ao menos um produto para efetuar a venda");
        }
    	List<Produto> produtos = produtoIds.stream()
                 .map(idProd -> produtoRepository.findById(idProd)
                         .orElseThrow(() -> new ResourceNotFound("Produto não encontrado: " + idProd)))
                 .collect(Collectors.toList());

         for (Produto produto : produtos) {
             if (produto.getEstoque() < 1) {
                 throw new UnsuportedOp("Estoque insuficiente para o produto: " + produto.getNome());
             }
             
             if(produto.getAtivo() == false) {
            	 throw new UnsuportedOp("produto "+ produto.getNome() +" se encontra inativo");
             }
             
             produto.setEstoque(produto.getEstoque() - 1);
             produtoRepository.save(produto);
         }

         venda.setData(LocalDateTime.now());
         venda.setProdutos(produtos);
         return vendaRepository.save(venda);
    	}
    	throw new UnsuportedOp("Somente administradores podem atualizar vendas");
    }

    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }

    public Venda buscarVendaPorId(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Venda não encontrada"));
    }

    public void deletarVenda(Long id) {
    	
    	if(userService.mostrarPermissoes().equals("ADMIN")) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Venda não encontrada"));
        for (Produto produto : venda.getProdutos()) {
            Produto p = produtoRepository.findById(produto.getId())
                    .orElseThrow(() -> new ResourceNotFound("Produto não encontrado"));
            p.setEstoque(p.getEstoque() + 1);
            produtoRepository.save(p);
        }
        vendaRepository.delete(venda);
    	}
    	else {
    		throw new UnsuportedOp("Somente administradores podem deletar vendas sua permissao é de "+userService.mostrarPermissoes());
    	}
    	}

    public List<Venda> filtrarVendasPorData(LocalDateTime startDate, LocalDateTime endDate) {
        
    	if(startDate.isAfter(LocalDateTime.now())) {
    		
    		throw new UnsuportedOp("O período da filtragem deve estar no passado");
    	}
    	if(startDate.isAfter(endDate)) {
    		
    		throw new UnsuportedOp("A data inicial deve ser anterior a data final");
    	}
    	
    	return vendaRepository.findByDataBetween(startDate, endDate);
    }
    
    public List<Venda> relatorioSemanal() {
    	
    	LocalDateTime startDate = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    	LocalDateTime endDate = startDate.plusDays(6);
        return vendaRepository.findByDataBetween(startDate, endDate);
    }
    
    public List<Venda> relatorioMensal(){
    	
    	LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1);
    	LocalDateTime endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23,59,59);
    	return vendaRepository.findByDataBetween(startDate, endDate);
    }
}
