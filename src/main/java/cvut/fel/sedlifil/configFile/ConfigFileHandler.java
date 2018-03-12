package cvut.fel.sedlifil.configFile;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import cvut.fel.sedlifil.parser.ParserClass;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigFileHandler implements IConfigFileHandler {
    private String locationOfDirectory;
    private org.slf4j.Logger logger;



    private List<ClassWithMethods> classWithMethodsList;

    public ConfigFileHandler() {
        locationOfDirectory = ConfigFileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        locationOfDirectory = locationOfDirectory.substring(0, locationOfDirectory.indexOf(ParserClass.JAVA_TARGET)-1);
        init();

    }

    public ConfigFileHandler(String locationOfDirectory) {
        this.locationOfDirectory = locationOfDirectory;
        init();
    }

    private void init(){
        classWithMethodsList = new ArrayList<>();
        logger = LoggerFactory.getLogger(ConfigFileHandler.class);
    }



    @Override
    public void generateConfigFile(Map<String, CompilationUnit> listBlock1,
                                   Map<String, CompilationUnit> listBlock2,
                                   Map<String, CompilationUnit> listBlock3) {
        logger.info("Generating of configuration file...");
        convertToClass(listBlock1, ParserClass.BLOCK1_);
        convertToClass(listBlock2, ParserClass.BLOCK2_);
        convertToClass(listBlock3, ParserClass.BLOCK3_);
        logger.info("Configuration file is generated.");
        printAllClasses();


    }

    private void convertToClass(Map<String, CompilationUnit> listBlock, String block){
        listBlock.forEach((K, cu) -> {
            
            List<String> methodNames = new ArrayList<>();

            VoidVisitor<List<String>> methodVisitor = new MethodNamePrinter();
            methodVisitor.visit(cu, methodNames);
            ClassWithMethods classWithMethods = new ClassWithMethods(K, methodNames, block);

            classWithMethodsList.add(classWithMethods);
        });

    }

    private void printAllClasses(){
        classWithMethodsList.forEach(System.out::println);
    }

    /* public void printAllClasses() {
        List<String> results = new ArrayList<>();


        File[] files = new File(filePath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".java");
            }
        });

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }

        System.out.println("Size of all finded classes:" + results.size());


        List<ClassWithMethods> listOfClass = results.stream()
                .map(y -> {
                    CompilationUnit cu = null;
                    try {
                        cu = JavaParser.parse(new FileInputStream(filePath.concat(FILE_DELIMITER + y)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    List<String> methodNames = new ArrayList<>();
                    VoidVisitor<List<String>> methodVisitor = new MethodNamePrinter();
                    methodVisitor.visit(cu, methodNames);

                    return new ClassWithMethods(y, filePath, methodNames);
                }).collect(Collectors.toList());

        listOfClass.forEach(System.out::println);
    } */

    private static class MethodNamePrinter extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodDeclaration md, List<String> collector) {
            super.visit(md, collector);
            collector.add(md.getNameAsString());
        }
    }
}
