package ar.latorraca.disneyapi.rest.dtos;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Movie (full info)")
@Getter
@Setter
@ToString
public class MovieFullDto {

	@ApiModelProperty(value = "Id of the movie entity",
			example = "8a4b861b-fa73-4e00-8ac4-045742174d68")
	private UUID id;
	
	@ApiModelProperty(value = "Title of the movie",
			example = "Piratas del Caribe")
	@NotBlank(message = "Movie title cannot be null or blank")
	private String title;
	
	@ApiModelProperty(value = "Date of release",
			example = "2020-10-22")
	private Date releaseDate;
	
	@ApiModelProperty(value = "Rating from 1 to 5",
			example = "5")
    @Min(value = 1, message = "Rating should not be less than 1")
    @Max(value = 5, message = "Rating should not be greater than 5")
	private Integer rating;
	
	@ApiModelProperty(value = "Image file name",
			example = "qawNZhtLOoCthJPw7888SGd72GAeKmXF.jpg")
	private String image;
	
	@ApiModelProperty(value = "List of characters")
	private Set<CharacterThinDto> characters;
	
	@ApiModelProperty(value = "Genre of movie")
	private GenreThinDto genre;
	
}
