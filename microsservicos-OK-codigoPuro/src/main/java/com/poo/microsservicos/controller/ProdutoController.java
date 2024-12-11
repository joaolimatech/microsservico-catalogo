package com.poo.microsservicos.controller;

import com.poo.microsservicos.model.ProdutoModel;
import com.poo.microsservicos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "ProdutoController", description = "Gerenciamento de Produtos") // Agrupa no Swagger
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @Operation(summary = "Criar Produto")
    public ResponseEntity<ProdutoModel> criarProduto(@RequestBody ProdutoModel produto) {
        return ResponseEntity.ok(produtoService.criarProduto(produto));
    }

    @GetMapping
    @Operation(summary = "Listar Todos os Produtos")
    public List<ProdutoModel> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Produto por ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            ProdutoModel produto = produtoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto com ID " + id + " não encontrado"));
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não conseguimos achar o produto com o código " + id + " :(");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Produto")
    public ResponseEntity<ProdutoModel> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoModel produto) {
        return ResponseEntity.ok(produtoService.atualizarProduto(id, produto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Produto")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    // Método para realizar a venda de um produto
    @PostMapping("/{id}/venda")
    @Operation(summary = "Vender Produto")
    public ResponseEntity<?> venderProduto(@PathVariable Long id, @RequestParam int quantidadeVendida) {
        try {
            // Tenta realizar a venda e calcular o valor total
            BigDecimal valorTotal = produtoService.venderProdutoComCalculo(id, quantidadeVendida);

            // Recupera o produto para obter a quantidade atual no estoque
            ProdutoModel produtoAtualizado = produtoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Mensagem de resposta
            String mensagem;
            if (quantidadeVendida >= 5) {
                mensagem = String.format(
                        "Valor final: R$ %.2f\nDesconto de 10%% aplicado com sucesso para venda de 5 ou mais produtos.\nQuantidade no estoque agora: %d",
                        valorTotal, produtoAtualizado.getQuantidade()
                );
            } else {
                mensagem = String.format(
                        "Valor final: R$ %.2f\nQuantidade no estoque agora: %d",
                        valorTotal, produtoAtualizado.getQuantidade()
                );
            }

            return ResponseEntity.ok(mensagem);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao realizar a venda");
        }
    }


    // Método para aumentar o estoque de um produto
    @PostMapping("/{id}/estoque")
    @Operation(summary = "Aumentar Estoque")
    public ResponseEntity<?> aumentarEstoque(@PathVariable Long id, @RequestParam int quantidadeAdicionada) {
        try {
            ProdutoModel produtoAtualizado = produtoService.aumentarEstoque(id, quantidadeAdicionada);
            return ResponseEntity.ok("O estoque do produto " + produtoAtualizado.getNome() +
                    " agora está em " + produtoAtualizado.getQuantidade());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível adicionar ao estoque.");
        }
    }
}
