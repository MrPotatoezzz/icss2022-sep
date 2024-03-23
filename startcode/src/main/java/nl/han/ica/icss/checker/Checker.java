package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {
    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private IHANLinkedList<VariableReference> ReferencesInCurrentScope = new HANLinkedList<>();
    HashMap<String, ExpressionType> hashMap = new HashMap<>();

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkASTNode(ast.root, null);
        variableTypes.insert(variableTypes.getSize() - 1, hashMap);

    }

    public void checkASTNode(ASTNode node, ASTNode parent) {
        if (node == null) {
            //ReferencesInCurrentScope = null;
            return;
        }

        if (node.getClass() == Stylerule.class) {
            if (variableTypes.getSize() == 0) {
                variableTypes.addFirst(hashMap);
            } else {
                variableTypes.insert(variableTypes.getSize() - 1, hashMap);
            }
            hashMap = new HashMap<>();

            if(ReferencesInCurrentScope.getSize() != 0) {
                checkIfReferencedVariableExists(ReferencesInCurrentScope.getFirst());
                ReferencesInCurrentScope = new HANLinkedList<>();
            }
        }

        ExpressionType expressionType = ExpressionType.UNDEFINED;
        if (node instanceof Declaration) {
            expressionType = setExpressionType(((Declaration) node).expression);
        } else if (node instanceof VariableAssignment){
            expressionType = setExpressionType(((VariableAssignment) node).expression);
        }else if (node instanceof VariableReference){
            if(ReferencesInCurrentScope.getSize() == 0){
                ReferencesInCurrentScope.addFirst((VariableReference) node);
            }else {
                ReferencesInCurrentScope.insert(ReferencesInCurrentScope.getSize() - 1, (VariableReference) node);
            }
        }

        if (node.getClass() == VariableAssignment.class) {
            hashMap.put(((VariableAssignment) node).name.name, expressionType);
        }

        for (ASTNode childNode : node.getChildren()) {
            checkASTNode(childNode, node);
        }
    }


    private void checkIfReferencedVariableExists(ASTNode node){
        if(ReferencesInCurrentScope.getSize() == 0){
            return;
        }
        ExpressionType expressionType = ExpressionType.UNDEFINED;
        VariableReference reference = (VariableReference) node;
        boolean foundNote = false;
        boolean correctType = false;
        for (int i = 0; i < variableTypes.getSize(); i++) {
            if (variableTypes.get(i).containsKey(reference.name)) {
                expressionType = setExpressionType(((VariableReference) node));
                if (variableTypes.get(i).get(reference.name) == expressionType) {
                    correctType = true;
                }
                foundNote = true;
            }
        }

        if (!foundNote) {
            node.setError("Variable: " + reference.name + " undefined");
        } else if (!correctType && expressionType != ExpressionType.UNDEFINED) {
            node.setError("Can't change " + reference.name + " to type " + expressionType);
        }
        ReferencesInCurrentScope.delete(0);
        if(ReferencesInCurrentScope.getSize() != 0) {
            checkIfReferencedVariableExists(ReferencesInCurrentScope.getFirst());
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
