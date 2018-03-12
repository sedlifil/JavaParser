package cvut.fel.sedlifil.main;

import java.util.List;

import org.slf4j.Logger;


/**
 * Created by filip on 03.11.17.
 */
public class ClassWithMethods {
    private String className;
    private String path;
    private List<String> methodsList;
    private Logger logger;

    public ClassWithMethods(String className, String path, List<String> methodsList) {
        this.className = className;
        this.path = path;
        this.methodsList = methodsList;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getMethodsList() {
        return methodsList;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        String res = className + "{\n" +
                "path= " + path + "\n" +
                "methodsList=\n\t";
        res += String.join("\n\t", methodsList);
        res += "\n}";
        return res;
    }
}
