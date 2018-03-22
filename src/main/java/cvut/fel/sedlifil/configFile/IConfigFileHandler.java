package cvut.fel.sedlifil.configFile;

import cvut.fel.sedlifil.parser.ContainerClassCU;

import java.util.Map;

public interface IConfigFileHandler {

    /**
     * generate config file that maps partitioning of blocks
     * @param listBlock1 list of class belongs to block 1
     * @param listBlock2 list of class belongs to block 2
     * @param listBlock3 list of class belongs to block 3
     */
    void generateConfigFile(Map<String, ContainerClassCU> listBlock1,
                            Map<String, ContainerClassCU> listBlock2,
                            Map<String, ContainerClassCU> listBlock3);
}
