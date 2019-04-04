package com.fqa;
import javax.naming.spi.DirectoryManager;
import java.io.File;
import java.util.*;

public class MyUtil {

    public static UUID urlToUUID(String url) {
        return UUID.nameUUIDFromBytes(url.getBytes());
    }

    public static List<String> getDirectoryListing(String path) {
        File f = new File(path);
        if(f.list() == null) {System.out.println(path);}
        return Arrays.asList(f.list());

    }

    public static void CreateDirectory(String dirname) {
        File f = new File(dirname);
        f.mkdir();
        if (f.isDirectory())
            System.out.println("It's a directory");
    }

    public static void log(String s) {
        System.out.println(s);
    }
}

