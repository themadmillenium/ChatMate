package dev.tmm.chatmate.util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

public class FileUtility {
    private FileUtility() {
    }

    public static void saveToFile(String filePath, String... args) throws IOException {
        File f = new File(filePath);
        f.getParentFile().mkdirs();
        f.createNewFile();

        String out = "";

        for (String s : args) {
            out += s + "\n";
        }

        if (out.length() > 0) out = out.substring(0, out.length() - 1);

        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        writer.write(out);
        writer.close();
    }

    public static String[] loadFromFile(String filePath) throws IOException {
        File f = new File(filePath);
        if (!f.exists()) return new String[0];

        ArrayList<String> fileContent = new ArrayList<>();

        BufferedReader in = new BufferedReader(new FileReader(f));

        String line;

        while ((line = in.readLine()) != null) {
            fileContent.add(line);
        }

        in.close();

        return fileContent.toArray(new String[fileContent.size()]);
    }

    public static boolean exists(String filePath) {
        return new File(filePath).exists();
    }

    public static File getFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (file.exists()) return file;

        file.getParentFile().mkdirs();
        file.createNewFile();

        return file;
    }

    public static void removeFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists()) return;

        file.delete();
    }

    public static File getDirectory(String filePath) throws IOException {
        File file = new File(filePath);

        if (file.exists()) return file;

        file.mkdirs();

        return file;
    }
}
