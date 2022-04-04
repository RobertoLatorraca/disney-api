package ar.latorraca.disneyapi.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ar.latorraca.disneyapi.services.interfaces.FilesStorageService;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

	private final Path ROOT = Path.of("/images");

	@Override
	public void init() {
        if (!Files.exists(ROOT)) {
            try {
            	Files.createDirectory(ROOT);
            } catch (IOException e) {
            	throw new RuntimeException("Could not initialize folder for images!");
            }
        }
	}

	@Override
	public String save(MultipartFile file) {
		String newFileName = randomFileName(file.getOriginalFilename(), 32);
	    try (InputStream fileInputStream = file.getInputStream()) {
	    	Files.copy(fileInputStream, ROOT.resolve(newFileName));
	    	return newFileName;
	    } catch (IOException e) {
	    	throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
	    }
	}

	@Override
	public byte[] load(String filename) {
        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File(ROOT.resolve(filename).toString()));
        } catch (IOException e) {
	    	throw new RuntimeException("Could not read the file. Error: " + e.getMessage());
        }
		return image;
	}

	@Override
	public void delete(String filename) {
		try {
			Files.deleteIfExists(ROOT.resolve(filename));
		} catch (IOException e) {
	    	throw new RuntimeException("Could not remove the previous image file. Error: " + e.getMessage());
		}		
	}

	@Override
	public boolean validateAuthorizedFileExtensions(String filename) {
		String ext = FilenameUtils.getExtension(filename);
		if (ext.equalsIgnoreCase("jpg") ||
				ext.equalsIgnoreCase("png") ||
				ext.equalsIgnoreCase("gif")) {
			return true;
		} else {
			throw new IllegalArgumentException("Invalid file type.");
		}
	}

	@Override
	public MediaType getMediaType(String filename) {
		String ext = FilenameUtils.getExtension(filename);
		switch (ext) {
		case "jpg":
			return MediaType.IMAGE_JPEG;
		case "png":
			return MediaType.IMAGE_PNG;
		case "gif":
			return MediaType.IMAGE_GIF;
		}
		return null;
	}

	private String randomFileName(String originalFilename, int lengthNewName) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder stringBuilder = new StringBuilder();
		while (lengthNewName-- > 0) {
			int i = (int) (Math.random() * chars.length());
			stringBuilder.append(chars.charAt(i));
		}
		String nameSplitted[] = originalFilename.split("\\.");
		stringBuilder.append("." + nameSplitted[nameSplitted.length - 1]);
		return stringBuilder.toString();
	}

}
