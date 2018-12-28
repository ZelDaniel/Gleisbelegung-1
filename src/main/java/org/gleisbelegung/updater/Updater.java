package org.gleisbelegung.updater;

import org.gleisbelegung.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


/**
 * This is at the moment a not working Updater, but the basic concepts are working. There are many details left for implementing
 * <p>
 * The Update-Process at the moment is working as following, if the user approved the update:<br>
 * 1. copy the executed jar File in the same folder with the specified name. and start this file<br>
 * 2. Download the file and save it with the specified file name. After that prompt the user the downloading dialog from the updater jar<br>
 * 3. after the user click restart to the newly downloaded version
 */
public class Updater {

    private String updaterFileName = "Gleisbelegung_Updater.jar";

    /**
     * @// FIXME: 28.12.2018 should be set dynamiccaly according to new version name
     */
    private String newVersionFileName = "Gleisbelegung-0.0.2.jar";

    /**
     * Checks wether the executed jar is the real plugin or the updater
     * @return true if is plugin, false if is updater
     */
    public boolean isUpdateable() {
        File file = getJarFile();

        if (file != null && file.getName().endsWith(".jar")) {
            System.out.println("Updater: new version available");
            return true;
        } else {
            System.out.println(
                    "Updater: Unable to update, because the executed File is not a .jar File. Are you in execution mode?");
            return false;
        }
    }

    /**
     * @//FIXME not implemented
     * @return true if never version available on server, else return false
     */
    public boolean isNeverVersionAvailable() {
        return true;
    }

    /**
     * download the new version from the server and save it under the specified file name
     */
    public void downloadNewVersion() {
        try {
            URL website = new URL("http://www.website.com");
            InputStream in = website.openStream();

            File out = new File(getJarFile().getAbsolutePath()
                    .replace(getJarFile().getName(), "") + File.pathSeparator + newVersionFileName);
            Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * execute the new downloaded version and stop the current jar execution
     */
    public void startNewerVersion() {
        System.out.println("starting new version");
        try {
            Process proc = Runtime.getRuntime()
                    .exec("java -jar " + newVersionFileName);
            System.out.println("new version started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("stopping system");
        System.exit(1);
    }

    /**
     * check if the executed jar has the specified updater file name
     * @return
     */
    public boolean isUpdater() {
        try {
            String path = Plugin.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");

            if (decodedPath.contains(updaterFileName)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * copy the normal jar to the updater jar
     */
    public void copyFile() {
        File file = getJarFile();
        File updater = new File(
                file.getAbsolutePath().replace(file.getName(), "")
                        + updaterFileName);

        try {
            Files.copy(file.toPath(), updater.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return the current executed jar file
     */
    private File getJarFile() {
        try {
            String path = Plugin.class.getProtectionDomain().getCodeSource()
                    .getLocation().getPath();
            String decodedPath = URLDecoder.decode(path, "UTF-8");

            return new File(decodedPath);
        } catch (UnsupportedEncodingException e) {
            //e.printStackTrace();
            return null;
        }
    }

    /**
     * start the updater
     */
    public void startUpdater() {
        System.out.println("starting updater");
        try {
            Process proc =
                    Runtime.getRuntime().exec("java -jar " + updaterFileName);
            System.out.println("service started");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("stopping system");
        System.exit(1);
    }
}
