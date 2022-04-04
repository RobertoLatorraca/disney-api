package ar.latorraca.disneyapi.rest.dtos;

import java.util.Date;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Movie (for updates)")
@Getter
@Setter
@ToString
public class MovieUpdateDto {
	
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
	
	@ApiModelProperty(value = "Genre of movie")
	private GenreThinDto genre;
	
}
