package nl.jqno.equalsverifier.verify_release.jar.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JarReader implements AutoCloseable {

    private final String filename;
    private final FileSystem fs;
    private final Set<String> entries;

    public JarReader(String filename) {
        this.filename = filename;
        this.fs = getFileSystem();
        this.entries = buildEntryList();
    }

    @Override
    public void close() throws Exception {
        fs.close();
    }

    public String getFilename() {
        return filename;
    }

    public Set<String> getEntries() {
        return Collections.unmodifiableSet(entries);
    }

    public byte[] getContentOf(String innerFilename) {
        var path = fs.getPath(innerFilename);
        try (var out = new ByteArrayOutputStream()) {
            Files.copy(path, out);
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new AssertionError("Failed to read " + innerFilename + " from " + filename, e);
        }
    }

    private FileSystem getFileSystem() {
        var file = getClass().getClassLoader().getResource(filename);
        try {
            var uri = URI.create("jar:" + file.toURI().toString());
            return FileSystems.newFileSystem(uri, Map.of());
        }
        catch (IOException | URISyntaxException e) {
            throw new AssertionError("Failed to read " + filename, e);
        }
    }

    private Set<String> buildEntryList() {
        var path = fs.getPath("/");
        try (var walk = Files.walk(path)) {
            return StreamSupport.stream(walk.spliterator(), false).map(Path::toString).collect(Collectors.toSet());
        }
        catch (IOException e) {
            throw new AssertionError("Failed to read files from " + filename, e);
        }
    }
}
