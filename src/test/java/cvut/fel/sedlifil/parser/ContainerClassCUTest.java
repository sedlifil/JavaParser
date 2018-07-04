package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.body.VariableDeclarator;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ContainerClassCUTest {
    private ContainerClassCU containerClassCU;
    private ContainerClassCU containerClassCU1;
    private String path1;
    private String FD = ParserClass.FILE_DELIMITER;


    @Before
    public void setUp() throws Exception {
        path1 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/LibraryRegistrationRESTController.java")
                .toAbsolutePath().toString();
        String path2 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/JaxRsActivator.java")
                .toAbsolutePath().toString();

        containerClassCU = new ContainerClassCU(path1);
        containerClassCU1 = new ContainerClassCU(path2);
    }

    @Test
    public void getImportsFromClass() {
        List<String> list = new ArrayList<>();
        list.add("java" + FD + "util" + FD + "List");
        list.add("javax" + FD + "enterprise" + FD + "context" + FD + "RequestScoped");
        list.add("javax" + FD + "inject" + FD + "Inject");
        list.add("javax" + FD + "json" + FD + "Json");
        list.add("javax" + FD + "json" + FD + "JsonArray");
        list.add("javax" + FD + "json" + FD + "JsonArrayBuilder");
        list.add("javax" + FD + "json" + FD + "JsonObject");
        list.add("javax" + FD + "validation" + FD + "Valid");
        list.add("javax" + FD + "ws" + FD + "rs" + FD + "*");
        list.add("javax" + FD + "ws" + FD + "rs" + FD + "core" + FD + "MediaType");
        list.add("javax" + FD + "ws" + FD + "rs" + FD + "core" + FD + "Response");
        list.add("org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "block" + FD + "Block");
        list.add("org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "model" + FD + "Library");
        list.add("org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "service" + FD + "LibraryRegistration");
        list.forEach(l -> assertTrue("getImportsImplementsExtendedFromClass list does not contain " + l, containerClassCU.getImportsFromClass().contains(l)));
        assertEquals("Wrong size getImportsImplementsExtendedFromClass list!!!", list.size(), containerClassCU.getImportsFromClass().size());

    }

    @Test
    public void getImplementsExtendedFromClass() throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        ContainerClassCU classCU = new ContainerClassCU(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/AuthorDAOImpl.java").toAbsolutePath().toString());
        list.add("AuthorDAO");
        list.add("GenericDAO");

        list.forEach(l -> assertTrue("getImplementsExtendedFromClass list does not contain " + l, classCU.getImplementsExtendedFromClass().contains(l)));
        assertEquals("Wrong size getImplementsExtendedFromClass list!!!", list.size(), classCU.getImplementsExtendedFromClass().size());
    }

    @Test
    public void getNameClass() {
        assertEquals("Name is wrong", "LibraryRegistrationRESTController.java", containerClassCU.getNameClass());
    }

    @Test
    public void getNameClassWithAbsPath() {
        assertEquals("Name is wrong", path1, containerClassCU.getNameClassWithAbsPath());
    }

    @Test
    public void getFieldsFromClassList() {
        assertEquals("Size of fields should be 1.", 1, containerClassCU.getFieldsFromClassList().size());
        VariableDeclarator v = new VariableDeclarator();
        assertEquals("Name of field should be libraryRegistration!!!", "libraryRegistration", containerClassCU.getFieldsFromClassList().get(0).getName().toString());
    }

    @Test
    public void getImportsImplementsExtendedFromClass() throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        ContainerClassCU classCU = new ContainerClassCU(Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/dao/AuthorDAOImpl.java").toAbsolutePath().toString());
        list.add("org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "model" + FD + "Author");
        list.add("org" + FD + "jboss" + FD + "as" + FD + "quickstarts" + FD + "kitchensink" + FD + "block" + FD + "Block");
        list.add("AuthorDAO");
        list.add("GenericDAO");

        list.forEach(l -> assertTrue("getImportsImplementsExtendedFromClass list does not contain " + l, classCU.getImportsImplementsExtendedFromClass().contains(l)));
        assertEquals("Wrong size getImportsImplementsExtendedFromClass list!!!", list.size(), classCU.getImportsImplementsExtendedFromClass().size());
    }

    @Test
    public void getMethodDeclarations() {
        List<String> list = new ArrayList<>();
        list.add("findAll");
        list.add("findById");
        list.add("save");
        assertEquals("Wrong size of methods", list.size(), containerClassCU.getMethodDeclarations().size());
        list.forEach(l -> assertEquals("Name of method " + l + " was not found", 1,
                containerClassCU.getMethodDeclarations().stream().filter(m -> m.getNameAsString().equals(l)).count()));
    }

    @Test
    public void getClassAnnotations() {
        List<String> list = new ArrayList<>();
        list.add("Path");
        list.add("RequestScoped");
        list.add("Produces");
        list.add("Consumes");
        list.add("Block");
        assertEquals("Wrong size of class annotation", list.size(), containerClassCU.getClassAnnotations().size());
        list.forEach(l -> assertEquals("Name of annotation " + l + " was not found", 1,
                containerClassCU.getClassAnnotations().stream().filter(m -> m.getNameAsString().equals(l)).count()));
    }

    @Test
    public void getBelongToBlocks() {
        assertEquals("Wrong block annotation!", "\"key2\"", containerClassCU.getBelongToBlocks());
        assertEquals("Wrong block annotation!", "{ \"key2\", \"key3\" }", containerClassCU1.getBelongToBlocks());
    }
}