package com.scalarreplacement;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.body.VariableDeclarator;

import java.util.*;
import java.util.stream.Collectors;

public class InterproceduralScalarVisitors {

    public static class SignatureVisitor extends ModifierVisitor<Void> {
        @Override
        public MethodDeclaration visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);

            NodeList<Parameter> rebuilt = new NodeList<>();
            String methodSrc = md.toString();

            for (Parameter param : md.getParameters()) {
                String typeName = param.getType().asString();
                String varName = param.getNameAsString();

                if (BasicScalarReplacement.classMap.containsKey(typeName)) {
                    // get all declared fields in Point
                    List<String> allFields = BasicScalarReplacement
                            .classMap
                            .get(typeName)
                            .resolve()
                            .getAllFields()
                            .stream()
                            .map(ResolvedFieldDeclaration::getName)
                            .collect(Collectors.toList());

                    for (String fld : allFields) {
                        String scalarName = varName + "_" + fld;
                        // only lift it if it actually appears in the method body
                        if (md.findAll(NameExpr.class).stream()
                                .anyMatch(ne -> ne.getNameAsString().equals(scalarName))) {
                            rebuilt.add(new Parameter(PrimitiveType.intType(), scalarName));
                            BasicScalarReplacement.declaredScalars.add(scalarName);
                                BasicScalarReplacement
                                    .liveFields
                                    .computeIfAbsent(varName, k -> new ArrayList<>())
                                    .add(fld);
                        }
                    }

                } else {
                    // keep everything else exactly
                    rebuilt.add(param.clone());
                }
            }

            md.setParameters(rebuilt);
            return md;
        }
    }

    public static class CallVisitor extends ModifierVisitor<Void> {
            @Override
            public MethodCallExpr visit(MethodCallExpr mc, Void arg) {
                super.visit(mc, arg);

                // 1) find the corresponding MethodDeclaration (so we know its scalar-params)
                Optional<MethodDeclaration> decl = mc.findCompilationUnit()
                        .flatMap(cu -> cu.findAll(MethodDeclaration.class).stream()
                                .filter(md -> md.getNameAsString().equals(mc.getNameAsString()))
                                .findFirst())
                        ;

                if (decl.isPresent()) {
                    MethodDeclaration md = decl.get();
                    NodeList<Expression> oldArgs = mc.getArguments();
                    NodeList<Expression> newArgs = new NodeList<>();
                    NodeList<Parameter> params = md.getParameters();

                    // 2) zip them: for each scalar-param, remap using the call-site var
                    for (int i = 0; i < params.size(); i++) {
                        Parameter p        = params.get(i);
                        Expression callArg = oldArgs.get(i);
                        String pname       = p.getNameAsString();

                        if (pname.contains("_") && callArg.isNameExpr()) {
                            // e.g. pname="p_x" → suffix="x"
                            String suffix  = pname.substring(pname.indexOf('_') + 1);
                            String baseVar = callArg.asNameExpr().getNameAsString();
                            newArgs.add(new NameExpr(baseVar + "_" + suffix));
                        } else {
                            // primitive (e.g. z) → keep as-is
                            newArgs.add(callArg.clone());
                        }
                    }
                    mc.setArguments(newArgs);
                }

                return mc;
            }


        //        @Override
//        public MethodCallExpr visit(MethodCallExpr mc, Void arg) {
//            super.visit(mc, arg);
//
//            // 1) find the corresponding MethodDeclaration (so we know its scalar-params)
//            Optional<MethodDeclaration> decl = mc.findCompilationUnit()
//                    .flatMap(cu -> cu.findAll(MethodDeclaration.class).stream()
//                            .filter(md -> md.getNameAsString().equals(mc.getNameAsString()))
//                            .findFirst())
//                    ;
//
//            if (decl.isPresent()) {
//                MethodDeclaration md = decl.get();
//                NodeList<Expression> oldArgs = mc.getArguments();
//                NodeList<Expression> newArgs = new NodeList<>();
//                NodeList<Parameter> params = md.getParameters();
//
//                // 2) zip them: for each scalar-param, remap using the call-site var
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter p        = params.get(i);
//                    Expression callArg = oldArgs.get(i);
//                    String pname       = p.getNameAsString();
//
//                    if (pname.contains("_") && callArg.isNameExpr()) {
//                        // e.g. pname="p_x" → suffix="x"
//                        String suffix  = pname.substring(pname.indexOf('_') + 1);
//                        String baseVar = callArg.asNameExpr().getNameAsString();
//                        newArgs.add(new NameExpr(baseVar + "_" + suffix));
//                    } else {
//                        // primitive (e.g. z) → keep as-is
//                        newArgs.add(callArg.clone());
//                    }
//                }
//                mc.setArguments(newArgs);
//            }
//
//            return mc;
//        }

        //        @Override
//        public MethodCallExpr visit(MethodCallExpr mc, Void arg) {
//            super.visit(mc, arg);
//
//            // 1) find the matching MethodDeclaration in this CU
//            Optional<MethodDeclaration> decl = mc.findCompilationUnit()
//                    .flatMap(cu -> cu.findAll(MethodDeclaration.class).stream()
//                            .filter(md -> md.getNameAsString().equals(mc.getNameAsString()))
//                            .findFirst());
//
//            if (decl.isPresent()) {
//                MethodDeclaration md = decl.get();
//                NodeList<Expression> oldArgs = mc.getArguments();
//                NodeList<Expression> newArgs = new NodeList<>();
//                NodeList<Parameter> params = md.getParameters();
//
//                // 2) zip parameters ↔ call‐site args
//                for (int i = 0; i < params.size(); i++) {
//                    Parameter p = params.get(i);
//                    Expression callArg = oldArgs.get(i);
//                    String pname = p.getNameAsString();
//
//                    if (pname.contains("_") && callArg.isNameExpr()) {
//                        // e.g. pname="p_x" → suffix="x"
//                        String suffix = pname.substring(pname.indexOf('_') + 1);
//                        String baseVar = callArg.asNameExpr().getNameAsString();
//                        newArgs.add(new NameExpr(baseVar + "_" + suffix));
//                    } else {
//                        // primitive or non‐lifted param: just reuse the original arg
//                        newArgs.add(callArg.clone());
//                    }
//                }
//
//                mc.setArguments(newArgs);
//            }
//
//            return mc;
//        }

//        @Override
//        public MethodCallExpr visit(MethodCallExpr mc, Void arg) {
//            super.visit(mc, arg);
//
//            // 1) Find the matching MethodDeclaration in this CU
//            Optional<MethodDeclaration> decl = mc.findCompilationUnit()
//                    .flatMap(cu -> cu.findAll(MethodDeclaration.class).stream()
//                            .filter(md -> md.getNameAsString().equals(mc.getNameAsString()))
//                            .findFirst());
//
//            if (decl.isPresent()) {
//                MethodDeclaration md = decl.get();
//
//                // 2) Build the new argument list by simply re‐emitting each parameter name
//                //    ASSUMING your SignatureVisitor has already set md.getParameters() to be
//                //    exactly the list of scalar params you want, in the correct order.
//                NodeList<Expression> newArgs = new NodeList<>();
//                for (Parameter p : md.getParameters()) {
//                    newArgs.add(new NameExpr(p.getNameAsString()));
//                }
//
//                mc.setArguments(newArgs);
//            }
//
//            return mc;
//        }
    }

    public static class LocalVariableVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(BlockStmt block, Void arg) {
            super.visit(block, arg);
            NodeList<Statement> oldStmts = block.getStatements();
            NodeList<Statement> newStmts = new NodeList<>();

            for (Statement stmt : oldStmts) {
                if (stmt.isExpressionStmt()) {
                    ExpressionStmt es = stmt.asExpressionStmt();
                    if (es.getExpression() instanceof VariableDeclarationExpr) {
                        VariableDeclarationExpr vde = (VariableDeclarationExpr) es.getExpression();
                        if (vde.getVariables().size() == 1) {
                            String type = vde.getElementType().asString();
                            String name = vde.getVariable(0).getNameAsString();
                            if (BasicScalarReplacement.classMap.containsKey(type)) {
                                // replace `Point p = …;` with scalar decls
                                for (ResolvedFieldDeclaration fld
                                        : BasicScalarReplacement.classMap.get(type).resolve().getAllFields()) {
                                    String scalar = name + "_" + fld.getName();
                                    BasicScalarReplacement.declaredScalars.add(scalar);
                                    BasicScalarReplacement
                                            .liveFields
                                            .computeIfAbsent(name, k -> new ArrayList<>())
                                            .add(fld.getName());
                                    newStmts.add(new ExpressionStmt(
                                            new VariableDeclarationExpr(PrimitiveType.intType(), scalar)
                                    ));
                                }
                                continue;  // skip the original `Point p = new Point();`
                            }
                        }
                    }
                }
                newStmts.add(stmt);
            }

            // replace all at once to avoid CME
            block.setStatements(newStmts);
        }
    }
}