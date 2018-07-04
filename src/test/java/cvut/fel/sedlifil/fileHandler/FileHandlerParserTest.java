package cvut.fel.sedlifil.fileHandler;

import cvut.fel.sedlifil.parser.ContainerClassCU;
import cvut.fel.sedlifil.parser.ParserClass;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


public class FileHandlerParserTest {

    private Map<String, ContainerClassCU> block1;
    private Map<String, ContainerClassCU> block2;
    private Map<String, ContainerClassCU> block3;
    private List<String> list;
    private List<String> list1;
    private List<String> list2;
    private List<String> list3;
    private String appDirName;
    private String FD = ParserClass.FILE_DELIMITER;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        block1 = new HashMap<>();
        block2 = new HashMap<>();
        block3 = new HashMap<>();
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();

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

        list.add(Paths.get("./src/testFiles/BookSystem/pom.xml").toAbsolutePath().toString());
    }

    @After
    public void tearDown() throws Exception {
        Files.walk(Paths.get("./src/testFiles/generatedTestFiles/"))
                .map(Path::toFile)
                .sorted((o1, o2) -> -o1.compareTo(o2))
                .forEach(File::delete);
        Files.createDirectories(Paths.get("./src/testFiles/generatedTestFiles/"));
    }

    @Test
    public void saveAppToFile() throws IOException {
        Path generatedFilePath = Paths.get("./src/testFiles/generatedTestFiles");
        IFileHandlerParser fileHandlerParser = new FileHandlerParser("./src/testFiles/generatedTestFiles");
        fileHandlerParser.saveAppToFile(block1, block2, block3, list);

        File[] files = new File("./src/testFiles/generatedTestFiles").listFiles();

        if (files == null) {
            fail("generatedTestFiles is missing!!!");
        }

        Optional<File> appDirFile = Arrays.stream(files).filter(file -> file.isDirectory() && file.getName().startsWith(".")).findFirst();
        if (appDirFile.isPresent()) {
            appDirName = appDirFile.get().getName();
            fillInLists();

            compareList("key1", list1);
            compareList("key2", list2);
            compareList("key3", list3);


        }


    }

    private void compareList(String key, List<String> list) throws IOException {
        List<String> generatedListOfBlock = Files.walk(Paths.get("./src/testFiles/generatedTestFiles/" + appDirName + "/" + key))
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .collect(Collectors.toList());

        list.forEach(expected -> assertTrue("generatedList does not contain " + expected, generatedListOfBlock.contains(expected)));
        assertEquals("Lists are not same", list.size(), generatedListOfBlock.size());

    }


    private void fillInLists() {
        list1.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key1" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "pom.xml");
        list1.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key1" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "LibraryRegistrationRESTController.java");
        list1.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key1" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "BookRegistrationRESTController.java");
        list1.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key1" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "AuthorRegistrationRESTController.java");

        list2.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key2" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "pom.xml");
        list2.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key2" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "LibraryRegistrationRESTController.java");
        list2.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key2" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "AuthorRegistrationRESTController.java");

        list3.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key3" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "pom.xml");
        list3.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key3" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "BookRegistrationRESTController.java");
        list3.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key3" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "CorsFilter.java");
        list3.add("." + FD + "src" + FD + "testFiles" + FD + "generatedTestFiles" + FD + "" + appDirName + "" + FD + "key3" + FD + "src" + FD + "testFiles" + FD + "BookSystem" + FD + "src" + FD + "main" + FD + "java" + FD + "org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "rest" + FD + "AuthorRegistrationRESTController.java");


    }
}