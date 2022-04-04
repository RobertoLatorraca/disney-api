package ar.latorraca.disneyapi.data.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class MovieEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@org.hibernate.annotations.Type(type = "uuid-char")
	@Column(name = "id", columnDefinition = "varchar(36)")
	private UUID id;
	
	@Column(nullable = false, unique = true)
	private String title;
	
	@Column(name = "release_date", columnDefinition = "date")
	private Date releaseDate;
	
	private Integer rating;
	private String image;
	
	@ManyToMany
	@JoinTable(name = "characters_movies",
    		joinColumns = @JoinColumn(name = "movie_id"),
    		inverseJoinColumns = @JoinColumn(name = "character_id"))
	@Builder.Default
	private Set<CharacterEntity> characters = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	private GenreEntity genre;

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
		MovieEntity other = (MovieEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MovieEntity [id=" + id + ", title=" + title + ", releaseDate=" + releaseDate + ", rating=" + rating
				+ ", image=" + image + "]";
	}

}
