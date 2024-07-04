package com.example.demo;

import com.example.demo.dto.VendaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VendaControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        // Registrar usuário para o teste
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin");
    }

    @Test
    public void testCriarVendaAutenticado() {
        VendaRequest vendaRequest = new VendaRequest();
        vendaRequest.setProdutos(Collections.singletonList(3L));

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user1", "password1");

        HttpEntity<VendaRequest> request = new HttpEntity<>(vendaRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("/vendas", HttpMethod.POST, request, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }
    
    @Test
    public void testCriarVendaNaoAutenticado() {
        VendaRequest vendaRequest = new VendaRequest();
        vendaRequest.setProdutos(Collections.singletonList(3L));

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("", "");
        HttpEntity<VendaRequest> request = new HttpEntity<>(vendaRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange("/vendas", HttpMethod.POST, request, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }
}
