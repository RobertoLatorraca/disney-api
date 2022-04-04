package ar.latorraca.disneyapi.rest.dtos;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ApiModel(value = "Character (thin info)")
@Getter
@Setter
@ToString
public class CharacterThinDto {
	
	@ApiModelProperty(value = "Id of the character entity",
			example = "8a4b861b-fa73-4e00-8ac4-045742174d68")
	private UUID id;
	
	@ApiModelProperty(value = "Name of the character",
			example = "Mickey Mouse")
	@NotBlank(message = "Character name cannot be null or blank")
	private String name;
	
	@ApiModelProperty(value = "Image file name",
			example = "qawNZhtLOoCthJPw7888SGd72GAeKmXF.jpg")
	private String image;

}
