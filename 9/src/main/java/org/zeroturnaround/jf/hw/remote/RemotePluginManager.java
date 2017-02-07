package org.zeroturnaround.jf.hw.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.zeroturnaround.jf.hw.Plugin;

public class RemotePluginManager {

    public static String[] findAllPlugins() {
        return findAllPluginInfos().keySet().toArray(new String[]{});
    }

    private static Map<String, String> findAllPluginInfos() {
        return new HashMap<String, String>() {
            {
                put("NomNomNomPlugin", "https://raw.github.com/zeroturnaround/jf-hw-classloaders/master/plugins-remote/NomNomNomPlugin/README.properties");
                put("ChickenPlugin", "https://raw.github.com/zeroturnaround/jf-hw-classloaders/master/plugins-remote/ChickenPlugin/README.properties");
                put("HeadAndShouldersPlugin", "https://raw.github.com/zeroturnaround/jf-hw-classloaders/master/plugins-remote/HeadAndShouldersPlugin/README.properties");
            }
        };
    }

    public static Plugin getPluginInstance(String pluginName) throws IllegalAccessException, InstantiationException, IOException {
        Map<String, String> allPlugins = findAllPluginInfos();
        String url = allPlugins.get(pluginName);
        ClassLoader cl = new RemotePluginLoader(pluginName);
        Class c;
        try {
            c = cl.loadClass(url);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return (Plugin) c.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
