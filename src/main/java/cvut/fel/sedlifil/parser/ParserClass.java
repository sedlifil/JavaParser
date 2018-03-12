package cvut.fel.sedlifil.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.imports.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import cvut.fel.sedlifil.configFile.ClassWithMethods;
import cvut.fel.sedlifil.configFile.IConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by filip on 01.11.17.
 */
public class ParserClass {

    private final String filePath;
    private final IFileHandlerParser fileHandlerParser;
    private final IConfigFileHandler configFileHandler;

    private Map<String, CompilationUnit> classPathWithCuMap;
    private Map<String, CompilationUnit> classPathWithCuMapBlock1;
    private Map<String, CompilationUnit> classPathWithCuMapBlock2;
    private Map<String, CompilationUnit> classPathWithCuMapBlock3;
    private List<String> filesToAllBlocks;
    private boolean flagUnCategorized = true;
    public static final String BLOCK_ = "Block";
    public static final String BLOCK1_ = "key1";
    public static final String BLOCK2_ = "key2";
    public static final String BLOCK3_ = "key3";
    public static final String FILE_DELIMITER = File.separator;
    private static final String JAVA_SUFFIX = ".java";
    private static final String PomXML = "pom.xml";
    public static final String JAVA_SOURCE  = ParserClass.FILE_DELIMITER + "src";
    public static final String JAVA_TARGET  = "target";

    private Logger logger;

    public ParserClass(IFileHandlerParser fileHandlerParser, IConfigFileHandler configFileHandler, String filePath) {
        this.fileHandlerParser = fileHandlerParser;
        this.configFileHandler = configFileHandler;
        this.filePath = filePath;
        classPathWithCuMap = new HashMap<>();
        classPathWithCuMapBlock1 = new HashMap<>();
        classPathWithCuMapBlock2 = new HashMap<>();
        classPathWithCuMapBlock3 = new HashMap<>();
        filesToAllBlocks = new ArrayList<>();
        logger = LoggerFactory.getLogger(ParserClass.class);
    }

    public void divideIntoBlocks() {
        findAllClasses();
        splitToBlocks();

        MethodParser methodParser = new MethodParser();
        methodParser.categorizeMethods(classPathWithCuMapBlock1, BLOCK1_);
        methodParser.categorizeMethods(classPathWithCuMapBlock2, BLOCK2_);
        methodParser.categorizeMethods(classPathWithCuMapBlock3, BLOCK3_);

        fileHandlerParser.saveAppToFile(classPathWithCuMapBlock1,
                classPathWithCuMapBlock2,
                classPathWithCuMapBlock3,
                filesToAllBlocks);

        configFileHandler.generateConfigFile(classPathWithCuMapBlock1,
                classPathWithCuMapBlock2,
                classPathWithCuMapBlock3);

    }

    private void findAllClasses() {
        findAllClasses(filePath);
    }

    /**
     * Find all files in given directory path and theirs subdirectories
     *
     * @param path of root/first directory
     */
    private void findAllClasses(String path) {
        //
        List<String> classNameList = new ArrayList<>();
        List<String> directories = new ArrayList<>();

        Path absPath = Paths.get(path).toAbsolutePath();
        File[] files = new File(absPath.toString()).listFiles();

        if (files == null){
            logger.info("There is no directory or file in this path.");
            logger.info("JavaParser exits.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(JAVA_SUFFIX)) {
                classNameList.add(file.getName());
            } else if (file.isDirectory()) {
                directories.add(file.getName());
            } else if (file.getName().equals(PomXML)) {
                //System.out.println("POM " + absPath.toString().concat(FILE_DELIMITER + file.getName()));
                filesToAllBlocks.add(absPath.toString().concat(FILE_DELIMITER + file.getName()));
            }
        }
        classNameList.forEach(y -> saveClass(absPath.toString().concat(FILE_DELIMITER + y)));

        /* recursion for founded directories*/
        for (String s : directories) {
            findAllClasses(path.concat(FILE_DELIMITER + s));
        }
    }

    /**
     * save found classes and split into 3 set of blocks depending on annotation
     *
     * @param path
     */
    private void saveClass(String path) {
        CompilationUnit cu;
        try {
            cu = JavaParser.parse(new FileInputStream(path));

            //zavolani tridy ClassVisitor a ulozeni vsech anotaci do listu
            List<AnnotationExpr> annotationClassList = new ArrayList<>();
            VoidVisitor<List<AnnotationExpr>> annotationClassVisitor = new AnnotationClassVisitor();
            annotationClassVisitor.visit(cu, annotationClassList);

            boolean flagDeepCopy;
            for (AnnotationExpr ann : annotationClassList) {
                if (ann.getNameAsString().equals(BLOCK_)) {
                    if (!(ann instanceof SingleMemberAnnotationExpr)) {
                        continue;
                    }
                    flagDeepCopy = false;
                    SingleMemberAnnotationExpr nax = (SingleMemberAnnotationExpr) ann;
                    //rozhodnuti kam dana trida poputuje
                    if (nax.getMemberValue().toString().contains(BLOCK1_)) {
                        classPathWithCuMapBlock1.put(path, cu);
                        flagDeepCopy = true;
                    }
                    if (nax.getMemberValue().toString().contains(BLOCK2_)) {
                        if(flagDeepCopy){
                            CompilationUnit cuCopy = JavaParser.parse(new FileInputStream(path));
                            classPathWithCuMapBlock2.put(path, cuCopy);
                        }else {
                            classPathWithCuMapBlock2.put(path, cu);
                            flagDeepCopy = true;
                        }

                    }
                    if (nax.getMemberValue().toString().contains(BLOCK3_)) {
                        if(flagDeepCopy){
                            CompilationUnit cuCopy = JavaParser.parse(new FileInputStream(path));
                            classPathWithCuMapBlock3.put(path, cuCopy);
                        }else {
                            classPathWithCuMapBlock3.put(path, cu);
                        }
                    }
                }

            }
            classPathWithCuMap.put(path, cu);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * mothod which, for every xxx finds all imports and calls parse methods
     */
    private void splitToBlocks() {
        Map<String, CompilationUnit> classBlockList = new HashMap<>();

        classPathWithCuMapBlock1.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(K, V, BLOCK1_));

        classBlockList.clear();
        classPathWithCuMapBlock2.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(K, V, BLOCK2_));

        classBlockList.clear();
        classPathWithCuMapBlock3.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(K, V, BLOCK3_));

        while (flagUnCategorized) {
            flagUnCategorized = false;
            classPathWithCuMap.forEach(this::parseImportsFromUncategorizedClass);
        }

    }

    /**
     * method to find if class is needed to be placed into some block
     *
     * @param classNameWithPath
     * @param cu
     */
    private void parseImportsFromUncategorizedClass(String classNameWithPath, CompilationUnit cu) {
        List<String> importsImplementsExtendedFromClassded = getImplementsFromClass(cu);
        // do listu naplnim vsechny classWithMethods, ktere se objevi v importu
        List<String> belongToBlocksList = tryCategorizedList(classNameWithPath, cu, importsImplementsExtendedFromClassded);
        belongToBlocksList.forEach(block -> parseImportsImplementsExtended(classNameWithPath, cu, block));
    }

    /**
     * mothod to find if class is needed to be placed into some block from implements and extends
     *
     * @param classNameWithPath
     * @param cu
     * @param list
     * @return
     */
    private List<String> tryCategorizedList(String classNameWithPath, CompilationUnit cu, List<String> list) {
        List<String> resultList = new ArrayList<>();
        list.forEach(y -> {
            for (Map.Entry<String, CompilationUnit> entry : classPathWithCuMapBlock1.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    classPathWithCuMapBlock1.put(classNameWithPath, cu);
                    resultList.add(BLOCK1_);
                    break;
                }
            }

            for (Map.Entry<String, CompilationUnit> entry : classPathWithCuMapBlock2.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    classPathWithCuMapBlock2.put(classNameWithPath, cu);
                    resultList.add(BLOCK2_);
                    break;
                }
            }

            for (Map.Entry<String, CompilationUnit> entry : classPathWithCuMapBlock3.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    classPathWithCuMapBlock3.put(classNameWithPath, cu);
                    resultList.add(BLOCK3_);
                    break;
                }
            }
        });
        return resultList;
    }


    /**
     * method to fill into blocks every class, which is imported, extended or implemented from classNameWithPath
     *
     * @param classNameWithPath
     * @param cu
     * @param block
     */
    private void parseImportsImplementsExtended(String classNameWithPath, CompilationUnit cu, String block) {
        List<String> importsImplementsExtendedFromClass = getImportsImplementsExtendedFromClass(cu);

        // do listu naplnim vsechny classWithMethods, ktere se objevi v importu
        Map<String, CompilationUnit> tempBlockList = new HashMap<>();
        importsImplementsExtendedFromClass.forEach(y -> {
            classPathWithCuMap.forEach((K, V) -> {
                //  System.out.println("y = " + y);
                //  System.out.println("K = " + K);
                String kwithoutdot;
                if (K.contains(".")) {
                    kwithoutdot = K.substring(0, K.lastIndexOf("."));
                } else {
                    kwithoutdot = K;
                }
                // to check if searched class has same name as tried class without path
                if (!y.contains(FILE_DELIMITER)) {
                    if (!kwithoutdot.substring(kwithoutdot.lastIndexOf(FILE_DELIMITER) + 1, kwithoutdot.length()).equals(y)) {
                        return;
                    }
                }


                if (kwithoutdot.endsWith(y)) {
                    // save to hashmap of proper block
                    if (block.equals(BLOCK1_)) {

                        if (!classPathWithCuMapBlock1.containsKey(K)) {
                            classPathWithCuMapBlock1.put(K, V);
                            flagUnCategorized = true;
                            tempBlockList.put(K, V);
                        }
                    } else if (block.equals(BLOCK2_)) {
                        if (!classPathWithCuMapBlock2.containsKey(K)) {
                            classPathWithCuMapBlock2.put(K, V);
                            flagUnCategorized = true;
                            tempBlockList.put(K, V);
                        }
                    } else if (block.equals(BLOCK3_)) {
                        if (!classPathWithCuMapBlock3.containsKey(K)) {
                            classPathWithCuMapBlock3.put(K, V);
                            flagUnCategorized = true;
                            tempBlockList.put(K, V);
                        }
                    }
                }
            });
        });
        /* for each class call recursively parseImportsImplementsExtended() method */
        tempBlockList.forEach((K, V) -> parseImportsImplementsExtended(K, V, block));
    }

    /**
     * method to get all implemented class names with path from given compilationUnit
     *
     * @param cu
     * @return
     */
    private List<String> getImplementsFromClass(CompilationUnit cu) {
        List<String> implementsClassList = new ArrayList<>();
        VoidVisitor<List<String>> implementsClassVisitor = new ImplementsClassVisitor();
        implementsClassVisitor.visit(cu, implementsClassList);
        return implementsClassList;
    }

    /**
     * method to get all imported,implemented and extended class names with path from given compilationUnit
     *
     * @param cu
     * @return
     */
    private List<String> getImportsImplementsExtendedFromClass(CompilationUnit cu) {
        List<ImportDeclaration> imports = cu.getImports();
        List<String> importsImplementsExtendedList = addImportsAsClassName(imports);

        List<String> implementsClassList = new ArrayList<>();
        VoidVisitor<List<String>> implementsClassVisitor = new ImplementsClassVisitor();
        implementsClassVisitor.visit(cu, implementsClassList);

        importsImplementsExtendedList.addAll(implementsClassList);
        //zavolani tridy ClassVisitor a ulozeni vsech extendujicich trid do listu
        List<String> extendedClassList = new ArrayList<>();
        VoidVisitor<List<String>> extendedClassVisitor = new ExtendedClassVisitor();
        extendedClassVisitor.visit(cu, extendedClassList);
        importsImplementsExtendedList.addAll(extendedClassList);
        return importsImplementsExtendedList;
    }

    /**
     * method to get all implemented and extended class name with path from given compilationUnit
     *
     * @param cu
     * @return
     */
    private List<String> getImplementsExtendedFromClass(CompilationUnit cu) {
        List<String> implementsExtendedList = new ArrayList<>();
        VoidVisitor<List<String>> implementsClassVisitor = new ImplementsClassVisitor();
        implementsClassVisitor.visit(cu, implementsExtendedList);

        //zavolani tridy ClassVisitor a ulozeni vsech extendujicich trid do listu
        List<String> extendedClassList = new ArrayList<>();
        VoidVisitor<List<String>> extendedClassVisitor = new ExtendedClassVisitor();
        extendedClassVisitor.visit(cu, extendedClassList);
        implementsExtendedList.addAll(extendedClassList);
        return implementsExtendedList;
    }

    /**
     * method to get all imported class name with path from given compilationUnit
     *
     * @param cu
     * @return
     */
    private List<String> getImportsFromClass(CompilationUnit cu) {
        List<ImportDeclaration> importsList = cu.getImports();
        return addImportsAsClassName(importsList);
    }


    /**
     * method to change raw line with imports to look like ordinary class name with path
     * using delimiter - JAVA_DELIMITER
     *
     * @param imports
     * @return
     */
    private List<String> addImportsAsClassName(List<ImportDeclaration> imports) {
        // dany import upravim -> odebere strednik naknci a slovo import s mezerou
        // vymenim "." v importu za "/"
        return imports.stream().map(y -> {
            String import_ = y.toString();
            return import_.substring(import_.indexOf(" ") + 1, import_.lastIndexOf(";"))
                    .replace(".", FILE_DELIMITER);

        }).collect(Collectors.toList());
    }



    public Map<String, CompilationUnit> getClassPathWithCuMap() {
        return classPathWithCuMap;
    }

    public Map<String, CompilationUnit> getClassPathWithCuMapBlock1() {
        return classPathWithCuMapBlock1;
    }

    public Map<String, CompilationUnit> getClassPathWithCuMapBlock2() {
        return classPathWithCuMapBlock2;
    }

    public Map<String, CompilationUnit> getClassPathWithCuMapBlock3() {
        return classPathWithCuMapBlock3;
    }

    public List<String> getFilesToAllBlocks() {
        return filesToAllBlocks;
    }

    /**
     * Visitor, ktery ulozi do listu collector vsechny anotace dane tridy
     */
    public static class AnnotationClassVisitor extends VoidVisitorAdapter<List<AnnotationExpr>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration n, List<AnnotationExpr> collector) {
            super.visit(n, collector);
            collector.addAll(n.getAnnotations());
        }
    }

    private static class ExtendedClassVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration md, List<String> collector) {
            super.visit(md, collector);
            md.getExtendedTypes().forEach(y -> collector.add(y.getNameAsString()));
        }
    }

    private static class ImplementsClassVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(ClassOrInterfaceDeclaration md, List<String> collector) {
            super.visit(md, collector);
            md.getImplementedTypes().forEach(y -> collector.add(y.getNameAsString()));
        }
    }


}
