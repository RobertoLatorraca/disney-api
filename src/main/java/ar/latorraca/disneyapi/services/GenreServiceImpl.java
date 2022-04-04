package ar.latorraca.disneyapi.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.latorraca.disneyapi.data.models.GenreEntity;
import ar.latorraca.disneyapi.data.repositories.GenreRepository;
import ar.latorraca.disneyapi.services.interfaces.GenreService;

@Service
public class GenreServiceImpl implements GenreService {

	@Autowired
	private GenreRepository genreRepository;
	
	@Override
	public List<GenreEntity> findAll() {
		return genreRepository.findAll();
	}

	@Override
	public GenreEntity findById(UUID id) {
		Optional<GenreEntity> result = genreRepository.findById(id);
		return (result.isPresent()) ? result.get() : null;
	}

	@Override
	public GenreEntity save(GenreEntity genreEntity) {
		return genreRepository.save(genreEntity);
	}

}
