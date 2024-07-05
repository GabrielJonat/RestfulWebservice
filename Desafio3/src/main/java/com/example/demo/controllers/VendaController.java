package com.example.demo.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.VendaRequest;
import com.example.demo.models.Venda;
import com.example.demo.services.UserLogServices;
import com.example.demo.services.VendaServices;

@RestController
@RequestMapping("/vendas")
public class VendaController {

	@Autowired
    private VendaServices vendaService;

	@Autowired
	private UserLogServices logService;
	
    @PostMapping
    public ResponseEntity<?> criarVenda(@RequestBody VendaRequest vendaRequest) {
    	
    	vendaService.salvarVenda(vendaRequest.getProdutos());
    	logService.registrarLog("Cadastro de venda");
    	return ResponseEntity.created(null).build();
    }

    @GetMapping
    public List<Venda> listarVendas() {
    	
    	logService.registrarLog("Listagem de vendas");
    	return vendaService.listarVendas();
    }

    @GetMapping("/{id}")
    public Venda buscarVendaPorId(@PathVariable Long id) {
    	
    	logService.registrarLog("Pesquisa por venda");
        return vendaService.buscarVendaPorId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        logService.registrarLog("Exclusão de venda");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtrar")
    public List<Venda> filtrarVendasPorData(
            @RequestParam("DataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("DataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    	logService.registrarLog("Filtragem de vendas");
        return vendaService.filtrarVendasPorData(startDate, endDate);
    }

    @GetMapping("/semana")
    public List<Venda> gerarRelatorioSemanal() {
    	logService.registrarLog("Geração de relatório semanal");
        return vendaService.relatorioSemanal();
    }
    
    @GetMapping("/mes")
    public List<Venda> gerarRelatorioMensal() {
    	logService.registrarLog("Geração de relatório mensal");
    	return vendaService.relatorioMensal();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarVenda(@RequestBody VendaRequest vendaRequest, @PathVariable String id) {
    	
    	vendaService.atualizarVenda(vendaRequest.getProdutos(), id);
    	logService.registrarLog("Atualização de venda");
    	return ResponseEntity.ok().build();
    }
    
}