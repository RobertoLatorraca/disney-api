package ar.latorraca.disneyapi.services.interfaces;

import java.util.List;
import java.util.UUID;

import ar.latorraca.disneyapi.data.models.GenreEntity;

public interface GenreService {

	List<GenreEntity> findAll();
	
	GenreEntity findById(UUID id);
		
	GenreEntity save(GenreEntity genreEntity);

}
