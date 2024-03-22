package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {
    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    HashMap<String, ExpressionType> hashMap = new HashMap<>();

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkTree(ast.root);
        variableTypes.insert(variableTypes.getSize() - 1, hashMap);
    }

    public void checkTree(ASTNode node) {
        if (node == null) {
            return;
        }

        if (node.getClass() == Stylerule.class) {
            //Oude hashmap toevoegen aan lijst
            if (variableTypes.getSize() == 0) {
                variableTypes.addFirst(hashMap);
            } else {
                variableTypes.insert(variableTypes.getSize() - 1, hashMap);
            }
            hashMap = new HashMap<>();
        }

        ExpressionType expressionType = ExpressionType.UNDEFINED;
        if (node instanceof Declaration) {
            expressionType = setExpressionType(((Declaration) node).expression);
        } else if (node instanceof VariableAssignment){
            expressionType = setExpressionType(((VariableAssignment) node).expression);
        }

        if (node.getClass() == VariableAssignment.class) {
            hashMap.put(node.getNodeLabel(), expressionType);
        }

        for (ASTNode childNode : node.getChildren()) {
            checkTree(childNode);
        }
    }

    private ExpressionType setExpressionType(Expression node) {
        if (node instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (node instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        } else if (node instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        } else if (node instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        } else if (node instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
//        } else if (node instanceof VariableReference) {
//            return
        } else {
            return ExpressionType.UNDEFINED;
        }
    }
}
