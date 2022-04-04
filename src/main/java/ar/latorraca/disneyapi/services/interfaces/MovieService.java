package ar.latorraca.disneyapi.services.interfaces;

import java.util.List;
import java.util.UUID;

import ar.latorraca.disneyapi.data.models.MovieEntity;

public interface MovieService {

	List<MovieEntity> findAll();

	MovieEntity findById(UUID id);
	
	List<MovieEntity> findAllBySpecification(String title, UUID genreId, String order);
	
	MovieEntity save(MovieEntity movieEntity);
	
	MovieEntity update(UUID id, MovieEntity movieEntity);
	
	void deleteById(UUID id);

		
}
