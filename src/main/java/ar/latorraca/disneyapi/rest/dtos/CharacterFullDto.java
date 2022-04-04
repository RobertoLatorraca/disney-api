package ar.latorraca.disneyapi.rest.dtos;

import java.util.Set;
import java.util.UUID;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Character (full info)")
@Getter
@Setter
@ToString
public class CharacterFullDto {

	@ApiModelProperty(value = "Id of the character entity",
			example = "8a4b861b-fa73-4e00-8ac4-045742174d68")
	private UUID id;
	
	@ApiModelProperty(value = "Name of the character",
			example = "Mickey Mouse")
	private String name;
	
	@ApiModelProperty(value = "Character's age in years",
			example = "35")
	private Integer age;
	
	@ApiModelProperty(value = "Character's weight in kilograms",
			example = "27")
	private Integer weight;
	
	@ApiModelProperty(value = "Character biography",
			example = "Lorem ipsum dolor sit amet, consectetur adipiscing elit...")
	private String biography;
	
	@ApiModelProperty(value = "Image file name",
			example = "qawNZhtLOoCthJPw7888SGd72GAeKmXF.jpg")
	private String image;
	
	@ApiModelProperty(value = "List of movies")
	private Set<MovieThinDto> movies;
	
}
