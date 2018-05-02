package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class MethodParserTest {
    private MethodParser methodParser;
    private ContainerClassCU container1;
    private ContainerClassCU container2;
    private Map<String, ContainerClassCU> map1;
    private Map<String, ContainerClassCU> map2;

    @Before
    public void setUp() throws Exception {
        BasicConfigurator.configure();

        String path1 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/AuthorRegistrationRESTController.java").toAbsolutePath().toString();
        String path2 = Paths.get("./src/testFiles/BookSystem/src/main/java/org/jboss/as/quickstarts/kitchensink/rest/LibraryRegistrationRESTController.java").toAbsolutePath().toString();
        container1 = new ContainerClassCU(path1);
        container2 = new ContainerClassCU(path2);
        map1 = new HashMap<>();
        map1.put(path1, container1);
        map2 = new HashMap<>();
        map2.put(path2, container2);
        methodParser = new MethodParser();
    }

    @Test
    public void categorizeMethodsForBlock1() {
        List<String> list1 = new ArrayList<>();
        list1.add("findAll");
        list1.add("save");
        list1.add("findById");

        List<String> list2 = new ArrayList<>();
        list2.add("findAll");
        list2.add("save");
        list2.add("findById");

        list1.forEach(y -> assertTrue("container1 neobsahuje metodu: " + y, container1.getMethodNames()
                .stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toList()).contains(y)));
        assertEquals("Lists are not equal", list1.size(), container1.getMethodNames().size());

        list2.forEach(y -> assertTrue("container2 neobsahuje metodu: " + y, container2.getMethodNames()
                .stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toList()).contains(y)));
        assertEquals("Lists are not equal", list2.size(), container2.getMethodNames().size());


        methodParser.categorizeMethods(map1, ParserClass.BLOCK1_);
        methodParser.categorizeMethods(map2, ParserClass.BLOCK2_);

        list1.remove("findById");
        list1.forEach(y -> assertTrue("container1 neobsahuje metodu: " + y, container1.getMethodNames()
                .stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toList()).contains(y)));
        assertEquals("Lists are not equal", list1.size(), container1.getMethodNames().size());

        list2.remove("findById");
        list2.remove("save");

        list2.forEach(y -> assertTrue("container2 neobsahuje metodu: " + y, container2.getMethodNames()
                .stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toList()).contains(y)));
        assertEquals("Lists are not equal", list2.size(), container2.getMethodNames().size());
    }
}