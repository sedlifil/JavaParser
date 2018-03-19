package cvut.fel.sedlifil.configFile;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cvut.fel.sedlifil.parser.ParserClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by filip on 03.11.17.
 */
public class ClassWithMethodsPath {
    private String className;
    private String block;
    private Map<String, String> methodList;
    private Logger logger;

    ClassWithMethodsPath(String className, Map<String, String> methodsList, String block) {
        if (className.contains(ParserClass.FILE_DELIMITER)) {
            this.className = className.substring(className.lastIndexOf(ParserClass.FILE_DELIMITER) + 1, className.lastIndexOf(ParserClass.JAVA_SUFFIX));
        } else {
            this.className = className;
        }
        this.methodList = methodsList;
        this.block = block;
        logger = LoggerFactory.getLogger(ClassWithMethods.class);
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getMethodList() {
        return methodList;
    }

    public String getBlock() {
        return block;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("Error with mapping class " + className + " to Json!!!");
            return "";
        }

    }

}
