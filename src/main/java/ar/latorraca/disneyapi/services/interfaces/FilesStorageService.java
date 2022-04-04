package ar.latorraca.disneyapi.services.interfaces;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {

	  void init();
	  
	  String save(MultipartFile file);
	  
	  byte[] load(String filename);

	  MediaType getMediaType(String filename);
	  
	  void delete(String filename);
	 
	  boolean validateAuthorizedFileExtensions(String filename);
	  
}
