package com.restaurant.backend;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);
	private static final String DB_FILE_NAME = "restaurant.db";

	public static void main(String[] args) {
		Path databasePath = resolveDatabasePath();
		log.info("Using SQLite database at {}", databasePath);

		SpringApplication application = new SpringApplication(BackendApplication.class);
		application.setDefaultProperties(Map.of(
				"spring.datasource.url",
				"jdbc:sqlite:" + databasePath
		));
		application.run(args);
	}

	private static Path resolveDatabasePath() {
		String overridePath = System.getenv("RESTAURANT_DB_PATH");
		if (overridePath != null && !overridePath.isBlank()) {
			return Path.of(overridePath).toAbsolutePath().normalize();
		}

		Path currentDirectory = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
		Path backendDirectory = findBackendDirectory(currentDirectory);
		return backendDirectory.resolve(DB_FILE_NAME);
	}

	private static Path findBackendDirectory(Path startDirectory) {
		Path current = startDirectory;
		while (current != null) {
			if (isBackendDirectory(current)) {
				return current;
			}

			Path nestedBackendDirectory = current.resolve("backend");
			if (isBackendDirectory(nestedBackendDirectory)) {
				return nestedBackendDirectory;
			}

			current = current.getParent();
		}

		return startDirectory;
	}

	private static boolean isBackendDirectory(Path directory) {
		return Files.exists(directory.resolve("build.gradle"))
				&& Files.isDirectory(directory.resolve("src/main/resources"));
	}
}
