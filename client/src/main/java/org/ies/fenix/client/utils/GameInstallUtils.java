package org.ies.fenix.client.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class GameInstallUtils {

    private static final String APP_FOLDER_NAME = ".fenixmind";
    private static final String INSTALLED_GAMES_FILE = "installed-games.properties";

    private GameInstallUtils() {
    }

    public static void saveInstallPath(Integer gameId, Path installDirectory) throws IOException {
        if (gameId == null || installDirectory == null) {
            return;
        }

        Properties properties = loadProperties();

        properties.setProperty(
                String.valueOf(gameId),
                installDirectory.toAbsolutePath().normalize().toString()
        );

        saveProperties(properties);
    }

    public static void launchGame(Integer gameId) throws IOException {
        Path installDirectory = getInstallPath(gameId);

        if (installDirectory == null || !Files.exists(installDirectory)) {
            throw new IOException("Game is not installed.");
        }

        Path executable = findExecutable(installDirectory)
                .orElseThrow(() -> new IOException("No executable file found."));

        new ProcessBuilder(executable.toAbsolutePath().toString())
                .directory(executable.getParent().toFile())
                .start();
    }

    public static boolean canLaunchGame(Integer gameId) {
        try {
            Path installDirectory = getInstallPath(gameId);

            if (installDirectory == null || !Files.exists(installDirectory)) {
                return false;
            }

            return findExecutable(installDirectory).isPresent();

        } catch (Exception e) {
            return false;
        }
    }

    public static Path getInstallPath(Integer gameId) throws IOException {
        if (gameId == null) {
            return null;
        }

        Properties properties = loadProperties();
        String path = properties.getProperty(String.valueOf(gameId));

        if (path == null || path.isBlank()) {
            return null;
        }

        return Path.of(path);
    }

    private static Optional<Path> findExecutable(Path installDirectory) throws IOException {
        try (Stream<Path> files = Files.walk(installDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(GameInstallUtils::isExecutableCandidate)
                    .min(Comparator.comparingInt(path -> path.getNameCount()));
        }
    }

    private static boolean isExecutableCandidate(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();

        if (isWindows()) {
            return fileName.endsWith(".exe");
        }

        return Files.isExecutable(path);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name")
                .toLowerCase()
                .contains("win");
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        Path propertiesFile = getPropertiesFile();

        if (!Files.exists(propertiesFile)) {
            return properties;
        }

        try (InputStream inputStream = Files.newInputStream(propertiesFile)) {
            properties.load(inputStream);
        }

        return properties;
    }

    private static void saveProperties(Properties properties) throws IOException {
        Path propertiesFile = getPropertiesFile();

        Files.createDirectories(propertiesFile.getParent());

        try (OutputStream outputStream = Files.newOutputStream(propertiesFile)) {
            properties.store(outputStream, "Installed FenixMind games");
        }
    }

    private static Path getPropertiesFile() {
        return Path.of(
                System.getProperty("user.home"),
                APP_FOLDER_NAME,
                INSTALLED_GAMES_FILE
        );
    }
}