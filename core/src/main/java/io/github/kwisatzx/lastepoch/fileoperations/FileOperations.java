package io.github.kwisatzx.lastepoch.fileoperations;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FileOperations {
    protected final Path file;
    private final Path backups;
    protected StringBuilder fileString;

    protected FileOperations(String saveDirectory, String filePath) throws IOException {
        file = Paths.get(saveDirectory, filePath);
        backups = Paths.get(saveDirectory, "backups");
        ensureBackupsDirectory();
        ensureFileIsReadable();
        backupFile();

        try (Stream<String> fileStream = Files.lines(file)) {
            fileString = new StringBuilder(fileStream.collect(Collectors.joining())
                                                   .replaceAll("\t", ""));
        }
    }

    protected Logger getLogger(Class<?> callingClass) {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(callingClass);
    }

    public static String getStackTraceString(Exception e) {
        StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        return stackTraceWriter.toString();
    }

    public boolean saveToFile() {
        try {
            FileWriter writer = new FileWriter(file.toFile(), false);
            writer.write(fileString.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            getLogger(this.getClass()).error(getStackTraceString(e));
            return false;
        }
    }

    protected String copyCharacterFile() throws IOException {
        Path newPath = Paths.get(file.toString().substring(0, file.toString().length() - 2));
        newPath = increaseFileNumber(newPath);
        Files.copy(file, newPath);
        return newPath.getFileName().toString();
    }

    private void ensureBackupsDirectory() throws IOException {
        if (!(Files.isDirectory(backups) && Files.isReadable(backups) && Files.isWritable(backups))) {
            Files.createDirectory(backups);
        }
    }

    private void ensureFileIsReadable() {
        if (!(Files.exists(file) && Files.isReadable(file) && Files.isWritable(file))) {
            System.out.println(
                    "File doesn't exist or can't be read/written to. Exiting in 3 seconds..."); //TODO Logger or exception
            System.exit(0);
        }
    }

    private void backupFile() throws IOException {
        Path backupFile = Paths.get(backups.toString(), file.getFileName() + "_" + LocalDate.now());
        if (Files.exists(backupFile)) backupFile = increaseFileNumber(backupFile);
        Files.copy(file, backupFile);
    }

    private Path increaseFileNumber(Path dir) {
        int i = 0;
        String num;
        do {
            i++;
            num = "_" + i;
        } while (Files.exists(Paths.get(dir + num)));
        return Paths.get(dir + "_" + i);
    }

    protected Optional<String> loadTextFile(String path) {
        InputStream file = this.getClass().getResourceAsStream(path);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file))) {
                String fileString = reader.lines().collect(Collectors.joining());
                return Optional.of(fileString);
            } catch (IOException e) {
                e.printStackTrace(); //TODO logger
                return Optional.empty();
            }
        } else return Optional.empty(); //TODO logger
    }
}
