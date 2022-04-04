package ar.latorraca.disneyapi.data.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "characters")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CharacterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.hibernate.annotations.Type(type = "uuid-char")
	@Column(name = "id", columnDefinition = "varchar(36)")
	private UUID id;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	private Integer age;
	private Integer weight;
	
	@Column(columnDefinition = "TEXT")
	private String biography;
	
	private String image;
	
	@ManyToMany(mappedBy = "characters")
	@Builder.Default
	private Set<MovieEntity> movies = new HashSet<>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CharacterEntity other = (CharacterEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CharacterEntity [id=" + id + ", name=" + name + ", age=" + age + ", weight=" + weight + ", biography="
				+ biography + ", image=" + image + "]";
	}

}
