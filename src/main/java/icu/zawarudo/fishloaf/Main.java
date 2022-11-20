package icu.zawarudo.fishloaf;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        InputStream itchat4jJar = Main.class.getClassLoader().getResourceAsStream("libs/itchat4j-1.1.0-jar-with-dependencies.jar");
        String tmpDir = System.getProperty("java.io.tmpdir") + File.separator + "fshloafServer" + File.separator;
        File tmpDirFile = new File(tmpDir);
        tmpDirFile.mkdirs();
        tmpDirFile.deleteOnExit();
        String tmpJar = tmpDir + "itchat4j.jar";
        File tmpFile = new File(tmpJar);
        FileOutputStream fout = new FileOutputStream(tmpFile);
        try {
            IOUtils.copy(itchat4jJar, fout);
        } finally {
            IOUtils.closeQuietly(itchat4jJar);
            IOUtils.closeQuietly(fout);
        }
        URLClassLoader classloader = (URLClassLoader) Main.class.getClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
        add.setAccessible(true);
        add.invoke(classloader, new Object[]{tmpFile.toURI().toURL()});
        Boot.start();
    }

}
