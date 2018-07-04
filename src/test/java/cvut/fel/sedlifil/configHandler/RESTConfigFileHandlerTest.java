package cvut.fel.sedlifil.configHandler;

import cvut.fel.sedlifil.parser.ContainerClassCU;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RESTConfigFileHandlerTest {

    private Map<String, ContainerClassCU> block1;
    private Map<String, ContainerClassCU> block2;
    private Map<String, ContainerClassCU> block3;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        block1 = new HashMap<>();
        block2 = new HashMap<>();
        block3 = new HashMap<>();
        String path1 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/AuthorRegistrationRESTController.java").toAbsolutePath().toString();
        String path2 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/LibraryRegistrationRESTController.java").toAbsolutePath().toString();
        String path3 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/BookRegistrationRESTController.java").toAbsolutePath().toString();
        String path4 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/AuthorRegistrationRESTController.java").toAbsolutePath().toString();
        String path5 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/CorsFilter.java").toAbsolutePath().toString();

        ContainerClassCU classCU1 = new ContainerClassCU(path1);
        ContainerClassCU classCU2 = new ContainerClassCU(path2);
        ContainerClassCU classCU3 = new ContainerClassCU(path3);
        ContainerClassCU classCU4 = new ContainerClassCU(path4);
        ContainerClassCU classCU5 = new ContainerClassCU(path5);
        block1.put(path1, classCU1);
        block1.put(path2, classCU2);
        block1.put(path3, classCU3);
        block2.put(path2, classCU2);
        block2.put(path4, classCU4);
        block3.put(path1, classCU1);
        block3.put(path3, classCU3);
        block3.put(path5, classCU5);
    }

    @Test
    public void generateConfigFile() throws IOException {
        IConfigFileHandler configFileHandler = new RESTConfigFileHandler("./src/testFiles/generatedTestFiles");
        configFileHandler.generateConfigFile(block1, block2, block3);

        byte[] dataExpected = Files.readAllBytes(Paths.get("./src/testFiles/configRESTfileExpected.json").toAbsolutePath());
        byte[] dataGenerated = Files.readAllBytes(Paths.get("./src/testFiles/generatedTestFiles/config.json").toAbsolutePath());

        assertTrue("Generated config files should be same.", Arrays.equals(dataExpected, dataGenerated));
    }

    @After
    public void tearDown() throws Exception {
        File file = new File("./src/testFiles/generatedTestFiles/config.json");

        if (!file.delete()) {
            fail("Can not delete generated config.json file!!!");
        }
    }
}