package cvut.fel.sedlifil.parser;

import com.github.javaparser.ast.CompilationUnit;
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

    public void categorizeMethods(Map<String, CompilationUnit> compilationUnitMap, String block) {
        logger.info("starts to categorized methods into block with " + block + "...");
        List<MethodDeclaration> methodClassList = new ArrayList<>();
        VoidVisitor<List<MethodDeclaration>> methodDeclaration = new MethodName();
        compilationUnitMap.forEach((K, V) -> {
            methodClassList.clear();
            methodDeclaration.visit(V, methodClassList);
            methodClassList.forEach(y -> categorizeMethod(y, block));
        });
    }

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

               // System.out.println(nax.getMemberValue());

                if (!nax.getMemberValue().toString().contains(block)) {
                    methodDeclaration.remove();
                    break;
                }
            }
        }
    }

    private static class MethodName extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

    public static class AnnotationMethodVisitor extends VoidVisitorAdapter<List<AnnotationExpr>> {
        @Override
        public void visit(MethodDeclaration n, List<AnnotationExpr> collector) {
            super.visit(n, collector);
            collector.addAll(n.getAnnotations());
        }
    }

}
