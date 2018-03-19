package cvut.fel.sedlifil.configFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import cvut.fel.sedlifil.parser.MethodParser;
import cvut.fel.sedlifil.parser.ParserClass;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RESTConfigFileHandler implements IConfigFileHandler {
    private String locationOfDirectory;
    private org.slf4j.Logger logger;
    private List<ClassWithMethods> classWithMethodsList;
    private static String CONFIG_FILE_NAME = "config.json";

    private enum HTTP_METHODS {
        HEAD, GET, POST, PUT, DELETE, OPTIONS
    }

    public RESTConfigFileHandler() {
        locationOfDirectory = RESTConfigFileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        locationOfDirectory = locationOfDirectory.substring(0, locationOfDirectory.indexOf(ParserClass.JAVA_TARGET) - 1);
        init();

    }

    public RESTConfigFileHandler(String locationOfDirectory) {
        this.locationOfDirectory = locationOfDirectory;
        init();
    }

    private void init() {
        classWithMethodsList = new ArrayList<>();
        logger = LoggerFactory.getLogger(RESTConfigFileHandler.class);
    }

    @Override
    public void generateConfigFile(Map<String, CompilationUnit> listBlock1,
                                   Map<String, CompilationUnit> listBlock2,
                                   Map<String, CompilationUnit> listBlock3) {
        logger.info("generating of configuration file...");
        convertToClass(listBlock1, ParserClass.BLOCK1_);
        convertToClass(listBlock2, ParserClass.BLOCK2_);
        convertToClass(listBlock3, ParserClass.BLOCK3_);
        saveToFile();
    }

    private void convertToClass(Map<String, CompilationUnit> listBlock, String block) {
        listBlock.forEach((K, cu) -> {

            List<AnnotationExpr> annotationClassList = new ArrayList<>();
            VoidVisitor<List<AnnotationExpr>> annotationClassVisitor = new ParserClass.AnnotationClassVisitor();
            annotationClassVisitor.visit(cu, annotationClassList);

            for (AnnotationExpr ann : annotationClassList) {
                if (ann.getNameAsString().equals(ParserClass.BLOCK_)) {
                    parseMethod(cu, K, block);
                }
            }
        });
    }

    private void parseMethod(CompilationUnit cu, String pathName, String block) {
        List<String> methodNames = new ArrayList<>();

        VoidVisitor<List<String>> methodVisitor = new MethodNamePrinter();
        methodVisitor.visit(cu, methodNames);
        ClassWithMethods classWithMethods = new ClassWithMethods(pathName, methodNames, block);

        classWithMethodsList.add(classWithMethods);
    }

    private void saveToFile() {
        //StringBuilder data = new StringBuilder();

        //for (ClassWithMethods c : classWithMethodsList) {
        //    data.append(c.toJson()).append(",");
        //}
        ObjectMapper mapper = new ObjectMapper();
        String data = "";
        try {

            data = mapper.writeValueAsString(classWithMethodsList.stream().filter(_class -> !_class.getMethodsList().isEmpty()).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Path savingFile = Paths.get(locationOfDirectory + ParserClass.FILE_DELIMITER + CONFIG_FILE_NAME);
        byte[] fileArray;
        fileArray = data.getBytes(StandardCharsets.UTF_8);
        try {
            Files.createFile(savingFile);
            Files.write(savingFile, fileArray);
            logger.info("configuration file is located in " + savingFile.toString() + ".");

        } catch (FileAlreadyExistsException e) {
            logger.error("configuration file " + savingFile.toString() + " already exists, could NOT be generated again!!!");
        }catch (IOException e) {
            logger.error("Error with writing to file " + savingFile.toString() + "!!!");
        }
    }

    private void printAllClasses() {
        classWithMethodsList.stream()
                .filter(_class -> !_class.getMethodsList().isEmpty())
                .forEach(y -> System.out.println(y.toJson()));
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

            if (decideMethod(md)) {
                collector.add(md.getNameAsString());
            }
        }

        private boolean decideMethod(MethodDeclaration methodDeclaration) {
            List<AnnotationExpr> annotationClassList = new ArrayList<>();
            VoidVisitor<List<AnnotationExpr>> annotationClassVisitor = new MethodParser.AnnotationMethodVisitor();
            annotationClassVisitor.visit(methodDeclaration, annotationClassList);
            for (AnnotationExpr ann : annotationClassList) {
                for (HTTP_METHODS method : HTTP_METHODS.values()) {
                    if (ann.getNameAsString().equals(method.toString())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }


}
