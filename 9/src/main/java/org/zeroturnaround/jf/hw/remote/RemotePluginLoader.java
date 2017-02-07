package org.zeroturnaround.jf.hw.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RemotePluginLoader extends ClassLoader {
    private static HashMap<String, String> getLogoURLs() {
        return new HashMap<String, String>() {
            {
                put("NomNomNomPlugin", "https://github.com/JavaFundamentalsZT/jf-hw-classloaders/blob/master/plugins-remote/NomNomNomPlugin/NomNomNomPlugin.png?raw=true");
                put("ChickenPlugin", "https://github.com/JavaFundamentalsZT/jf-hw-classloaders/blob/master/plugins-remote/ChickenPlugin/ChickenPlugin.png?raw=true");
                put("HeadAndShouldersPlugin", "https://github.com/JavaFundamentalsZT/jf-hw-classloaders/blob/master/plugins-remote/HeadAndShouldersPlugin/HeadAndShouldersPlugin.png?raw=true");
            }
        };
    }
    private static byte[] getBytes(String fileURL) {
        URL url = null;
        try {
            url = new URL(fileURL);
        } catch (MalformedURLException e) {
           // e.printStackTrace();
        }
        byte[] data = null;
        try (InputStream is = url.openStream();
             ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = is.read(buffer)) >= 0) os.write(buffer, 0, n);
            data = os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static HashMap<String, String> classInfo (String fileURL) {
        HashMap<String, String> hm = new HashMap<>();
        try {
            URL url = new URL(fileURL);
            Scanner s = new Scanner(url.openStream());
            while (s.hasNext()) {
                String[] temp = s.nextLine().split(":");
                hm.put(temp[0], temp[1]);
            }
        } catch (IOException ignored) {
        }
        return hm;
    }
    public RemotePluginLoader(String pluginName) {
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        HashMap<String, String> allURLS = getLogoURLs();
        HashMap<String, String> classInfo = classInfo(name);
        Class clazz;
        try {
            clazz = super.loadClass(name);
            return clazz;
        }
        catch (ClassNotFoundException e) {
            byte[] logo = getBytes(allURLS.get(classInfo.get("name")));
            byte[] javaClass = new byte[0];
            for (int i = 0; i < logo.length - 3; i++) {
                if (logo[i] == -54 && logo[i + 1] == -2 && logo[i + 2] == -70 && logo[i + 3] == -66) { // CAFEBABE
                    javaClass = Arrays.copyOfRange(logo, i, logo.length);
                    break;
                }
            }
            return defineClass(classInfo.get("class"), javaClass, 0, javaClass.length);
        }
    }

}
