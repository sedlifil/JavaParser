package cvut.fel.sedlifil.configFile;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cvut.fel.sedlifil.parser.ParserClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by filip on 03.11.17.
 */
public class ClassWithMethods {
    private String className;
    private String block;
    private List<String> methodList;
    private Logger logger;

    ClassWithMethods(String className, List<String> methodsList, String block) {
        if (className.contains(ParserClass.FILE_DELIMITER)) {
            this.className = className.substring(className.lastIndexOf(ParserClass.FILE_DELIMITER) + 1, className.lastIndexOf(ParserClass.JAVA_SUFFIX));
        } else {
            this.className = className;
        }
        this.methodList = methodsList;
        this.block = block;
        logger = LoggerFactory.getLogger(ClassWithMethods.class);
    }

    /**
     *
     * @return class name
     */
    public String getClassName() {
        return className;
    }

    /**
     *
     * @return list of class methods
     */
    public List<String> getMethodsList() {
        return methodList;
    }

    public String getBlock() {
        return block;
    }

    /**
     *
     * @return class in json format
     */
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

    @Override
    public String toString() {
        String res = className + "{\n" +
                "block= " + block + "\n" +
                "methodsList=\n\t";
        res += String.join("\n\t", methodList);
        res += "\n}";
        return res;
    }
}
