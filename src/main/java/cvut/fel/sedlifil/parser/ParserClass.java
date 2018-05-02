package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import cvut.fel.sedlifil.configFile.IConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.exit;

/**
 * Created by filip on 01.11.17.
 */
public class ParserClass {

    private final String filePath;
    private final IFileHandlerParser fileHandlerParser;
    private final IConfigFileHandler configFileHandler;
    private final String[] filesName;

    private Map<String, ContainerClassCU> containerClassCuMap;
    private Map<String, ContainerClassCU> containerClassCuMapBlock1;
    private Map<String, ContainerClassCU> containerClassCuMapBlock2;
    private Map<String, ContainerClassCU> containerClassCuMapBlock3;
    private List<String> filesToAllBlocks;
    private boolean flagUnCategorized = true;
    public static final String BLOCK_ = "Block";
    public static final String UNIVERSAL_BLOCK_KEY = "key";
    public static final String BLOCK1_ = "key1";
    public static final String BLOCK2_ = "key2";
    public static final String BLOCK3_ = "key3";
    public static final String FILE_DELIMITER = File.separator;
    public static final String JAVA_SUFFIX = ".java";
    public static final String PomXML = "pom.xml";
    public static final String JAVA_SOURCE = ParserClass.FILE_DELIMITER + "src";
    public static final String JAVA_TARGET = "target";
    private static final String JAVA_IMPORT_ALL_PACKAGE = "*";
    private static final int ERROR_CODE = 1;
    private final MethodParser methodParser;

    private Logger logger;

    /**
     * @param fileHandlerParser file handler which saves divided app into files
     * @param configFileHandler config handler which generates config file with name of class and theirs methods
     * @param filePath          path of app which should be divided into blocks
     */
    public ParserClass(IFileHandlerParser fileHandlerParser, IConfigFileHandler configFileHandler, String filePath, String []filesName) {
        this.fileHandlerParser = fileHandlerParser;
        this.configFileHandler = configFileHandler;
        this.filePath = filePath;
        this.filesName = filesName;
        methodParser = new MethodParser();
        containerClassCuMap = new HashMap<>();
        containerClassCuMapBlock1 = new HashMap<>();
        containerClassCuMapBlock2 = new HashMap<>();
        containerClassCuMapBlock3 = new HashMap<>();
        filesToAllBlocks = new ArrayList<>();
        logger = LoggerFactory.getLogger(ParserClass.class);
    }

    /**
     * main method to divide app into blocks
     */
    public void divideIntoBlocks() {
        divideIntoBlocks(true, true);

    }

    /**
     * main method to divide app into blocks with two parameters
     *
     * @param generatedFiles to generated target app infrastructure divided into modules
     * @param generatedConfigFiles to generate configFile of target app divided into modules
     */
    public void divideIntoBlocks(boolean generatedFiles, boolean generatedConfigFiles) {
        logger.info("JavaParser starts...");
        findAllClasses();
        splitIntoBlocks();

        methodParser.categorizeMethods(containerClassCuMapBlock1, BLOCK1_);
        methodParser.categorizeMethods(containerClassCuMapBlock2, BLOCK2_);
        methodParser.categorizeMethods(containerClassCuMapBlock3, BLOCK3_);

        if (generatedFiles) {
            fileHandlerParser.saveAppToFile(containerClassCuMapBlock1,
                    containerClassCuMapBlock2,
                    containerClassCuMapBlock3,
                    filesToAllBlocks);
        }

        if (generatedConfigFiles) {
            configFileHandler.generateConfigFile(containerClassCuMapBlock1,
                    containerClassCuMapBlock2,
                    containerClassCuMapBlock3);
        }


        logger.info("JavaParser ends.");
    }

    /**
     * method to find all directories and files of given path
     */
    private void findAllClasses() {
        logger.info("JavaParser starts for searching files in given path: " + filePath);
        findAllClasses(filePath);
    }

    /**
     * Find all files in given directory path and theirs subdirectories
     *
     * @param path of root/first directory
     */
    private void findAllClasses(String path) {
        List<String> classNameList = new ArrayList<>();
        List<String> directories = new ArrayList<>();

        Path absPath = Paths.get(path).toAbsolutePath();
        File[] files = new File(absPath.toString()).listFiles();

        if (files == null) {
            logger.error("There is no directory or file in this path.");
            logger.error("JavaParser exits!");
            System.exit(ERROR_CODE);
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(JAVA_SUFFIX)) {
                classNameList.add(file.getName());
            } else if (file.isDirectory() && !file.getName().equals(JAVA_TARGET) && !file.getName().startsWith(".")) {
                directories.add(file.getName());
            } else if (file.getName().equals(PomXML)) {
                filesToAllBlocks.add(absPath.toString().concat(FILE_DELIMITER + file.getName()));
            }else {
                for (String aFilesName : filesName) {
                    if (file.getName().equals(aFilesName)) {
                        filesToAllBlocks.add(absPath.toString().concat(FILE_DELIMITER + file.getName()));
                    }
                }
            }
        }
        classNameList.forEach(y -> saveClass(absPath.toString().concat(FILE_DELIMITER + y)));

        /* recursion for found directories */
        for (String s : directories) {
            findAllClasses(path.concat(FILE_DELIMITER + s));
        }
    }

    /**
     * save found classes and split into 3 set of blocks depending on annotation
     *
     * @param path path of file
     */
    private void saveClass(String path) {

        try {
            ContainerClassCU containerClassCU = new ContainerClassCU(path);
            boolean flagDeepCopy = false;
            String belongToBlock = containerClassCU.getBelongToBlocks();

            if (belongToBlock.contains(BLOCK1_)) {
                containerClassCuMapBlock1.put(path, containerClassCU);
                flagDeepCopy = true;
            }
            if (belongToBlock.contains(BLOCK2_)) {
                if (flagDeepCopy) {
                    ContainerClassCU containerClassCUCopy = new ContainerClassCU(path);
                    containerClassCuMapBlock2.put(path, containerClassCUCopy);
                } else {
                    containerClassCuMapBlock2.put(path, containerClassCU);
                    flagDeepCopy = true;
                }
            }
            if (belongToBlock.contains(BLOCK3_)) {
                if (flagDeepCopy) {
                    ContainerClassCU containerClassCUCopy = new ContainerClassCU(path);
                    containerClassCuMapBlock3.put(path, containerClassCUCopy);
                } else {
                    containerClassCuMapBlock3.put(path, containerClassCU);
                }
            }
            containerClassCuMap.put(path, containerClassCU);
        } catch (FileNotFoundException e) {
            logger.error("Error: can not open file " + path);
            logger.info("JavaParser ends with error status.");
            exit();
        }
    }


    /**
     * method which, for every list of blocks finds all imports and calls parse methods
     */
    private void splitIntoBlocks() {
        logger.info("JavaParser starts to dividing into blocks...");
        Map<String, ContainerClassCU> classBlockList = new HashMap<>();

        containerClassCuMapBlock1.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(V, BLOCK1_));

        classBlockList.clear();
        containerClassCuMapBlock2.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(V, BLOCK2_));

        classBlockList.clear();
        containerClassCuMapBlock3.forEach(classBlockList::put);
        classBlockList.forEach((K, V) -> parseImportsImplementsExtended(V, BLOCK3_));

        /* until some class is placed to some block */
        while (flagUnCategorized) {
            flagUnCategorized = false;
            containerClassCuMap.forEach(this::parseImportsFromUncategorizedClass);
        }
    }

    /**
     * method to find if class is needed to be placed into some block
     *
     * @param classNameWithPath name of class with absolute path
     */
    private void parseImportsFromUncategorizedClass(String classNameWithPath, ContainerClassCU containerClassCU) {
        List<String> implementsFromClass = containerClassCU.getImplementsFromClass();
        List<String> belongToBlocksList = tryCategorizedList(classNameWithPath, containerClassCU, implementsFromClass);
        belongToBlocksList.forEach(block -> parseImportsImplementsExtended(containerClassCU, block));
    }

    /**
     * method to find if class is needed to be placed into some block from implements and extends
     *
     * @param classNameWithPath name of class with absolute path
     * @param list              list of implements from class
     * @return list of blocks where class should be added
     */
    private List<String> tryCategorizedList(String classNameWithPath, ContainerClassCU containerClassCU, List<String> list) {
        List<String> resultList = new ArrayList<>();
        list.forEach(y -> {
            for (Map.Entry<String, ContainerClassCU> entry : containerClassCuMapBlock1.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    containerClassCuMapBlock1.put(classNameWithPath, containerClassCU);
                    resultList.add(BLOCK1_);
                    break;
                }
            }

            for (Map.Entry<String, ContainerClassCU> entry : containerClassCuMapBlock2.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    containerClassCuMapBlock2.put(classNameWithPath, containerClassCU);
                    resultList.add(BLOCK2_);
                    break;
                }
            }

            for (Map.Entry<String, ContainerClassCU> entry : containerClassCuMapBlock3.entrySet()) {
                if (entry.getKey().substring(0, entry.getKey().lastIndexOf(".")).endsWith(y)) {
                    containerClassCuMapBlock3.put(classNameWithPath, containerClassCU);
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
     * @param block belongs to block
     */
    private void parseImportsImplementsExtended(ContainerClassCU containerClassCU, String block) {
        List<String> importsImplementsExtendedFromClass = containerClassCU.getImportsImplementsExtendedFromClass();
        Map<String, ContainerClassCU> tempBlockList = new HashMap<>();
        importsImplementsExtendedFromClass.forEach(importName -> {
            /* need to be imported all classes from package -> "*" notation */
            if (importName.endsWith(JAVA_IMPORT_ALL_PACKAGE)) {
                importAllFromPackage(tempBlockList, importName, block);
            } else {
                containerClassCuMap.forEach((K, V) -> {
                    String kwithoutdot = K;
                    if (K.contains(JAVA_SUFFIX)) {
                        kwithoutdot = K.substring(0, K.lastIndexOf(JAVA_SUFFIX));
                    }
                    /* to check if searched class has same name as tried class without paths */
                    if (!importName.contains(FILE_DELIMITER)) {
                        if (!kwithoutdot.substring(kwithoutdot.lastIndexOf(FILE_DELIMITER) + 1, kwithoutdot.length()).equals(importName)) {
                            return;
                        }
                    }
                    if (kwithoutdot.endsWith(importName)) {
                        insertIntoBlock(tempBlockList, K, V, block);
                    }
                });
            }
        });
        /* list of classes, which should be added to some block(s) from classes fields  */
        Map<String, ContainerClassCU> tempList = parseFieldVariables(containerClassCU, block);
        /* call recursively for each class in list */
        tempList.forEach((K, V) -> parseImportsImplementsExtended(V, block));
        /* for each class call recursively parseImportsImplementsExtended() method */
        tempBlockList.forEach((K, V) -> parseImportsImplementsExtended(V, block));
    }

    /**
     * method to decide where to put class based on block
     *
     * @param tempBlockList - to add found class into list
     * @param pathName      - name of class with absolute path
     * @param block         - class belongs to block
     */
    private void insertIntoBlock(Map<String, ContainerClassCU> tempBlockList, String pathName, ContainerClassCU containerClassCU, String block) {
        switch (block) {
            case BLOCK1_:
                if (!containerClassCuMapBlock1.containsKey(pathName)) {
                    containerClassCuMapBlock1.put(pathName, containerClassCU);
                    flagUnCategorized = true;
                    tempBlockList.put(pathName, containerClassCU);
                }
                break;
            case BLOCK2_:
                if (!containerClassCuMapBlock2.containsKey(pathName)) {
                    containerClassCuMapBlock2.put(pathName, containerClassCU);
                    flagUnCategorized = true;
                    tempBlockList.put(pathName, containerClassCU);
                }
                break;
            case BLOCK3_:
                if (!containerClassCuMapBlock3.containsKey(pathName)) {
                    containerClassCuMapBlock3.put(pathName, containerClassCU);
                    flagUnCategorized = true;
                    tempBlockList.put(pathName, containerClassCU);
                }
                break;
        }
    }

    /**
     * method to searched all fields of class and if it is necessary put found class into same block
     *
     * @param containerClassCU class
     * @param block            belongs to block
     * @return list of found classes which should be added into same block as class
     */
    private Map<String, ContainerClassCU> parseFieldVariables(ContainerClassCU containerClassCU, String block) {
        Map<String, ContainerClassCU> result = new HashMap<>();
        containerClassCU.getFieldsFromClassList()
                .stream()
                .filter(v -> v.getType() instanceof ClassOrInterfaceType)
                .forEach(
                        v -> {
                            Type type = v.getType();
                            ((ClassOrInterfaceType) type).getTypeArguments()
                                    .ifPresent(tas -> tas.forEach(y -> tryCategorizeField(result, y, containerClassCU, block)));
                            if (!((ClassOrInterfaceType) type).getTypeArguments().isPresent()) {
                                tryCategorizeField(result, type, containerClassCU, block);
                            }
                        }
                );
        return result;
    }

    /**
     * method for those imports which ends with * -> need imports all classes from that package
     *
     * @param tempBlockList list into which the classes will be added
     * @param importPath    path of import of class
     * @param block         belongs to block
     */
    private void importAllFromPackage(Map<String, ContainerClassCU> tempBlockList, String importPath, String block) {
        String importPathAsPackageName = importPath.replaceAll(ParserClass.FILE_DELIMITER, ".")
                .substring(0, importPath.lastIndexOf(ParserClass.JAVA_IMPORT_ALL_PACKAGE) - 1);

        containerClassCuMap.entrySet()
                .stream()
                .filter(map -> map.getValue()
                        .getCompilationUnit()
                        .getPackageDeclaration()
                        .isPresent())
                .filter(map -> map.getValue()
                        .getCompilationUnit()
                        .getPackageDeclaration()
                        .get()
                        .getPackageName()
                        .equals(importPathAsPackageName))
                .forEach(map -> tempBlockList.put(map.getKey(), map.getValue()));
    }

    /**
     * check all fields from class and figure it out if some field do not need import some class from same package
     *
     * @param list  - to add found class into list
     * @param type  - type of Field from class
     * @param block - class belongs to block
     */
    private void tryCategorizeField(Map<String, ContainerClassCU> list, Type type, ContainerClassCU containerClassCU, String block) {
        containerClassCuMap.forEach((K, V) -> {
            String className = K.substring(K.lastIndexOf(ParserClass.FILE_DELIMITER) + 1, K.lastIndexOf(ParserClass.JAVA_SUFFIX));
            // both classes have present pagkage
            if (containerClassCU.getCompilationUnit().getPackageDeclaration().isPresent() && V.getCompilationUnit().getPackageDeclaration().isPresent()) {
                // packages are the same
                if (containerClassCU.getCompilationUnit().getPackageDeclaration().get().getPackageName().equals(V.getCompilationUnit().getPackageDeclaration().get().getPackageName())) {
                    //name of class and searched type has equal names
                    if (type.toString().equals(className)) {
                        insertIntoBlock(list, K, V, block);
                    }
                }
            } // both classes do not have package
            else if (!containerClassCU.getCompilationUnit().getPackageDeclaration().isPresent() && !V.getCompilationUnit().getPackageDeclaration().isPresent()) {
                //name of class and searched type has equal names
                if (type.toString().equals(className)) {
                    insertIntoBlock(list, K, V, block);
                }
            }
        });
    }

    /**
     * get map of containerClassCu of block 1
     *
     * @return map of containerClassCu of block 1
     */
    public Map<String, ContainerClassCU> getContainerClassCuMapBlock1() {
        return containerClassCuMapBlock1;
    }

    /**
     * get map of containerClassCu of block 2
     *
     * @return map of containerClassCu of block 2
     */
    public Map<String, ContainerClassCU> getContainerClassCuMapBlock2() {
        return containerClassCuMapBlock2;
    }

    /**
     * get map of containerClassCu of block 3
     *
     * @return map of containerClassCu of block 3
     */
    public Map<String, ContainerClassCU> getContainerClassCuMapBlock3() {
        return containerClassCuMapBlock3;
    }

    /**
     * get list of important files of app
     *
     * @return list of important files of app
     */
    public List<String> getFilesToAllBlocks() {
        return filesToAllBlocks;
    }
}
