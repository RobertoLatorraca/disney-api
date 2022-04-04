package ar.latorraca.disneyapi.rest.dtos;

import java.util.Set;
import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Genre (full info)")
@Getter
@Setter
@ToString
public class GenreFullDto {

	@ApiModelProperty(value = "Id of the genre entity",
			example = "8a4b861b-fa73-4e00-8ac4-045742174d68")
	private UUID id;
	
	@ApiModelProperty(value = "Title of the genre",
			example = "Comedy")
	private String name;
	
	@ApiModelProperty(value = "Image file name",
			example = "qawNZhtLOoCthJPw7888SGd72GAeKmXF.jpg")
	private String image;

	@ApiModelProperty(value = "List of movies")
	private Set<MovieThinDto> movies;

}
