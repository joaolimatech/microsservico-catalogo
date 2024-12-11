package com.poo.microsservicos.controller;

import com.poo.microsservicos.model.ProdutoModel;
import com.poo.microsservicos.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoControllerTest {

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    private ProdutoModel produto;

    @BeforeEach
    public void setUp() {
        produto = new ProdutoModel();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(BigDecimal.valueOf(100.00));
        produto.setQuantidade(10);
    }

    @Test
    public void testCriarProduto() {
        when(produtoService.criarProduto(produto)).thenReturn(produto);

        ResponseEntity<ProdutoModel> response = produtoController.criarProduto(produto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(produto, response.getBody());
    }

    @Test
    public void testListarTodos() {
        List<ProdutoModel> produtos = Arrays.asList(produto);
        when(produtoService.listarTodos()).thenReturn(produtos);

        List<ProdutoModel> produtosRetornados = produtoController.listarTodos();

        assertFalse(produtosRetornados.isEmpty());
        assertEquals(1, produtosRetornados.size());
    }

    @Test
    public void testBuscarPorIdSucesso() {
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.of(produto));

        ResponseEntity<?> response = produtoController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ProdutoModel);
        assertEquals(produto, response.getBody());
    }

    @Test
    public void testBuscarPorIdFalha() {
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = produtoController.buscarPorId(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
    }

    @Test
    public void testVenderProduto() {
        when(produtoService.venderProdutoComCalculo(1L, 5))
                .thenReturn(BigDecimal.valueOf(450.00));
        when(produtoService.buscarPorId(1L)).thenReturn(Optional.of(produto));

        ResponseEntity<?> response = produtoController.venderProduto(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        assertTrue(((String)response.getBody()).contains("Valor final: R$ 450,00"));
    }

    @Test
    public void testAumentarEstoque() {
        when(produtoService.aumentarEstoque(1L, 5)).thenReturn(produto);
        produto.setQuantidade(15);

        ResponseEntity<?> response = produtoController.aumentarEstoque(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof String);
        assertTrue(((String)response.getBody()).contains("agora está em 15"));
    }
}