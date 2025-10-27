package com.generation.farmacia.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.model.Produto;
import com.generation.farmacia.util.BaseControllerTeste;
import com.generation.farmacia.util.JwtHelper;
import com.generation.farmacia.util.TestBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para o ProdutoController.
 * Realiza testes de CRUD e consultas, garantindo o correto funcionamento dos endpoints.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes - Produto Controller")
public class ProdutoControllerTest extends BaseControllerTeste {

    private static final String BASE_URL = "/produtos";
    private Categoria categoria;
    private BigDecimal preco;

    /**
     * Método executado uma única vez antes de todos os testes.
     * Cria uma categoria base para uso nos produtos.
     */
    @BeforeAll
    void prepararDados() {
        inicializarTestes(); // método herdado de BaseControllerTeste

        categoria = categoriaRepository.save(
                TestBuilder.criarCategoria(null, "Categoria 01", "Descrição 01")
        );

        preco = new BigDecimal("250.50");
    }

    /**
     * Limpa os repositórios antes de cada teste, garantindo isolamento.
     */
    @BeforeEach
    void limparDados() {
        produtoRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("01 - Deve cadastrar um novo produto")
    void deveCadastrarProduto() {
        Produto produto = TestBuilder.criarProduto(null, "Produto 01", "Descrição 01", preco, categoria);

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Produto> requisicao = JwtHelper.criarRequisicaoComToken(produto, token);
        ResponseEntity<Produto> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.POST, requisicao, Produto.class);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Produto 01", resposta.getBody().getNome());
    }

    @Test
    @Order(2)
    @DisplayName("02 - Deve atualizar um produto existente")
    void deveAtualizarProduto() {
        Produto produto = produtoRepository.save(
                TestBuilder.criarProduto(null, "Produto 02", "Descrição 02", preco, categoria)
        );

        Produto produtoAtualizado = TestBuilder.criarProduto(
                produto.getId(),
                "Produto 02 - Atualizado",
                "Descrição 02",
                preco,
                categoria
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Produto> requisicao = JwtHelper.criarRequisicaoComToken(produtoAtualizado, token);
        ResponseEntity<Produto> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.PUT, requisicao, Produto.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Produto 02 - Atualizado", resposta.getBody().getNome());
    }

    @Test
    @Order(3)
    @DisplayName("03 - Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        produtoRepository.save(TestBuilder.criarProduto(null, "Produto 03", "Descrição 03", preco, categoria));
        produtoRepository.save(TestBuilder.criarProduto(null, "Produto 04", "Descrição 04", preco, categoria));

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Produto[]> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.GET, requisicao, Produto[].class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().length >= 2);
    }

    @Test
    @Order(4)
    @DisplayName("04 - Deve buscar um produto por ID")
    void deveBuscarProdutoPorId() {
        Produto produto = produtoRepository.save(
                TestBuilder.criarProduto(null, "Produto 05", "Descrição 05", preco, categoria)
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Produto> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + produto.getId(), HttpMethod.GET, requisicao, Produto.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Produto 05", resposta.getBody().getNome());
    }

    @Test
    @Order(5)
    @DisplayName("05 - Deve buscar produtos por nome")
    void deveBuscarProdutosPorNome() {
        produtoRepository.save(
                TestBuilder.criarProduto(null, "Produto 06", "Descrição 06", preco, categoria)
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);

        String url = UriComponentsBuilder
                .fromUriString(BASE_URL + "/nome/{nome}")
                .buildAndExpand("Produto 06")
                .toUriString();

        ResponseEntity<Produto[]> resposta = testRestTemplate.exchange(
                url, HttpMethod.GET, requisicao, Produto[].class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().length > 0);
    }

    @Test
    @Order(6)
    @DisplayName("06 - Deve deletar um produto por ID")
    void deveDeletarProduto() {
        Produto produto = produtoRepository.save(
                TestBuilder.criarProduto(null, "Produto 07", "Descrição 07", preco, categoria)
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
        ResponseEntity<Void> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + produto.getId(), HttpMethod.DELETE, requisicao, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertFalse(produtoRepository.findById(produto.getId()).isPresent());
    }
}