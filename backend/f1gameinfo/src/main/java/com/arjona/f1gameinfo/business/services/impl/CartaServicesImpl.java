package com.arjona.f1gameinfo.business.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.arjona.f1gameinfo.business.repositores.CartaUsuarioRepository;
import com.arjona.f1gameinfo.business.repositores.CircuitoRepository;
import com.arjona.f1gameinfo.business.repositores.PilotoRepository;
import com.arjona.f1gameinfo.business.services.CartaServices;
import com.arjona.f1gameinfo.security.integration.model.Usuario;
import com.arjona.f1gameinfo.security.integration.repositories.UsuarioRepository;

import jakarta.transaction.Transactional;

import com.arjona.f1gameinfo.business.model.CartaUsuario;
import com.arjona.f1gameinfo.business.model.CartaUsuarioDTO;
import com.arjona.f1gameinfo.business.model.Circuito;
import com.arjona.f1gameinfo.business.model.Piloto;

@Service
public class CartaServicesImpl implements CartaServices{
	
	private UsuarioRepository usuarioRepository;
	
	private CircuitoRepository circuitoRepository;
	
	private PilotoRepository pilotoRepository;

	private CartaUsuarioRepository cartaUsuarioRepository;

	public CartaServicesImpl(UsuarioRepository usuarioRepository, CircuitoRepository circuitoRepository,
			PilotoRepository pilotoRepository, CartaUsuarioRepository cartaUsuarioRepository) {
		this.usuarioRepository = usuarioRepository;
		this.circuitoRepository = circuitoRepository;
		this.pilotoRepository = pilotoRepository;
		this.cartaUsuarioRepository = cartaUsuarioRepository;
	}

	@Transactional
	@Override
	public void actualizarCartas(Long id, Integer monedas, Long idPiloto, Long idCircuito) {
		if(! usuarioRepository.existsById(id))
			throw new IllegalStateException("Usuario no existente.");
		
		Usuario usuario = usuarioRepository.findById(id).get();
		usuario.setMonedas(monedas);
		
		if(idPiloto == null && idCircuito == null)
			throw new IllegalStateException("No hay piloto o circuito que actualizar.");
		
		if(idPiloto != null) {
			if(! pilotoRepository.existsById(idPiloto))
				throw new IllegalStateException("Piloto no existente.");
			Piloto piloto = pilotoRepository.findById(idPiloto).get();
			usuario.addPiloto(piloto);
		}
		
		if(idCircuito != null) {
			if(! circuitoRepository.existsById(idCircuito))
				throw new IllegalStateException("Circuito no existente.");
			Circuito circuito = circuitoRepository.findById(idCircuito).get();
			usuario.addCircuito(circuito);
		}
		
		usuarioRepository.save(usuario);
	}

	@Override
	public List<CartaUsuarioDTO> getAllDtos() {
		return cartaUsuarioRepository.getAllDtos();
	}

	@Override
	public void subirCarta(Long id, Integer valoracion, MultipartFile imagen) {
		if(! usuarioRepository.existsById(id))
			throw new IllegalStateException("Usuario no existente.");
		
		Usuario usuario = usuarioRepository.findById(id).get();
		
		String rutaImg = "uploads/images/" + imagen.getOriginalFilename();
		File file = new File(rutaImg);
		
		try {
			imagen.transferTo(file);
		} catch (IOException e) {
			throw new IllegalStateException("Error con la imagen.");
		}
		
		CartaUsuario carta = new CartaUsuario();
		carta.setUsuario(usuario);
		carta.setValoracion(valoracion);
		carta.setRutaImagen("/uploads/images/" + imagen.getOriginalFilename());
		
		cartaUsuarioRepository.save(carta);
	}
	
}
