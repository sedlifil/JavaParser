package cvut.fel.sedlifil.main;

import cvut.fel.sedlifil.configFile.RESTConfigFileHandler;
import cvut.fel.sedlifil.configFile.IConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.FileHandlerParser;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import cvut.fel.sedlifil.parser.ParserClass;
import org.apache.log4j.BasicConfigurator;

import java.util.Date;
import java.util.Map;

public class Main {
    private static final String FILE_APP_PATH = "/Users/filip/Dropbox/FEL_OI/semestr5/BookSystem/";
    private static final String CONFIG_GENERATED_PATH = "./generatedFiles";
    private static final String APP_GENERATED_PATH = "./generatedFiles";
    private static final String[] files = {"kitchensink-quickstart-ds.xml", "faces-config.xml"};


    public static void main(String[] args) {
        String filePath = FILE_APP_PATH;
        String configFilePath = CONFIG_GENERATED_PATH;
        String appFilePath = APP_GENERATED_PATH;
        if (args.length == 1){
            filePath = args[0];
        }

        if (args.length == 2){
            filePath = args[0];
            configFilePath = args[1];
        }

        if (args.length == 3){
            filePath = args[0];
            configFilePath = args[1];
            appFilePath = APP_GENERATED_PATH;
        }




        long startTime = System.currentTimeMillis();
        BasicConfigurator.configure();
        IFileHandlerParser fileHandlerParser = new FileHandlerParser(appFilePath);
        IConfigFileHandler configFileHandler = new RESTConfigFileHandler(configFilePath);
        ParserClass parserClass = new ParserClass(fileHandlerParser, configFileHandler, filePath, files);

        parserClass.divideIntoBlocks();
/*
        System.out.println("======BLOCK1==========");
        parserClass.getContainerClassCuMapBlock1().forEach((K, V) -> System.out.println(K));
        System.out.println("======BLOCK2==========");
        parserClass.getContainerClassCuMapBlock2().forEach((K, V) -> System.out.println(K));
        System.out.println("======BLOCK3==========");
        parserClass.getContainerClassCuMapBlock3().forEach((K, V) -> System.out.println(K));
        System.out.println("======REST==========");
        parserClass.getClassPathWithCuMap().forEach((K,V) -> System.out.println(K));
*/
        System.out.println("======TIME==========");
        long elapsedTime = (new Date()).getTime() - startTime;
        System.out.println("Time: " + elapsedTime + "ms.");
    }
}
