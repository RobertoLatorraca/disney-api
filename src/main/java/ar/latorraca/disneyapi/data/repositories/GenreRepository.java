package ar.latorraca.disneyapi.data.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.latorraca.disneyapi.data.models.GenreEntity;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, UUID> {

}
