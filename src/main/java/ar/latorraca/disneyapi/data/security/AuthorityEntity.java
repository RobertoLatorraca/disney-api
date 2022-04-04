package ar.latorraca.disneyapi.data.security;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@IdClass(AuthorityEntityId.class)
@Setter
@Getter
public class AuthorityEntity {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	private UserEntity user;
	
	@Id
	@Enumerated(EnumType.STRING)
	private Authority authority;

	@Override
	public String toString() {
		return "AuthorityEntity [authority=" + authority + "]";
	}

}
