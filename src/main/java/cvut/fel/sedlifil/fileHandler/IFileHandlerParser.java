package cvut.fel.sedlifil.fileHandler;

import com.github.javaparser.ast.CompilationUnit;
import cvut.fel.sedlifil.parser.ContainerClassCU;

import java.util.List;
import java.util.Map;

public interface IFileHandlerParser {
    /**
     * saving divided app into files
     * @param listBlock1 list of class belongs to block 1
     * @param listBlock2 list of class belongs to block 2
     * @param listBlock3 list of class belongs to block 3
     * @param listAllBlock list of remaining files belongs to all blocks
     */
    void saveAppToFile(Map<String, ContainerClassCU> listBlock1,
                       Map<String, ContainerClassCU> listBlock2,
                       Map<String, ContainerClassCU> listBlock3,
                       List<String> listAllBlock);
}
