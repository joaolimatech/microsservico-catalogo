package com.poo.microsservicos.controller;


import com.poo.microsservicos.model.ProdutoModel; // Modelo de dados do Produto
import com.poo.microsservicos.service.ProdutoService; // servico com regras de negócio
import io.swagger.v3.oas.annotations.Operation; // Para documentar métodos no Swagger
import io.swagger.v3.oas.annotations.tags.Tag; // Para agrupar endpoints no Swagger
import org.springframework.beans.factory.annotation.Autowired; // Injeta dependências no código
import org.springframework.http.HttpStatus; // Define status HTTP das respostas
import org.springframework.http.ResponseEntity; // Monta as respostas HTTP
import org.springframework.web.bind.annotation.*; // Habilita as anotações REST
import java.math.BigDecimal;
import java.util.List;

@RestController // Fala pro Spring que isso aqui é um controller REST
@RequestMapping("/api/produtos") // Define a URL base para acessar nossos endpoints
@Tag(name = "ProdutoController", description = "Gerenciamento de CATALOGO")
public class ProdutoController {

    @Autowired // Injeta o ProdutoService para ser usado aqui
    private ProdutoService produtoService;

    @PostMapping // Endpoint que cria um produto
    @Operation(summary = "Criar Produto") // Documentação do Swagger
    public ResponseEntity<ProdutoModel> criarProduto(@RequestBody ProdutoModel produto) {
        // Chama o método de servico que cria o produto e retorna o resultado da resp
        return ResponseEntity.ok(produtoService.criarProduto(produto));
    }

    @GetMapping // Endpoint que lista todos os produtos
    @Operation(summary = "Listar Todos os Produtos")
    public List<ProdutoModel> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}") // busca um produto  pelo ID
    @Operation(summary = "Buscar Produto por ID")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) { // O @PathVariable quer dize que o id será capturadoDIRETO da URL.
        try {
            // Busca o produto pelo ID e retorna se encontrar
            ProdutoModel produto = produtoService.buscarPorId(id).orElseThrow(() -> new RuntimeException("Produto com ID " + id + " não encontrado"));
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            // Retorna um erro se o produto não for encontrado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não conseguimos achar o produto com o código " + id + " :(");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Produto")
    public ResponseEntity<ProdutoModel> atualizarProduto(@PathVariable Long id, @RequestBody ProdutoModel produto) { // O @RequestBody: o objeto "produto" será enviado no corpo da requisição.
        // Chama o servico para atualizar o produto e retorna o produto atualizado
        return ResponseEntity.ok(produtoService.atualizarProduto(id, produto));
    }

    @DeleteMapping("/{id}") // Endpoint que deleta um produto pelo ID
    @Operation(summary = "Excluir Produto") // Documentação no Swagger
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        // chama  para excluir o produto
        produtoService.deletarProduto(id);
        // retorna uma resposta sem conteúdo
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/venda") // Endpoint que realiza uma venda de produto
    @Operation(summary = "Vender Produto") // Documentação no Swagger
    public ResponseEntity<?> venderProduto(@PathVariable Long id, @RequestParam int quantidadeVendida) {
        try {
            // chama o servico para calcular o valor total da venda
            BigDecimal valorTotal = produtoService.venderProdutoComCalculo(id, quantidadeVendida);

            // busca o produto atualizado e  mostra o estoque atual
            ProdutoModel produtoAtualizado = produtoService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // monta a msg  com base na quantidade vendida
            String msg;
            if (quantidadeVendida >= 5) { // desconto se for 5 ou mais coisas vendidas
                msg = String.format(
                        "Valor final: R$ %.2f\nDesconto de 10%% aplicado com sucesso para venda de 5 ou mais produtos.\nQuantidade no estoque agora: %d",
                        valorTotal, produtoAtualizado.getQuantidade()
                );
            } else {
                msg = String.format(
                        "Valor final: R$ %.2f\nQuantidade no estoque agora: %d",
                        valorTotal, produtoAtualizado.getQuantidade()
                );
            }

            // Retorna a msg com o status OK
            return ResponseEntity.ok(msg);

        } catch (RuntimeException e) {
            // Retorna erro se algo der errado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao realizar a venda");
        }
    }

    @PostMapping("/{id}/estoque") // Endpoint que aumenta o estoque de um produto
    @Operation(summary = "Aumentar Estoque") // Documentação no Swagger
    public ResponseEntity<?> aumentarEstoque(@PathVariable Long id, @RequestParam int quantidadeAdicionada) {
        try {
            // Chama o servico para adicionar quantidade ao estoque
            ProdutoModel produtoAtualizado = produtoService.aumentarEstoque(id, quantidadeAdicionada);
            // Retorna uma msg informando o estoque atualizado
            return ResponseEntity.ok("O estoque do produto " + produtoAtualizado.getNome() +
                    " agora está em " + produtoAtualizado.getQuantidade());
        } catch (RuntimeException e) {
            // Retorna erro se não conseguir adc ao estoque
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Não foi possível adicionar ao estoque.");
        }
    }
}
