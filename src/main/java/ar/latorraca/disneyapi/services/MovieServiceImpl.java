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

import ar.latorraca.disneyapi.data.models.MovieEntity;
import ar.latorraca.disneyapi.data.repositories.MovieRepository;
import ar.latorraca.disneyapi.services.exception.NotFoundException;
import ar.latorraca.disneyapi.services.interfaces.MovieService;
import ar.latorraca.disneyapi.services.modelmapper.ModelMapperFacade;

@Service
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieRepository movieRepository;

	@Override
	public List<MovieEntity> findAll() {
		return movieRepository.findAll();
	}
	
	@Override
	public MovieEntity findById(UUID id) {
		Optional<MovieEntity> result = movieRepository.findById(id);
		return (result.isPresent()) ? result.get() : null;
	}
	
	@Override
	public List<MovieEntity> findAllBySpecification(String title, UUID genreId, String order) {
		return movieRepository.findAll(new Specification<MovieEntity>() {
				private static final long serialVersionUID = 1L;
				@Override
				public Predicate toPredicate(Root<MovieEntity> root, CriteriaQuery<?> query,
						CriteriaBuilder criteriaBuilder) {
					List<Predicate> predicates = new ArrayList<>();					
					if (title != null) {
						predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("title"), "%" + title + "%")));
					}
					if (genreId != null) {
						predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("genre").get("id"), genreId)));
					}
					if (order != null) {
						if (order.equalsIgnoreCase("asc")) {
							query.orderBy(criteriaBuilder.asc(root.get("title")));
						} else {	
							query.orderBy(criteriaBuilder.desc(root.get("title")));
						}
					}
					return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
				}
		});
	}

	@Override
	public MovieEntity save(MovieEntity movieEntity) {
		return movieRepository.save(movieEntity);
	}

	@Override
	public MovieEntity update(UUID id, MovieEntity movieEntity) {
		MovieEntity movieDb = movieRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		movieEntity.setCharacters(null);
		movieEntity.setImage(null);
		ModelMapperFacade.patchObject(movieEntity, movieDb);
		return movieRepository.save(movieDb);
	}

	@Override
	public void deleteById(UUID id) {
		movieRepository.deleteById(id);
	}

}
