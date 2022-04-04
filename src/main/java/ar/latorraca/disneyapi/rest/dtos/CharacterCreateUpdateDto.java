package ar.latorraca.disneyapi.rest.dtos;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Character (for persistence)")
@Getter
@Setter
@ToString
public class CharacterCreateUpdateDto {

	@ApiModelProperty(value = "Name of the character",
			example = "Mickey Mouse")
	@NotBlank(message = "Character name cannot be null or blank")
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
	
}
