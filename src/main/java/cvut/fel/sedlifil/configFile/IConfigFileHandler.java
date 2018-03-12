package cvut.fel.sedlifil.configFile;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;
import java.util.Map;

public interface IConfigFileHandler {

    void generateConfigFile(Map<String, CompilationUnit> listBlock1,
                            Map<String, CompilationUnit> listBlock2,
                            Map<String, CompilationUnit> listBlock3);
}
