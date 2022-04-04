package ar.latorraca.disneyapi.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ar.latorraca.disneyapi.data.models.CharacterEntity;
import ar.latorraca.disneyapi.data.models.MovieEntity;
import ar.latorraca.disneyapi.rest.dtos.CharacterFullDto;
import ar.latorraca.disneyapi.rest.dtos.MovieFullDto;
import ar.latorraca.disneyapi.rest.dtos.MovieThinDto;
import ar.latorraca.disneyapi.rest.dtos.MovieUpdateDto;
import ar.latorraca.disneyapi.services.exception.BadRequestException;
import ar.latorraca.disneyapi.services.exception.FieldAlreadyExistException;
import ar.latorraca.disneyapi.services.exception.NotFoundException;
import ar.latorraca.disneyapi.services.interfaces.CharacterService;
import ar.latorraca.disneyapi.services.interfaces.FilesStorageService;
import ar.latorraca.disneyapi.services.interfaces.MovieService;
import ar.latorraca.disneyapi.services.modelmapper.ModelMapperFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(MovieController.MOVIES)
@Api(tags = "Movie")
public class MovieController {
	
	protected static final String MOVIES = "/movies";
	private static final String MOVIE_ID = "/{movieId}";
	private static final String IMAGE = "/image";
	protected static final String CHARACTERS = "/characters";
	private static final String CHARACTER_ID = "/{characterId}";

	@Autowired
	private MovieService movieService;
	
	@Autowired
	private CharacterService characterService;
	
	@Autowired
	private FilesStorageService filesStorageService;
	
    @ApiOperation(value = "Find movie by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Movie found",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = MovieFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@GetMapping(value = MOVIE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieFullDto> findById(@PathVariable UUID movieId) {
		MovieEntity movieEntity = movieService.findById(movieId);
		if (movieEntity != null) {
			return ResponseEntity.ok(ModelMapperFacade.map(movieEntity, MovieFullDto.class));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
    @ApiOperation(value = "Get all movies and search for movies by title, genre ID and sort them in ascending or descending order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Movies are shown",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = MovieThinDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieThinDto>> findAllBySpecification(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "genreId", required = false) String genreId,
			@RequestParam(name = "order", required = false) String order) {
    	UUID genreUUID = (genreId != null) ? UUID.fromString(genreId) : null;
    	if (order != null && !(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))) {
    		throw new BadRequestException("The order parameter can only be asc or desc.");
    	}
    	return ResponseEntity.ok(movieService.findAllBySpecification(name, genreUUID, order).stream().map(
    			m -> ModelMapperFacade.map(m, MovieThinDto.class))
    			.collect(Collectors.toList()));
	}
	
    @ApiOperation(value = "Create new movie")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Successfully created movie",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = MovieFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieFullDto> create(@Valid @RequestBody MovieFullDto movieFullDto) {
    	if (movieFullDto.getId() != null) throw new BadRequestException("Movie id must be null.");
    	movieFullDto.setImage(null);
    	MovieEntity movieNew = ModelMapperFacade.map(movieFullDto, MovieEntity.class);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ModelMapperFacade.map(movieService.save(movieNew), MovieFullDto.class));
	}

    @ApiOperation(value = "Partial update movie (without characters)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated movie",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = MovieFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PatchMapping(value = MOVIE_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieFullDto> update(@PathVariable UUID movieId, @Valid @RequestBody MovieUpdateDto movieUpdateDto) {
    	MovieEntity movieUpdate = ModelMapperFacade.map(movieUpdateDto, MovieEntity.class);
		return ResponseEntity.ok(
				ModelMapperFacade.map(movieService.update(movieId, movieUpdate),
						MovieFullDto.class));
	}
	
    @ApiOperation(value = "Add character to a movie")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully added character",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = MovieFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = MOVIE_ID + CHARACTERS + CHARACTER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MovieFullDto> addCharacter(@PathVariable UUID movieId, @PathVariable UUID characterId) {
		MovieEntity movieEntity = movieService.findById(movieId);
		if (movieEntity == null) throw new NotFoundException("Movie not found.");
		CharacterEntity characterEntity = characterService.findById(characterId);
		if (characterEntity == null) throw new NotFoundException("Character not found.");
		if (!movieEntity.getCharacters().add(characterEntity))
			throw new FieldAlreadyExistException("The character already exists for the selected movie.");
		return ResponseEntity.ok(
				ModelMapperFacade.map(movieService.save(movieEntity), MovieFullDto.class));
	}
	
    @ApiOperation(value = "Remove character to a movie")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content")})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
	@DeleteMapping(value = MOVIE_ID + CHARACTERS + CHARACTER_ID)
	public ResponseEntity<?> removeCharacter(@PathVariable UUID movieId, @PathVariable UUID characterId) {
		MovieEntity movieEntity = movieService.findById(movieId);
		if (movieEntity == null) throw new NotFoundException("Movie not found.");
		CharacterEntity characterEntity = characterService.findById(characterId);
		if (characterEntity == null) throw new NotFoundException("Character not found.");
		movieEntity.getCharacters().remove(characterEntity);
		movieService.save(movieEntity);
		return ResponseEntity.noContent().build();
	}
	
    @ApiOperation(value = "Upload movie's image file (valid types: jpg, png, gif)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image uploaded successfully",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = MOVIE_ID + IMAGE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MovieFullDto> uploadImage(@PathVariable UUID movieId,
			@RequestPart(value = "image") MultipartFile file) {
    	filesStorageService.validateAuthorizedFileExtensions(file.getOriginalFilename());
    	MovieEntity movieDb = movieService.findById(movieId);
    	if (movieDb == null) throw new NotFoundException();
    	if (movieDb.getImage() != null) filesStorageService.delete(movieDb.getImage());
    	movieDb.setImage(filesStorageService.save(file));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ModelMapperFacade.map(movieService.save(movieDb), MovieFullDto.class));
	}
    
    @ApiOperation(value = "Download movie's image")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image download successfully")
	})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = MOVIE_ID + IMAGE,
    		produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable UUID movieId) {
    	MovieEntity movieDb = movieService.findById(movieId);
    	if (movieDb == null) throw new NotFoundException();
    	if (movieDb.getImage() == null) throw new NotFoundException("The movie has no image loaded.");
    	byte[] file = filesStorageService.load(movieDb.getImage());
    	MediaType mediaType = filesStorageService.getMediaType(movieDb.getImage());
    	return ResponseEntity.ok().contentType(mediaType).body(file);
    }
	
    @ApiOperation(value = "Delete movie")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content")})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
	@DeleteMapping(MOVIE_ID)
	public ResponseEntity<?> delete(@PathVariable UUID movieId) {
		movieService.deleteById(movieId);
		return ResponseEntity.noContent().build();
	}
	
}
