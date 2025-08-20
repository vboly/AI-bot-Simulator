package org.Simulator.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileManager {

    private static String Directory = null;

    public FileManager(String Location) {

        if (!Files.isDirectory(Path.of(Location))) {
            System.out.println("ERROR: Directory does not exist!");
            System.exit(0);
        }

        Directory = Location;
    }

    public String read(String File) throws IOException {
        return Files.readString(Path.of(Directory + File));
    }

    public String get(String File) {
        return Directory + File;
    }

    public String load(String File) {
        return "File:\\" + Directory + File;
    }

    public boolean folder(String Name) {
        File Folder = new File(Name);
        return Folder.mkdir();
    }

    public boolean exists(String File) {
       return Files.isDirectory(Path.of(File));
    }

    public static void write() {

    }

    public static void create() {

    }

}

