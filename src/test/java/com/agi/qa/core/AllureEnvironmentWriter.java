package com.agi.qa.core;

import com.agi.qa.config.ConfigFactory;
import com.agi.qa.config.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Writes the Allure {@code environment.properties} and {@code categories.json}
 * into the results directory so the generated report carries meaningful
 * metadata and a consistent defect taxonomy on any machine.
 *
 * <p>Doing this in code (rather than relying on a manual file copy) keeps the
 * report self-contained and reproducible in CI.
 */
public final class AllureEnvironmentWriter {

    private static final String RESULTS_DIR =
            System.getProperty("allure.results.directory", "target/allure-results");

    private AllureEnvironmentWriter() {
        // Utility class - not instantiable.
    }

    public static void write() {
        Path resultsDir = Path.of(RESULTS_DIR);
        try {
            Files.createDirectories(resultsDir);
            writeEnvironment(resultsDir);
            copyCategories(resultsDir);
        } catch (IOException e) {
            // Reporting metadata is best-effort; never fail the suite for it.
            System.err.println("Could not write Allure metadata: " + e.getMessage());
        }
    }

    private static void writeEnvironment(Path resultsDir) throws IOException {
        Configuration config = ConfigFactory.get();

        Map<String, String> env = new LinkedHashMap<>();
        env.put("API.Base.URI", config.baseUri());
        env.put("API.Base.Path", config.basePath());
        env.put("Connection.Timeout.ms", String.valueOf(config.connectionTimeoutMs()));
        env.put("Socket.Timeout.ms", String.valueOf(config.socketTimeoutMs()));
        env.put("Java.Version", System.getProperty("java.version"));
        env.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));

        Properties properties = new Properties();
        properties.putAll(env);

        try (var out = Files.newOutputStream(resultsDir.resolve("environment.properties"))) {
            properties.store(out, "Allure environment - Dog API test suite");
        }
    }

    private static void copyCategories(Path resultsDir) throws IOException {
        try (InputStream in = AllureEnvironmentWriter.class
                .getResourceAsStream("/allure/categories.json")) {
            if (in != null) {
                Files.copy(in, resultsDir.resolve("categories.json"),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
