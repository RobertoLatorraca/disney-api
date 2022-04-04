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
import ar.latorraca.disneyapi.rest.dtos.CharacterCreateUpdateDto;
import ar.latorraca.disneyapi.rest.dtos.CharacterFullDto;
import ar.latorraca.disneyapi.rest.dtos.CharacterThinDto;
import ar.latorraca.disneyapi.services.exception.ConflictException;
import ar.latorraca.disneyapi.services.exception.NotFoundException;
import ar.latorraca.disneyapi.services.interfaces.CharacterService;
import ar.latorraca.disneyapi.services.interfaces.FilesStorageService;
import ar.latorraca.disneyapi.services.modelmapper.ModelMapperFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(CharacterController.CHARACTERS)
@Api(tags = "Character")
public class CharacterController {
	
	protected static final String CHARACTERS = "/characters";
	private static final String CHARACTER_ID = "/{characterId}";
	private static final String IMAGE = "/image";
	
	@Autowired
	private CharacterService characterService;
	
	@Autowired
	private FilesStorageService filesStorageService;
	
    @ApiOperation(value = "Get all characters and search characters by name, age or movie")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Characters are shown",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterThinDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CharacterThinDto>> findAllBySpecification(
			@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "age", required = false) String age,
			@RequestParam(name = "movieId", required = false) String movieId) {
    	UUID movieUUID = (movieId != null) ? UUID.fromString(movieId) : null;
    	Integer characterAge = (age != null) ? Integer.parseInt(age) : null;
    	return ResponseEntity.ok(characterService.findAllBySpecification(name, characterAge, movieUUID)
    			.stream().map(
    					c -> ModelMapperFacade.map(c, CharacterThinDto.class))
    			.collect(Collectors.toList()));
	}
    
    @ApiOperation(value = "Find character by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Character found",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@GetMapping(value = CHARACTER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CharacterFullDto> findById(@PathVariable UUID characterId) {
		CharacterEntity characterEntity = characterService.findById(characterId);
		if (characterEntity != null) {
			return ResponseEntity.ok(ModelMapperFacade.map(characterEntity, CharacterFullDto.class));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

    @ApiOperation(value = "Create new character")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Successfully created character",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CharacterFullDto> create(@Valid @RequestBody CharacterCreateUpdateDto characterCreateDto) {
    	CharacterEntity characterNew = ModelMapperFacade.map(characterCreateDto, CharacterEntity.class);    	
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ModelMapperFacade.map(characterService.save(characterNew), CharacterFullDto.class));
	}
    
    @ApiOperation(value = "Partial update character (without movies)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated character",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PatchMapping(value = CHARACTER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CharacterFullDto> update(@PathVariable UUID characterId, @Valid @RequestBody CharacterCreateUpdateDto characterUpdateDto) {
    	CharacterEntity characterUpdate = ModelMapperFacade.map(characterUpdateDto, CharacterEntity.class);
		return ResponseEntity.ok(
				ModelMapperFacade.map(characterService.update(characterId, characterUpdate),
						CharacterFullDto.class));
	}

    @ApiOperation(value = "Upload character's image file (valid types: jpg, png, gif)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image uploaded successfully",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = CharacterFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = CHARACTER_ID + IMAGE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CharacterFullDto> uploadImage(@PathVariable UUID characterId,
			@RequestPart(value = "image") MultipartFile file) {
    	filesStorageService.validateAuthorizedFileExtensions(file.getOriginalFilename());
    	CharacterEntity characterDb = characterService.findById(characterId);
    	if (characterDb == null) throw new NotFoundException();
    	if (characterDb.getImage() != null) filesStorageService.delete(characterDb.getImage());
    	characterDb.setImage(filesStorageService.save(file));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ModelMapperFacade.map(characterService.save(characterDb), CharacterFullDto.class));
	}
    
    @ApiOperation(value = "Download character's image")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image download successfully")
	})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = CHARACTER_ID + IMAGE,
    		produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable UUID characterId) {
    	CharacterEntity characterDb = characterService.findById(characterId);
    	if (characterDb == null) throw new NotFoundException();
    	if (characterDb.getImage() == null) throw new NotFoundException("The character has no image loaded.");
    	byte[] file = filesStorageService.load(characterDb.getImage());
    	MediaType mediaType = filesStorageService.getMediaType(characterDb.getImage());
    	return ResponseEntity.ok().contentType(mediaType).body(file);
    }
	
    @ApiOperation(value = "Delete character")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No content")})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
	@DeleteMapping(CHARACTER_ID)
	public ResponseEntity<?> delete(@PathVariable UUID characterId) {
    	if (!characterService.findById(characterId).getMovies().isEmpty())
			throw new ConflictException("The character you are trying to delete has movies associated with it. First remove these associations.");
		characterService.deleteById(characterId);
		return ResponseEntity.noContent().build();
	}

}
