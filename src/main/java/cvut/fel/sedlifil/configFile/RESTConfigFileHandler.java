package cvut.fel.sedlifil.configFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import cvut.fel.sedlifil.parser.ContainerClassCU;
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
    public void generateConfigFile(Map<String, ContainerClassCU> listBlock1,
                                   Map<String, ContainerClassCU> listBlock2,
                                   Map<String, ContainerClassCU> listBlock3) {
        logger.info("generating of configuration file...");
        convertToClass(listBlock1, ParserClass.BLOCK1_);
        convertToClass(listBlock2, ParserClass.BLOCK2_);
        convertToClass(listBlock3, ParserClass.BLOCK3_);
        saveToFile();
    }

    /**
     * method for those classes which have annotation @Block to convert into ClassWithMethods container
     *
     * @param listBlock list of classes
     * @param block     belongs to this block
     */
    private void convertToClass(Map<String, ContainerClassCU> listBlock, String block) {
        listBlock.entrySet()
                .stream()
                .filter(map -> map.getValue().getBelongToBlocks().contains(block))
                .forEach(map -> parseMethod(map.getKey(), map.getValue(), block));
    }

    /**
     * method to save classes into list in format: name, methods, block
     *
     * @param pathName         name with absolute path of class
     * @param containerClassCU class
     * @param block            belongs to this block
     */
    private void parseMethod(String pathName, ContainerClassCU containerClassCU, String block) {
        List<MethodDeclaration> methodDeclarationList = containerClassCU.getMethodNames();
        List<String> methodNames = methodDeclarationList
                .stream()
                .filter(this::decideMethod)
                .map(MethodDeclaration::getNameAsString)
                .collect(Collectors.toList());
        ClassWithMethods classWithMethods = new ClassWithMethods(pathName, methodNames, block);
        classWithMethodsList.add(classWithMethods);
    }

    private void saveToFile() {
        ObjectMapper mapper = new ObjectMapper();
        String data = "";
        try {

            data = mapper.writeValueAsString(classWithMethodsList.stream()
                    .filter(_class -> !_class.getMethodsList().isEmpty())
                    .collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            logger.error("error with converting list of classes into json format!!!");
            logger.error(e.toString());
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
        } catch (IOException e) {
            logger.error("Error with writing to file " + savingFile.toString() + "!!!");
        }
    }

    /**
     * method to decide if method of class has REST HTTP annotation
     * @param methodDeclaration method of class
     * @return decision if method of class is REST HTTP method
     */
    private boolean decideMethod(MethodDeclaration methodDeclaration) {
        List<AnnotationExpr> annotationClassList = new ArrayList<>();
        VoidVisitor<List<AnnotationExpr>> annotationClassVisitor = new AnnotationMethodVisitor();
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

    /**
     * Annotation method visitor
     * visitor fill in into list of AnnotationExpr all annotation of given method
     */
    private static class AnnotationMethodVisitor extends VoidVisitorAdapter<List<AnnotationExpr>> {
        @Override
        public void visit(MethodDeclaration n, List<AnnotationExpr> collector) {
            super.visit(n, collector);
            collector.addAll(n.getAnnotations());
        }
    }
}
