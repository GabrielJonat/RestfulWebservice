package com.example.demo.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import com.example.demo.models.CodigosDeSeguranca;
import com.example.demo.repositories.CodSegurancaRepository;

@Service
public class TokenServices {

	 
    @Autowired
    private CodSegurancaRepository codRepository;

   
    
    @CacheEvict(value = "tokens", allEntries = true)
    public void saveToken(CodigosDeSeguranca token) {
        
        codRepository.save(token);
    }
    
    @CacheEvict(value = "tokens", allEntries = true)
    public void updateToken(CodigosDeSeguranca token) {
        
    	token.setValid(false);
        codRepository.save(token);
    }
    
    
    public CodigosDeSeguranca findByCode(String code){
    	
    	CodigosDeSeguranca token = codRepository.findByCode(code);
    	return token;
    }
    
}
