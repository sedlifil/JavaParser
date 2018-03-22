package cvut.fel.sedlifil.main;

import cvut.fel.sedlifil.configFile.RESTConfigFileHandler;
import cvut.fel.sedlifil.configFile.IConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.FileHandlerParser;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import cvut.fel.sedlifil.parser.ParserClass;
import org.apache.log4j.BasicConfigurator;

import java.util.Date;

public class Main {



    public static void main(String[] args) {
        String filePath = "/Users/filip/Dropbox/FEL_OI/semestr5/BookSystem/";
        String configFilePath = "/Users/filip/Dropbox/BookSystemFrontend/src/";
        if (args.length == 1){
            filePath = args[0];
        }


        long startTime = System.currentTimeMillis();
        BasicConfigurator.configure();
        IFileHandlerParser fileHandlerParser = new FileHandlerParser();
        IConfigFileHandler configFileHandler = new RESTConfigFileHandler(configFilePath);
        ParserClass parserClass = new ParserClass(fileHandlerParser, configFileHandler, filePath);

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
