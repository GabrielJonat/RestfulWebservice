package com.example.demo;

import com.example.demo.dto.VendaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String authenticateAndGetToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String loginJson = "{\"email\": \"user1@outlook.com\", \"password\": \"password1\"}";
        HttpEntity<String> loginRequest = new HttpEntity<>(loginJson, headers);
        
        ResponseEntity<Map> loginResponse = restTemplate.exchange("/auth/login", HttpMethod.POST, loginRequest, Map.class);
        
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // Extrair o token da resposta
        String token = (String) loginResponse.getBody().get("token");
        
        return token;
    }

    @Test
    public void testCriarVenda() {
        String token = authenticateAndGetToken();

        VendaRequest vendaRequest = new VendaRequest();
        vendaRequest.setProdutos(Collections.singletonList(1L));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        
        HttpEntity<VendaRequest> request = new HttpEntity<>(vendaRequest, headers);
        
        ResponseEntity<String> response = restTemplate.exchange("/vendas", HttpMethod.POST, request, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }
}
