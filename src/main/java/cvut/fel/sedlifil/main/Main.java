package cvut.fel.sedlifil.main;

import cvut.fel.sedlifil.configHandler.RESTConfigFileHandler;
import cvut.fel.sedlifil.configHandler.IConfigFileHandler;
import cvut.fel.sedlifil.fileHandler.FileHandlerParser;
import cvut.fel.sedlifil.fileHandler.IFileHandlerParser;
import cvut.fel.sedlifil.parser.ParserClass;
import org.apache.log4j.BasicConfigurator;

public class Main {
    private static final String FILE_APP_PATH = "/Users/filip/Dropbox/FEL_OI/semestr5/BookSystem/";
    private static final String CONFIG_GENERATED_PATH = "." + ParserClass.FILE_DELIMITER + "generatedFiles";
    private static final String APP_GENERATED_PATH = "." + ParserClass.FILE_DELIMITER + "generatedFiles";
    private static final String[] files = {"kitchensink-quickstart-ds.xml", "persistence.xml", "beans.xml"};


    public static void main(String[] args) {
        String filePath = FILE_APP_PATH;
        String configFilePath = CONFIG_GENERATED_PATH;
        String appFilePath = APP_GENERATED_PATH;
        if (args.length == 1) {
            filePath = args[0];
        }

        if (args.length == 2) {
            filePath = args[0];
            configFilePath = args[1];
        }

        if (args.length == 3) {
            filePath = args[0];
            configFilePath = args[1];
            appFilePath = APP_GENERATED_PATH;
        }

        BasicConfigurator.configure();
        IFileHandlerParser fileHandlerParser = new FileHandlerParser(appFilePath);
        IConfigFileHandler configFileHandler = new RESTConfigFileHandler(configFilePath);
        ParserClass parserClass = new ParserClass(fileHandlerParser, configFileHandler, filePath, files);

        parserClass.divideIntoBlocks();

    }
}
