package cvut.fel.sedlifil.fileHandler;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;
import java.util.Map;

public interface IFileHandlerParser {

    void saveAppToFile(Map<String, CompilationUnit> listBlock1,
                       Map<String, CompilationUnit> listBlock2,
                       Map<String, CompilationUnit> listBlock3,
                       List<String> listAllBlock);
}
