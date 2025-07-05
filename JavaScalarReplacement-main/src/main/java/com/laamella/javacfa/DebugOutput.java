package com.laamella.javacfa;


import com.github.javaparser.ast.expr.Expression;

import static java.util.Objects.requireNonNull;

/**
 * Shows the control flow information available in a node for inspection.
 */
public class DebugOutput {
    public String print(Flow flow) {
        requireNonNull(flow);
        StringBuilder output = new StringBuilder();
        new Visitor(flow).visit(f -> innerPrint(output, f));

        return output.toString();
    }

    private void innerPrint(StringBuilder output, Flow flow) {
        output.append(String.format("%-4.4s %-6.6s ", extractLineNumber(flow), flow.getType().name()));
        if (flow.getNext() != null) {
            output.append("-> " + extractLineNumber(flow.getNext()));
        } else {
            output.append("-> end");
        }
        if (flow.getMayBranchTo() != null) {
            output.append(" or " + extractLineNumber(flow.getMayBranchTo()));
            if (flow.getCondition() != null) {
                output.append(" (cond: " + extractChoiceLineColNumber(flow.getCondition()) + ")");
            }
        }
        if (!flow.getErrors().isEmpty()) {
            output.append(flow.getErrors().mkString(" *** ", ", ", " ***"));
        }

        output.append("\n");
    }

    private String extractChoiceLineColNumber(Expression condition) {
        return condition.getRange().map(range -> "" + range.begin.line + ":" + range.begin.column).orElse("end");
    }

    private String extractLineNumber(Flow flow) {
        return flow.getNode().getRange().map(range -> "" + range.begin.line).orElse("end");
    }
}
