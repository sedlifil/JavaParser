package cvut.fel.sedlifil.fileHandler;

import com.github.javaparser.ast.CompilationUnit;
import cvut.fel.sedlifil.parser.ParserClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by filip on 09.12.17.
 */
public class FileHandlerParser implements IFileHandlerParser {
    private String locationOfDirectory;
    private static final String DATE_PATTERN = "yyyy_MM_dd_HH-mm-ss";
    private static final String APP_NONAME = "unKnown";
    private Logger logger;


    public FileHandlerParser(String locationOfDirectory) {
        this.locationOfDirectory = Paths.get(locationOfDirectory).toAbsolutePath().toString();
        logger = LoggerFactory.getLogger(FileHandlerParser.class);

    }

    public FileHandlerParser() {
        locationOfDirectory = FileHandlerParser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        locationOfDirectory = locationOfDirectory.substring(0, locationOfDirectory.indexOf(ParserClass.JAVA_TARGET) - 1);
        logger = LoggerFactory.getLogger(FileHandlerParser.class);

    }

    @Override
    public void saveAppToFile(Map<String, CompilationUnit> listBlock1,
                              Map<String, CompilationUnit> listBlock2,
                              Map<String, CompilationUnit> listBlock3,
                              List<String> listAllBlock) {
        logger.info("starts to saving app into blocks...");
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        Date date = new Date();

        String AppName = APP_NONAME;
        if (listBlock1.entrySet().stream().findFirst().isPresent()) {
            AppName = getAppNameFromPath(listBlock1.entrySet().stream().findFirst().get().getKey());
        } else if (listBlock2.entrySet().stream().findFirst().isPresent()) {
            AppName = getAppNameFromPath(listBlock2.entrySet().stream().findFirst().get().getKey());
        } else if (listBlock3.entrySet().stream().findFirst().isPresent()) {
            AppName = getAppNameFromPath(listBlock3.entrySet().stream().findFirst().get().getKey());
        }

        if (AppName.equals(APP_NONAME)) {
            return;
        }

        String directoryOfApp = locationOfDirectory + AppName + dateFormat.format(date);
        logger.info("generated app is located in " + directoryOfApp + ".");

        if (!Files.exists(Paths.get(directoryOfApp))) {
            try {
                Files.createDirectories(Paths.get(directoryOfApp));
            } catch (IOException e) {
                logger.error("Error with creating directory!!!");
                logger.error("FileHandler exits with error!!!");
                return;
            }
        }
        saveBlockAppToFile(listBlock1, directoryOfApp, ParserClass.BLOCK1_);
        saveBlockAppToFile(listBlock2, directoryOfApp, ParserClass.BLOCK2_);
        saveBlockAppToFile(listBlock3, directoryOfApp, ParserClass.BLOCK3_);
        saveAllBlocksAppToFile(listAllBlock, directoryOfApp, AppName);
    }

    private void saveBlockAppToFile(Map<String, CompilationUnit> blockList, String directoryOfApp, String blockName) {
        blockList.forEach((class_, cu) -> {
            String class_Path = class_.substring(class_.indexOf(ParserClass.JAVA_SOURCE), class_.lastIndexOf(ParserClass.FILE_DELIMITER));
            String class_PathWithName = class_.substring(class_.indexOf(ParserClass.JAVA_SOURCE), class_.length());
            try {
                Path fileName = Paths.get(directoryOfApp + ParserClass.FILE_DELIMITER + blockName + class_PathWithName);
                Files.createDirectories(Paths.get(directoryOfApp + ParserClass.FILE_DELIMITER + blockName + class_Path));
                Files.createFile(fileName);
                Files.write(fileName, cu.toString().getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }

    private void saveAllBlocksAppToFile(List<String> listOfPath, String directoryOfApp, String AppName) {
        listOfPath.forEach(nameWithPath -> {
            int startOfPath = 0;
            String name = nameWithPath;
            if (nameWithPath.contains(AppName)) {
                startOfPath = nameWithPath.lastIndexOf(AppName) + AppName.length();
                name = nameWithPath.substring(nameWithPath.lastIndexOf(ParserClass.FILE_DELIMITER), nameWithPath.length());
            }
            String relativePath = nameWithPath.substring(startOfPath, nameWithPath.length() - name.length());
            String relativePathWithName = nameWithPath.substring(startOfPath, nameWithPath.length());

            saveFileToBlock(AppName, name, nameWithPath, directoryOfApp, relativePath, relativePathWithName, ParserClass.BLOCK1_);
            saveFileToBlock(AppName, name, nameWithPath, directoryOfApp, relativePath, relativePathWithName, ParserClass.BLOCK2_);
            saveFileToBlock(AppName, name, nameWithPath, directoryOfApp, relativePath, relativePathWithName, ParserClass.BLOCK3_);
        });

    }

    private void saveFileToBlock(String AppName, String fileName, String inputFilePath, String directoryOfApp, String relativePath, String relativePathWithName, String block) {
        String fileDelimiter = ParserClass.FILE_DELIMITER;
        Path directoryPath = Paths.get(directoryOfApp + fileDelimiter + block + fileDelimiter + relativePath);
        Path fileNamePath = Paths.get(directoryOfApp + fileDelimiter + block + relativePathWithName);

        Path file = Paths.get(inputFilePath);
        byte[] fileArray;
        try {
            fileArray = Files.readAllBytes(file);
            Files.createDirectories(directoryPath);
            Files.createFile(fileNamePath);
            if (fileName.replace(ParserClass.FILE_DELIMITER, "").equals(ParserClass.PomXML)) {
                fileArray = changeArtificialIdInPom(AppName, fileArray, block);
            }
            Files.write(fileNamePath, fileArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] changeArtificialIdInPom(String AppName, byte[] fileArray, String block) {
        AppName = AppName.replace(ParserClass.FILE_DELIMITER, "");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            byteArrayOutputStream.write(fileArray);
            String serverName = ParserClass.BLOCK_;
            switch (block) {
                case ParserClass.BLOCK1_:
                    serverName += 1;
                    break;
                case ParserClass.BLOCK2_:
                    serverName += 2;
                    break;
                case ParserClass.BLOCK3_:
                    serverName += 3;
                    break;
            }
            return byteArrayOutputStream.toString().replace("<artifactId>" + AppName, "<artifactId>" + AppName + serverName).getBytes();
        } catch (IOException e) {
            logger.error("error with modifying POM.XML file. ArtifactId remain unchanged.");
            return fileArray;
        }
    }

    private String getAppNameFromPath(String path) {
        String AppName = path.substring(0, path.indexOf(ParserClass.JAVA_SOURCE));
        AppName = AppName.substring(AppName.lastIndexOf(ParserClass.FILE_DELIMITER, AppName.length()));
        return AppName;
    }
}
