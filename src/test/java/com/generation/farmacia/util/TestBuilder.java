package com.generation.farmacia.util;

import java.math.BigDecimal;

import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.model.Produto;
import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.model.UsuarioLogin;

public class TestBuilder {
	
	public static Usuario criarUsuario(Long id, String nome, String usuario, String senha) {
		Usuario novoUsuario = new Usuario();
		novoUsuario.setId(id);
		novoUsuario.setNome(nome);
		novoUsuario.setUsuario(usuario);
		novoUsuario.setSenha(senha);
		novoUsuario.setFoto("-");
		return novoUsuario;
	}

	public static UsuarioLogin criarUsuarioLogin(String usuario, String senha) {
		UsuarioLogin usuarioLogin = new UsuarioLogin();
		usuarioLogin.setUsuario(usuario);
		usuarioLogin.setSenha(senha);
		return usuarioLogin;
	}
	
	
	public static Categoria criarCategoria(Long id, String nome, String descricao) {
		Categoria novaCategoria = new Categoria();
		novaCategoria.setId(id);
		novaCategoria.setNome(nome);
		novaCategoria.setDescricao(descricao);
		return novaCategoria;
	}
	
	public static Produto criarProduto(Long id, String nome, String descricao, BigDecimal preco, Categoria categoria) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setFoto("-");
        produto.setCategoria(categoria);
        return produto;
    }

}
