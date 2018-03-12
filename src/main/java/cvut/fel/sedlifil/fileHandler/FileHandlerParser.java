package cvut.fel.sedlifil.fileHandler;

import com.github.javaparser.ast.CompilationUnit;
import cvut.fel.sedlifil.parser.ParserClass;

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
public class FileHandlerParser implements IFileHandlerParser{
    private String locationOfDirectory;
    private static final String DATE_PATTERN = "yyyy_MM_dd_HH-mm-ss";
    private static final String APP_NONAME = "unKnown";

    public FileHandlerParser(String locationOfDirectory) {
        this.locationOfDirectory = Paths.get(locationOfDirectory).toAbsolutePath().toString();
    }

    public FileHandlerParser() {
        locationOfDirectory = FileHandlerParser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        locationOfDirectory = locationOfDirectory.substring(0, locationOfDirectory.indexOf(ParserClass.JAVA_TARGET)-1);
    }

    @Override
    public void saveAppToFile(Map<String, CompilationUnit> listBlock1,
                              Map<String, CompilationUnit> listBlock2,
                              Map<String, CompilationUnit> listBlock3,
                              List<String> listAllBlock){
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        Date date = new Date();

        String AppName = APP_NONAME;
        if(listBlock1.entrySet().stream().findFirst().isPresent()){
            AppName = getAppNameFromPath(listBlock1.entrySet().stream().findFirst().get().getKey());
        }else if(listBlock2.entrySet().stream().findFirst().isPresent()){
            AppName = getAppNameFromPath(listBlock2.entrySet().stream().findFirst().get().getKey());
        }else if(listBlock3.entrySet().stream().findFirst().isPresent()){
            AppName = getAppNameFromPath(listBlock3.entrySet().stream().findFirst().get().getKey());
        }

        if (AppName.equals(APP_NONAME)){
            return;
        }

        String directoryOfApp = locationOfDirectory + AppName + dateFormat.format(date);

        if (!Files.exists(Paths.get(directoryOfApp))) {
            try {
                Files.createDirectories(Paths.get(directoryOfApp));
                //System.out.println("Directory created!");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        saveBlockAppToFile(listBlock1, directoryOfApp, ParserClass.BLOCK1_);
        saveBlockAppToFile(listBlock2, directoryOfApp, ParserClass.BLOCK2_);
        saveBlockAppToFile(listBlock3, directoryOfApp, ParserClass.BLOCK3_);
        saveToAllBlocksAppToFile(listAllBlock, directoryOfApp, AppName);
    }

    private void saveBlockAppToFile(Map<String, CompilationUnit> blockList, String directoryOfApp, String blockName){
        blockList.forEach((class_, cu) -> {
            String class_Path = class_.substring(class_.indexOf(ParserClass.JAVA_SOURCE), class_.lastIndexOf(ParserClass.FILE_DELIMITER));
            String class_PathWithName = class_.substring(class_.indexOf(ParserClass.JAVA_SOURCE), class_.length());
            try {
                Path fileName = Paths.get(directoryOfApp + ParserClass.FILE_DELIMITER + blockName + class_PathWithName);
                Files.createDirectories(Paths.get(directoryOfApp + ParserClass.FILE_DELIMITER + blockName + class_Path));
                Files.createFile(fileName);
                Files.write(fileName, cu.toString().getBytes(StandardCharsets.UTF_8)); // Java 7+ only);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(class_Path);

        });
    }

    private void saveToAllBlocksAppToFile(List<String> listOfPath, String directoryOfApp, String AppName){
        listOfPath.forEach(y -> {
            System.out.println("Path: " + y);

                int startOfPath = 0;
                String name = y;
                if(y.contains(AppName)){
                    startOfPath = y.lastIndexOf(AppName) + AppName.length();
                    name = y.substring(y.lastIndexOf(ParserClass.FILE_DELIMITER), y.length());
                }
                String relativePath = y.substring(startOfPath, y.length()-name.length());
                String relativePathWithName = y.substring(startOfPath, y.length());
                System.out.println("name "+ name);
                System.out.println("relativePath "+ relativePath);
                System.out.println("relativePathWithName "+ relativePathWithName);
                //Path absFileName = Paths.get(directoryOfApp + ParserClass.FILE_DELIMITER + relativePathWithName);
            System.out.println("-" + directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK1_+relativePath);
            saveFileToBlock(y, Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK1_+relativePath),
                    Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK1_+relativePathWithName));

            saveFileToBlock(y, Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK2_+relativePath),
                    Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK2_+relativePathWithName));
            saveFileToBlock(y, Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK3_+relativePath),
                    Paths.get(directoryOfApp+ParserClass.FILE_DELIMITER+ParserClass.BLOCK3_+relativePathWithName));



        });

    }

    private void saveFileToBlock(String inputFilePath, Path directoryPath, Path fileNamePath){
        Path file = Paths.get(inputFilePath);
        byte[] fileArray;
        try {
            fileArray = Files.readAllBytes(file);
            Files.createDirectories(directoryPath);
            Files.createFile(fileNamePath);
            Files.write(fileNamePath, fileArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getAppNameFromPath(String path){
        String AppName = path.substring(0, path.indexOf(ParserClass.JAVA_SOURCE));
        AppName = AppName.substring(AppName.lastIndexOf(ParserClass.FILE_DELIMITER, AppName.length()));
        return AppName;
    }



    public static void main(String[] args) {
        FileHandlerParser fileHandlerParser = new FileHandlerParser(
                "src/main/java/org/jboss/as/quickstarts/kitchensink/");
       // fileHandlerParser.saveAppToFile();

    }

}
