package ar.latorraca.disneyapi.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ar.latorraca.disneyapi.data.models.GenreEntity;
import ar.latorraca.disneyapi.rest.dtos.GenreCreateDto;
import ar.latorraca.disneyapi.rest.dtos.GenreFullDto;
import ar.latorraca.disneyapi.rest.dtos.GenreThinDto;
import ar.latorraca.disneyapi.services.exception.NotFoundException;
import ar.latorraca.disneyapi.services.interfaces.FilesStorageService;
import ar.latorraca.disneyapi.services.interfaces.GenreService;
import ar.latorraca.disneyapi.services.modelmapper.ModelMapperFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping(GenreController.GENRES)
@Api(tags = "Genre")
public class GenreController {
	
	protected static final String GENRES = "/genres";
	private static final String GENRE_ID = "/{genreId}";
	private static final String IMAGE = "/image";
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private FilesStorageService filesStorageService;
	
    @ApiOperation(value = "Get all genres")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "All genres are shown",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = GenreThinDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<GenreThinDto>> findAll() {
    	return ResponseEntity.ok(
    			genreService.findAll().stream()
    					.map(g -> ModelMapperFacade.map(g, GenreThinDto.class))
    					.collect(Collectors.toList()));
	}
	
    @ApiOperation(value = "Create new genre")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Successfully created genre",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = GenreFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenreFullDto> create(@Valid @RequestBody GenreCreateDto genreCreateDto) {
    	GenreEntity genreNew = ModelMapperFacade.map(genreCreateDto, GenreEntity.class);
    	return ResponseEntity.status(HttpStatus.CREATED).body(
    			ModelMapperFacade.map(genreService.save(genreNew), GenreFullDto.class));
    }
	
    @ApiOperation(value = "Upload genre's image file (valid types: jpg, png, gif)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image uploaded successfully",
					content = {@Content(mediaType = "application/json",
					schema = @Schema(implementation = GenreFullDto.class))})
	})
    @ResponseStatus(value = HttpStatus.OK)
	@PostMapping(value = GENRE_ID + IMAGE,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<GenreFullDto> uploadImage(@PathVariable UUID genreId,
			@RequestPart(value = "image") MultipartFile file) {
    	filesStorageService.validateAuthorizedFileExtensions(file.getOriginalFilename());
    	GenreEntity genreDb = genreService.findById(genreId);
    	if (genreDb == null) throw new NotFoundException();
    	if (genreDb.getImage() != null) filesStorageService.delete(genreDb.getImage());
    	genreDb.setImage(filesStorageService.save(file));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ModelMapperFacade.map(genreService.save(genreDb), GenreFullDto.class));
	}
    
    @ApiOperation(value = "Download genre's image")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Image download successfully")
	})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(value = GENRE_ID + IMAGE,
    		produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable UUID genreId) {
    	GenreEntity genreDb = genreService.findById(genreId);
    	if (genreDb == null) throw new NotFoundException();
    	if (genreDb.getImage() == null) throw new NotFoundException("The genre has no image loaded.");
    	byte[] file = filesStorageService.load(genreDb.getImage());
    	MediaType mediaType = filesStorageService.getMediaType(genreDb.getImage());
    	return ResponseEntity.ok().contentType(mediaType).body(file);
    }
	
}
