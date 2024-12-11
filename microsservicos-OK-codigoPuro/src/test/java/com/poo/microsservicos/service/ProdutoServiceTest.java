package com.poo.microsservicos.service;

import com.poo.microsservicos.model.ProdutoModel;
import com.poo.microsservicos.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

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
        when(produtoRepository.save(produto)).thenReturn(produto);

        ProdutoModel produtoCriado = produtoService.criarProduto(produto);

        assertNotNull(produtoCriado);
        assertEquals(produto.getNome(), produtoCriado.getNome());
        verify(produtoRepository).save(produto);
    }

    @Test
    public void testListarTodos() {
        List<ProdutoModel> produtos = Arrays.asList(produto);
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<ProdutoModel> produtosRetornados = produtoService.listarTodos();

        assertFalse(produtosRetornados.isEmpty());
        assertEquals(1, produtosRetornados.size());
        verify(produtoRepository).findAll();
    }

    @Test
    public void testBuscarPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Optional<ProdutoModel> produtoEncontrado = produtoService.buscarPorId(1L);

        assertTrue(produtoEncontrado.isPresent());
        assertEquals(produto.getId(), produtoEncontrado.get().getId());
        verify(produtoRepository).findById(1L);
    }

    @Test
    public void testVenderProdutoComCalculo() {
        // Criar um produto mock
        ProdutoModel produto = new ProdutoModel();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setDescricao("Descrição Teste");
        produto.setPreco(BigDecimal.valueOf(100.0));
        produto.setQuantidade(10);

        // Configurar o mock do repositório
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        // Executar o método
        BigDecimal valorTotal = produtoService.venderProdutoComCalculo(1L, 5);

        // Verificar o resultado
        assertEquals(BigDecimal.valueOf(450.00).setScale(2), valorTotal.setScale(2));
        assertEquals(5, produto.getQuantidade()); // Estoque atualizado
        verify(produtoRepository).save(produto);
    }


    @Test
    public void testVenderProdutoComCalculoSemDesconto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        BigDecimal valorTotal = produtoService.venderProdutoComCalculo(1L, 3);

        assertEquals(BigDecimal.valueOf(300.00), valorTotal);
        assertEquals(7, produto.getQuantidade());
        verify(produtoRepository).save(produto);
    }

    @Test
    public void testAumentarEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(produto)).thenReturn(produto);

        ProdutoModel produtoAtualizado = produtoService.aumentarEstoque(1L, 5);

        assertEquals(15, produtoAtualizado.getQuantidade());
        verify(produtoRepository).save(produto);
    }

    @Test
    public void testAumentarEstoqueQuantidadeInvalida() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(RuntimeException.class, () -> {
            produtoService.aumentarEstoque(1L, 0);
        });
    }
}