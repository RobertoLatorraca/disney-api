package ar.latorraca.disneyapi.services.interfaces;

import java.util.List;
import java.util.UUID;

import ar.latorraca.disneyapi.data.models.CharacterEntity;

public interface CharacterService {
	
	List<CharacterEntity> findAll();

	CharacterEntity findById(UUID id);
	
	List<CharacterEntity> findAllBySpecification(String name, Integer age, UUID movieId);
	
	CharacterEntity save(CharacterEntity characterEntity);
	
	CharacterEntity update(UUID id, CharacterEntity characterEntity);
	
	void deleteById(UUID id);

}
