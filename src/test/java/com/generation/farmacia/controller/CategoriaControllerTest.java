package com.generation.farmacia.controller;

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

import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.util.BaseControllerTeste;
import com.generation.farmacia.util.JwtHelper;
import com.generation.farmacia.util.TestBuilder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de testes para o CategoriaController.
 * Testa os endpoints principais de CRUD e busca de categorias.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes - Categoria Controller")
public class CategoriaControllerTest extends BaseControllerTeste {

    private static final String BASE_URL = "/categorias";

    @BeforeAll
    void prepararAmbiente() {
        inicializarTestes(); // método da BaseControllerTeste
    }

    @BeforeEach
    void limparBanco() {
        categoriaRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("01 - Deve cadastrar uma nova categoria")
    void deveCadastrarCategoria() {
        Categoria categoria = TestBuilder.criarCategoria(null, "Higiene Pessoal", "Produtos para cuidados pessoais e limpeza do corpo.");

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Categoria> requisicao = JwtHelper.criarRequisicaoComToken(categoria, token);

        ResponseEntity<Categoria> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.POST, requisicao, Categoria.class);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Higiene Pessoal", resposta.getBody().getNome());
    }

    @Test
    @Order(2)
    @DisplayName("02 - Deve atualizar uma categoria existente")
    void deveAtualizarCategoria() {
        Categoria categoria = categoriaRepository.save(
                TestBuilder.criarCategoria(null, "Medicamentos", "Categoria de remédios e similares.")
        );

        Categoria categoriaAtualizada = TestBuilder.criarCategoria(
                categoria.getId(), "Medicamentos e Suplementos", "Categoria de remédios, vitaminas e suplementos alimentares."
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Categoria> requisicao = JwtHelper.criarRequisicaoComToken(categoriaAtualizada, token);

        ResponseEntity<Categoria> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.PUT, requisicao, Categoria.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Medicamentos e Suplementos", resposta.getBody().getNome());
    }

    @Test
    @Order(3)
    @DisplayName("03 - Deve listar todas as categorias")
    void deveListarTodasCategorias() {
        categoriaRepository.save(TestBuilder.criarCategoria(null, "Beleza", "Produtos cosméticos e cuidados com a pele."));
        categoriaRepository.save(TestBuilder.criarCategoria(null, "Infantil", "Produtos voltados para o público infantil."));

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);

        ResponseEntity<Categoria[]> resposta = testRestTemplate.exchange(
                BASE_URL, HttpMethod.GET, requisicao, Categoria[].class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().length >= 2);
    }

    @Test
    @Order(4)
    @DisplayName("04 - Deve buscar uma categoria por ID")
    void deveBuscarCategoriaPorId() {
        Categoria categoria = categoriaRepository.save(
                TestBuilder.criarCategoria(null, "Dermocosméticos", "Produtos para tratamento e beleza da pele.")
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);

        ResponseEntity<Categoria> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + categoria.getId(), HttpMethod.GET, requisicao, Categoria.class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Dermocosméticos", resposta.getBody().getNome());
    }

    @Test
    @Order(5)
    @DisplayName("05 - Deve buscar categorias por nome")
    void deveBuscarCategoriaPorNome() {
        categoriaRepository.save(
                TestBuilder.criarCategoria(null, "Saúde Bucal", "Produtos para higiene e cuidados com os dentes.")
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);

        ResponseEntity<Categoria[]> resposta = testRestTemplate.exchange(
                BASE_URL + "/nome/Saúde Bucal", HttpMethod.GET, requisicao, Categoria[].class);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertTrue(resposta.getBody().length > 0);
    }

    @Test
    @Order(6)
    @DisplayName("06 - Deve deletar uma categoria por ID")
    void deveDeletarCategoria() {
        Categoria categoria = categoriaRepository.save(
                TestBuilder.criarCategoria(null, "Cuidados com os Cabelos", "Produtos capilares e de tratamento.")
        );

        String token = JwtHelper.obterToken(testRestTemplate, ADMIN, SENHA);
        HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);

        ResponseEntity<Void> resposta = testRestTemplate.exchange(
                BASE_URL + "/" + categoria.getId(), HttpMethod.DELETE, requisicao, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertFalse(categoriaRepository.findById(categoria.getId()).isPresent());
    }
}
