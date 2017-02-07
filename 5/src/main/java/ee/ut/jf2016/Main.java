package ee.ut.jf2016;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ee.ut.jf2016.sysdump.Info;
import ee.ut.jf2016.sysdump.SystemDump;
import ee.ut.jf2016.sysdump.SystemDumpImpl;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        SystemDump dump = new SystemDumpImpl();
        log.info("Creating info");
        Info info = dump.newInfo();
        Path xml = Files.createTempFile("sysdump", ".xml");
        Path json = Files.createTempFile("sysdump", ".json");
        log.info("Writing into {}", json);
        dump.writeJson(info, json);
        log.info("Writing into {}", xml);
        dump.writeXml(info, xml);
        log.info("Finished");
    }

}
