package com.algaworks.algafood.auth.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.auth.domain.Usuario;
import com.algaworks.algafood.auth.domain.UsuarioRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	//!!!!!!!!!
	//O repositorio fecha o EntityManager assim que retorna o usuario.
	//Esta anotação é para manter o EntityManager durante o contexto transacional
	//deste metodo, assim, podemos buscar as listas de grupo e permições
	//que são Fetch.LAZY por Default.
	@Transactional(readOnly = true) // readonly: indica que nao vamos fazer alteração.
	@Override
	public UserDetails loadUserByUsername(String username) 
	throws UsernameNotFoundException 
	{
		Usuario usuario = usuarioRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail informado"));
		
		return new User(usuario.getEmail(), usuario.getSenha(), getAuthorities(usuario));
	}
	
	private Collection<GrantedAuthority> getAuthorities(Usuario usuario) 
	{
		return usuario.getGrupos().stream()
				.flatMap(grupo -> grupo.getPermissoes().stream())
				.map(permissao -> new SimpleGrantedAuthority(permissao.getNome().toUpperCase()))
				.collect(Collectors.toSet()); //Set: usuario pode estar em dois grupos com permissoes repetidas
	}

}