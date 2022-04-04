package ar.latorraca.disneyapi.data.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import ar.latorraca.disneyapi.data.models.CharacterEntity;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, UUID>, JpaSpecificationExecutor<CharacterEntity> {
	
}
