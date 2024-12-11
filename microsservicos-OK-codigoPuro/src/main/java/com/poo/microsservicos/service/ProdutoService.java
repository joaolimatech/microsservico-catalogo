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

    @Autowired // @Autowired instância do repositoiy gerenciada pelo Spring.
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
        ProdutoModel produto = produtoRepository.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado")); //vai na model, na tabeela e ve se o produto esta la ou nao foi encontrado

        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setQuantidade(produtoAtualizado.getQuantidade());
        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        produtoRepository.deleteById(id);
    }

    // reailza venda do produto com o calculo total. Tbm mostra caso nn encontre
    public BigDecimal venderProdutoComCalculo(Long id, int quantidadeVendida) {
        ProdutoModel produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        if (produto.getQuantidade() < quantidadeVendida) {
            throw new RuntimeException("Quantidade em estoque insuficiente :(");
        }

        // ataulzia o estoque
        produto.setQuantidade(produto.getQuantidade() - quantidadeVendida);
        produtoRepository.save(produto);

        // calc o valor total da venda
        //bigDecimal é um float para numeros bem grandes, geralmente dizem q é pra dinheiro
        BigDecimal valorUnitario = produto.getPreco();
        BigDecimal quantidade = BigDecimal.valueOf(quantidadeVendida);
        BigDecimal valorTotal = valorUnitario.multiply(quantidade);

        // apica o desconto de 10% se a vender +de4 produtos do msm ID
        if (quantidadeVendida >= 5) {
            BigDecimal desconto = valorTotal.multiply(BigDecimal.valueOf(0.10));
            valorTotal = valorTotal.subtract(desconto);
        }

        return valorTotal;
    }

    // aumetna qtde do produto especifico no estoque
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
