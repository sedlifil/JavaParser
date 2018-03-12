package cvut.fel.sedlifil.configFile;

import java.util.List;

import org.slf4j.Logger;


/**
 * Created by filip on 03.11.17.
 */
public class ClassWithMethods {
    private String className;
    private List<String> methodsList;
    private String block;

    ClassWithMethods(String className, List<String> methodsList, String block) {
        this.className = className;
        this.methodsList = methodsList;
        this.block = block;
    }

    public String getClassName() {
        return className;
    }

    public List<String> getMethodsList() {
        return methodsList;
    }


    @Override
    public String toString() {
        String res = className + "{\n" +
                "block= " + block + "\n" +
                "methodsList=\n\t";
        res += String.join("\n\t", methodsList);
        res += "\n}";
        return res;
    }
}
