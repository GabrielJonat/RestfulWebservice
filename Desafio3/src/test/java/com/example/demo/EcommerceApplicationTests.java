package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.exception.ResourceNotFound;
import com.example.demo.models.Produto;
import com.example.demo.repositories.ProdutoRepository;
import com.example.demo.repositories.VendaRepository;
import com.example.demo.services.ProdutoServices;

@SpringBootTest
class EcommerceApplicationTests {

	@Mock
    private VendaRepository vendaRepository;
	
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoServices produtoService;

    public void VendaServiceTest() {
        MockitoAnnotations.initMocks(this);
    }
    

    @Test
    public void testSalvarVendaProdutoNaoEncontrado() {
        String produtoId = "5";
        when(produtoRepository.findById(Long.parseLong(produtoId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFound.class, () -> produtoService.findById(produtoId));
    }
    
}

