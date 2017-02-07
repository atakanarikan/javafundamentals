package ee.ut.jf2016.sysdump;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.MessageLoggers;
import org.zeroturnaround.exec.ProcessExecutor;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class SystemDumpImpl implements SystemDump {

    private static final Logger log = LoggerFactory.getLogger(SystemDumpImpl.class);

    @Override
    public Info newInfo() {
        TreeMap systemProperties = new TreeMap();
        Properties prop = System.getProperties();
        Iterator sysPropIterator = prop.stringPropertyNames().iterator();
        String currentProperty;
        String procOutput;
        while (sysPropIterator.hasNext()) {
            currentProperty = sysPropIterator.next().toString();
            systemProperties.put(currentProperty, prop.getProperty(currentProperty));
        }
        TreeMap environmentVariables = new TreeMap(System.getenv());
        try {
            ProcessExecutor proc;
            if (SystemUtils.IS_OS_UNIX) {
                proc = new ProcessExecutor("uname", "-a");
            } else {
                proc = new ProcessExecutor("cmd", "/c", "ver");
            }
            procOutput = proc.readOutput(true).setMessageLogger(MessageLoggers.TRACE).execute().getOutput().getUTF8().trim();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new Info() {
            @Override
            public Map<String, String> getSystemEnvironment() {
                return environmentVariables;
            }

            @Override
            public Map<String, String> getSystemProperties() {
                return systemProperties;
            }

            @Override
            public String getSystemVersion() {
                return procOutput;
            }
        };
    }

    @Override
    public void writeXml(Info src, Path dest) throws Exception {
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (OutputStream out =
                     new BufferedOutputStream(Files.newOutputStream(dest))) {
            XMLStreamWriter writer = output.createXMLStreamWriter(out);
            writer.writeStartDocument();
            writer.writeStartElement("sysDump");
            writer.writeStartElement("systemEnvironment");
            for (String key : src.getSystemEnvironment().keySet()) {
                writer.writeStartElement("entry");
                writer.writeAttribute("key", key);
                writer.writeCharacters(src.getSystemEnvironment().get(key));
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeStartElement("systemProperties");
            for (String key : src.getSystemProperties().keySet()) {
                writer.writeStartElement("entry");
                writer.writeAttribute("key", key);
                writer.writeCharacters(src.getSystemProperties().get(key));
                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.writeStartElement("systemVersion");
            writer.writeCharacters(src.getSystemVersion());
            writer.writeEndDocument();
            writer.writeEndDocument();
            writer.close();
        }
    }

    @Override
    public void writeJson(Info src, Path dest) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> obj = new TreeMap<>();
        obj.put("systemEnvironment", src.getSystemEnvironment());
        obj.put("systemProperties", src.getSystemProperties());
        obj.put("systemVersion", src.getSystemVersion());
        PrintWriter writer = new PrintWriter(dest.toString(), "UTF-8");
        writer.print(gson.toJson(obj));
        writer.close();
    }

}
