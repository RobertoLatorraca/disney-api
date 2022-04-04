package ar.latorraca.disneyapi.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import ar.latorraca.disneyapi.data.models.CharacterEntity;
import ar.latorraca.disneyapi.data.models.MovieEntity;
import ar.latorraca.disneyapi.data.repositories.CharacterRepository;
import ar.latorraca.disneyapi.data.repositories.MovieRepository;
import ar.latorraca.disneyapi.services.exception.NotFoundException;
import ar.latorraca.disneyapi.services.interfaces.CharacterService;
import ar.latorraca.disneyapi.services.modelmapper.ModelMapperFacade;

@Service
public class CharacterServiceImpl implements CharacterService {

	@Autowired
	private CharacterRepository characterRepository;
	
	@Autowired
	private MovieRepository movieRepository;

	@Override
	public List<CharacterEntity> findAll() {
		return characterRepository.findAll();
	}
	
	@Override
	public CharacterEntity findById(UUID id) {
		Optional<CharacterEntity> result = characterRepository.findById(id);
		return (result.isPresent()) ? result.get() : null;
	}

	@Override
	public List<CharacterEntity> findAllBySpecification(String name, Integer age, UUID movieId) {
		Optional<MovieEntity> movie = (movieId != null) ?
				movieRepository.findById(movieId) :
				Optional.empty();
		return characterRepository.findAll(new Specification<CharacterEntity>() {
			private static final long serialVersionUID = 1L;
			@Override
			public Predicate toPredicate(Root<CharacterEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();					
				if (name != null) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
				}
				if (age != null) {
					predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("age"), age)));
				}
				if (movie.isPresent()) {
					predicates.add(criteriaBuilder.isMember(movie.get(), root.get("movies")));
				}				
				return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
			}
		});
	}

	@Override
	public CharacterEntity save(CharacterEntity characterEntity) {
		return characterRepository.save(characterEntity);
	}

	@Override
	public CharacterEntity update(UUID id, CharacterEntity characterEntity) {
		CharacterEntity characterDb = characterRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		characterEntity.setMovies(null);
		characterEntity.setImage(null);
		ModelMapperFacade.patchObject(characterEntity, characterDb);
		return characterRepository.save(characterDb);
	}

	@Override
	public void deleteById(UUID id) {
		characterRepository.deleteById(id);
	}

}
