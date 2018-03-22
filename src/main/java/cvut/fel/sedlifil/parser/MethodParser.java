package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodParser {
    private Logger logger;

    MethodParser() {
        logger = LoggerFactory.getLogger(MethodParser.class);
    }

    /**
     * method to categorize method of class into proper blocks
     * @param containerClassCUMap class
     * @param block class belongs to block
     */
    public void categorizeMethods(Map<String, ContainerClassCU> containerClassCUMap, String block) {
        logger.info("starts to categorized methods into block with " + block + "...");

        containerClassCUMap.forEach((K, containerClassCU) -> {
            List<MethodDeclaration> methodClassList;
            methodClassList = containerClassCU.getMethodNames();
            methodClassList.forEach(y -> categorizeMethod(y, block));
        });
    }

    /**
     * method to find annotation @Block of method and decide if belongs to given block or not
     * @param methodDeclaration method of class
     * @param block class belongs to block
     */
    private void categorizeMethod(MethodDeclaration methodDeclaration, String block) {
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
                    methodDeclaration.remove();
                    break;
                }
            }
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
