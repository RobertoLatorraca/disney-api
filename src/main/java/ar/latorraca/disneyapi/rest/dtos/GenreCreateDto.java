package ar.latorraca.disneyapi.rest.dtos;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "Genre (create new)")
@Setter
@Getter
public class GenreCreateDto {

	@ApiModelProperty(value = "Title of the genre",
			example = "Comedy")
	@NotBlank(message = "Genre title cannot be null or blank")
	private String name;
	
}
