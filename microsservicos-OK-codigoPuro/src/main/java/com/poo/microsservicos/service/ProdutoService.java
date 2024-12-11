package com.poo.microsservicos.service;

import com.poo.microsservicos.model.ProdutoModel;
import com.poo.microsservicos.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public ProdutoModel criarProduto(ProdutoModel produto) {
        return produtoRepository.save(produto);
    }

    public List<ProdutoModel> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<ProdutoModel> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public ProdutoModel atualizarProduto(Long id, ProdutoModel produtoAtualizado) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setQuantidade(produtoAtualizado.getQuantidade());
        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    // Método para realizar a venda de um produto com cálculo de valor total
    public BigDecimal venderProdutoComCalculo(Long id, int quantidadeVendida) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidade() < quantidadeVendida) {
            throw new RuntimeException("Quantidade em estoque insuficiente.");
        }

        // Atualiza o estoque
        produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);
        produtoRepository.save(produto);

        // Calcula o valor total da venda
        BigDecimal valorUnitario = produto.getPreco();
        BigDecimal quantidade = BigDecimal.valueOf(quantidadeVendida);
        BigDecimal valorTotal = valorUnitario.multiply(quantidade);

        // Aplica o desconto de 10% se a quantidade for 5 ou mais
        if (quantidadeVendida >= 5) {
            BigDecimal desconto = valorTotal.multiply(BigDecimal.valueOf(0.10)); // 10% de desconto
            valorTotal = valorTotal.subtract(desconto);
        }

        return valorTotal;
    }

    // Método para aumentar o estoque de um produto
    public ProdutoModel aumentarEstoque(Long id, int quantidadeAdicionada) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (quantidadeAdicionada <= 0) {
            throw new RuntimeException("Adicione 1 ou mais produtos ao estoque.");
        }

        produto.setQuantidade(produto.getQuantidade() + quantidadeAdicionada);
        return produtoRepository.save(produto);
    }
}
