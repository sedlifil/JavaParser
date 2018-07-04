package cvut.fel.sedlifil.parser;

import cvut.fel.sedlifil.configHandler.IConfigFileHandler;
import cvut.fel.sedlifil.configHandler.RESTConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.FileHandlerParser;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ParserClassTest {

    @Test
    public void divideIntoBlocksTest() {
        List<String> list = new ArrayList<>();
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Author.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Library.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Book.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Publisher.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/AuthorDAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/PublisherDAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/BookDAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/LibraryDAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/AuthorDAOImpl.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/PublisherDAOImpl.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/BookDAOImpl.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/LibraryDAOImpl.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/DAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/GenericDAO.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/AuthorRegistrationRESTController.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/serviceImpl/AuthorRegistrationImpl.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/service/AuthorRegistration.java").toAbsolutePath().toString());
        list.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/block/Block.java").toAbsolutePath().toString());


        List<String> list2 = new ArrayList<>();
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Author.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Library.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Book.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/model/Publisher.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/LibraryDAO.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/LibraryDAOImpl.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/DAO.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/GenericDAO.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/LibraryRegistrationRESTController.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/JaxRsActivator.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/CorsFilter.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/serviceImpl/LibraryRegistrationImpl.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/service/LibraryRegistration.java").toAbsolutePath().toString());
        list2.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/block/Block.java").toAbsolutePath().toString());

        List<String> list3 = new ArrayList<>();
        list3.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/JaxRsActivator.java").toAbsolutePath().toString());
        list3.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/CorsFilter.java").toAbsolutePath().toString());
        list3.add(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/block/Block.java").toAbsolutePath().toString());

        BasicConfigurator.configure();

        IConfigFileHandler configFileHandler = mock(RESTConfigFileHandler.class);
        IFileHandlerParser fileHandlerParser = mock(FileHandlerParser.class);
        String[] files = {};
        ParserClass parserClass = new ParserClass(fileHandlerParser, configFileHandler, "./src/testFiles", files);
        parserClass.divideIntoBlocks(false, false);


        list.forEach(y -> assertTrue("Cu Block1 neobsahuje " + y,parserClass.getContainerClassCuMapBlock1().containsKey(y)));

        assertEquals(list.size(), parserClass.getContainerClassCuMapBlock1().size());

        list2.forEach(y -> assertTrue("Cu Block2 neobsahuje " + y,parserClass.getContainerClassCuMapBlock2().containsKey(y)));

        assertEquals(list2.size(), parserClass.getContainerClassCuMapBlock2().size());

        list3.forEach(y -> assertTrue("Cu Block3 neobsahuje " + y,parserClass.getContainerClassCuMapBlock3().containsKey(y)));

        assertEquals(list3.size(), parserClass.getContainerClassCuMapBlock3().size());

        assertEquals("List with other files except of *.java should have only one file - pom.xml",
                1, parserClass.getFilesToAllBlocks().size());

        parserClass.getFilesToAllBlocks().forEach(
                file -> assertEquals("List with other files except of *.java should have only one file - pom.xml",
                        Paths.get("./src/testFiles/BookSystem/pom.xml").toAbsolutePath().toString()

                                , file ));

    }
}