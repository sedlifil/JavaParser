package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodParser {
    private Logger logger;

    MethodParser() {
        logger = LoggerFactory.getLogger(MethodParser.class);
    }

    /**
     * method to categorize method of class into proper blocks
     *
     * @param containerClassCUMap class
     * @param block               class belongs to block
     */
    public void categorizeMethods(Map<String, ContainerClassCU> containerClassCUMap, String block) {
        logger.info("starts to categorize methods into block with " + block + "...");

        containerClassCUMap.entrySet()
                .stream().
                filter(y -> !y.getValue().getBelongToBlocks().equals(""))
                .forEach(y -> {
                    List<MethodDeclaration> methodClassList;
                    methodClassList = y.getValue().getMethodDeclarations();
                    methodClassList.forEach(z -> categorizeMethod(z, y.getValue(), block, containerClassCUMap));
                });
    }

    /**
     * method to find annotation @Block of method and decide if belongs to given block or not
     *
     * @param methodDeclaration method of class
     * @param block             class belongs to block
     */
    private void categorizeMethod(MethodDeclaration methodDeclaration, ContainerClassCU classCU, String block,
                                  Map<String, ContainerClassCU> containerClassCUMap) {
        List<AnnotationExpr> annotationClassList = new ArrayList<>();
        VoidVisitor<List<AnnotationExpr>> annotationClassVisitor = new AnnotationMethodVisitor();
        annotationClassVisitor.visit(methodDeclaration, annotationClassList);

        for (AnnotationExpr ann : annotationClassList) {
            if (ann.getNameAsString().equals(ParserClass.BLOCK_)) {
                if (!(ann instanceof SingleMemberAnnotationExpr)) {
                    continue;
                }
                SingleMemberAnnotationExpr nax = (SingleMemberAnnotationExpr) ann;
                if (!nax.getMemberValue().toString().contains(block)) {
                    methodInSomeBlockReport(nax.getMemberValue().toString(), methodDeclaration.getNameAsString(), classCU);
                    methodDeclaration.remove();
                    /* also remove "override method" from implemented or extended class */
                    containerClassCUMap.forEach((k, v) -> {
                        List<String> list = v.getImplementsExtendedFromClass();
                        if(list.stream().anyMatch(classCU.getNameClass()::contains)){
                            List<MethodDeclaration> declarationList = v.getMethodDeclarations().stream()
                                    .filter(y -> y.getName().equals(methodDeclaration.getName()))
                                    .filter( y -> y.getAnnotations().stream().anyMatch(an -> an.getNameAsString().contains("Override")))
                                    .collect(Collectors.toList());
                            for (MethodDeclaration m:declarationList) {
                                m.remove();
                            }
                        }
                    });
                    break;
                }
            }
        }
    }

    private void methodInSomeBlockReport(String tryingMethod, String methodName, ContainerClassCU containerClassCU) {
        List<String> list = Arrays.asList(containerClassCU.getBelongToBlocks().split("\""));
        if (list.stream().filter(w -> w.contains(ParserClass.UNIVERSAL_BLOCK_KEY)).noneMatch(tryingMethod::contains)) {
            logger.warn("method " + methodName + " from " + containerClassCU.getNameClass() + " is NOT generated in any block!!!");
        }
    }

    /**
     * Annotation method visitor
     * visitor fill in into list of AnnotationExpr all annotation of given method
     */
    private static class AnnotationMethodVisitor extends VoidVisitorAdapter<List<AnnotationExpr>> {
        @Override
        public void visit(MethodDeclaration n, List<AnnotationExpr> collector) {
            super.visit(n, collector);
            collector.addAll(n.getAnnotations());
        }
    }

}
