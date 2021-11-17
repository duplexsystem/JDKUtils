package io.github.duplexsystem.JDKUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

public class JNIUtils {
    private static final ConcurrentHashMap<String, Path> loadedLibs = new ConcurrentHashMap<>();

    public static synchronized String loadLib(String name, Path rootPath) throws IOException {
        String libName = System.mapLibraryName(name);
        Path tempFile = loadedLibs.get(libName);
        if (tempFile == null) {
            String filename = rootPath + libName;
            InputStream libStream = JNIUtils.class.getResourceAsStream(filename);
            if (libStream == null) {
                libStream = new FileInputStream(filename);
            }

            Path tempLib = Files.createTempFile(null, libName);

            Files.copy(libStream, tempLib, StandardCopyOption.REPLACE_EXISTING);
            loadedLibs.put(libName, tempLib);
        }

        assert tempFile != null;
        System.load(tempFile.toAbsolutePath().toString());
        return System.mapLibraryName(name);
    }
}

