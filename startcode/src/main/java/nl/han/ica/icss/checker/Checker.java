package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker {
    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    private IHANLinkedList<VariableReference> ReferencesInCurrentLocalScope = new HANLinkedList<>();
    private HashMap<String, ExpressionType> hashMap = new HashMap<>();

    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkASTNode(ast.root);
        variableTypes.insert(variableTypes.getSize() - 1, hashMap);
    }

    public void checkASTNode(ASTNode node) {
        if (node == null) {
            return;
        }

        if (node instanceof Stylerule) {
            newScope();
        }

        ExpressionType expressionType = ExpressionType.UNDEFINED;
        if (node instanceof Declaration) {
               expressionType = checkDeclaration((Declaration) node);
        } else if (node instanceof VariableAssignment){
            expressionType = getExpressionType(((VariableAssignment) node).expression);
        }else if (node instanceof VariableReference){
            if(ReferencesInCurrentLocalScope.getSize() == 0){
                ReferencesInCurrentLocalScope.addFirst((VariableReference) node);
            }else {
                ReferencesInCurrentLocalScope.insert(ReferencesInCurrentLocalScope.getSize() - 1, (VariableReference) node);
            }
        }else if(node instanceof Operation){
            checkIfOperatorsAreCorrect((Operation) node);
        }else if(node instanceof IfClause){
            checkIfIfstatementContainsBoolean((IfClause)node);
        }

        if (node.getClass() == VariableAssignment.class) {
            hashMap.put(((VariableAssignment) node).name.name, expressionType);
        }

        for (ASTNode childNode : node.getChildren()) {
            checkASTNode(childNode);
        }
    }

    private void newScope(){
        if (variableTypes.getSize() == 0) {
            variableTypes.addFirst(hashMap);
        } else {
            if(variableTypes.getSize() > 1) {
                variableTypes.delete(0);
            }
            variableTypes.insert(variableTypes.getSize() - 1, hashMap);
        }
        hashMap = new HashMap<>();

        if(ReferencesInCurrentLocalScope.getSize() != 0) {
            checkIfReferencedVariableExists(ReferencesInCurrentLocalScope.getFirst());
            ReferencesInCurrentLocalScope = new HANLinkedList<>();
        }
    }
    private ExpressionType checkDeclaration(Declaration node){
        ExpressionType expressionType = getExpressionType(node.expression);
        if(node.expression instanceof VariableReference){
            expressionType = getVariableExpressionTypeFromHashMap((VariableReference) node.expression);
        }else if(node.expression instanceof Operation){
            expressionType = checkIfOperatorsAreCorrect((Operation)node.expression);
        }

        switch(node.property.name) {
            case "color":
            case "background-color":
                if (expressionType != ExpressionType.COLOR) {
                    node.setError("property must be color");
                }
                break;
            case "height":
            case "width":
                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                    node.setError("property must be pixel or percentage");
                }
                break;
            default:
                break;
        }
        return expressionType;
    }

    private ExpressionType getVariableExpressionTypeFromHashMap(VariableReference node){
        if(hashMap.get(node.name) != null){
            return getExpressionType(node);
        }

        if(variableTypes.get(variableTypes.getSize() - 1).get(node.name) != null){
            return variableTypes.get(variableTypes.getSize() - 1).get(node.name);
        }

        node.setError("variable not in this scope");

        return ExpressionType.UNDEFINED;
    }

    private void checkIfIfstatementContainsBoolean(IfClause node){
        ExpressionType expression;
        if(node.conditionalExpression instanceof VariableReference){
            expression = getVariableExpressionTypeFromHashMap((VariableReference)node.conditionalExpression);
        }else {
            expression = getExpressionType(node.conditionalExpression);
        }
        if(expression != ExpressionType.BOOL){
            node.setError("If-statement must contain a boolean");
        }
    }

    private ExpressionType  checkIfOperatorsAreCorrect(Operation node){
        ExpressionType left = null;
        ExpressionType right = null;

            if (node.rhs instanceof Operation){
                right = checkIfOperatorsAreCorrect((Operation)node.rhs);
            } else if(node.rhs instanceof VariableReference) {
                right = getVariableExpressionTypeFromHashMap(((VariableReference) node.rhs));
            }else if(node.rhs instanceof Literal){
                right = getExpressionType(node.rhs);
            }

            if(node.lhs instanceof Operation){
                left = checkIfOperatorsAreCorrect((Operation)node.lhs);
            }else if (node.lhs instanceof VariableReference){
                left = getVariableExpressionTypeFromHashMap(((VariableReference) node.lhs));
            }else if(node.lhs instanceof Literal){
                left = getExpressionType(node.lhs);
            }

            if(node instanceof MultiplyOperation){
                if(left != ExpressionType.SCALAR && right != ExpressionType.SCALAR){
                    node.setError("One of the types must be scalar");
                    return ExpressionType.UNDEFINED;
                }
            }

            if(left == null || right == null){
                node.setError("Expression unfinished");
                return ExpressionType.UNDEFINED;
            }

            if(left == ExpressionType.COLOR || right == ExpressionType.COLOR){
                node.setError("Operation cannot contain color");
                return ExpressionType.UNDEFINED;
            }

            if(left == right || (left == ExpressionType.SCALAR || right == ExpressionType.SCALAR)){
                if(left == ExpressionType.SCALAR){
                    return right;
                }
                return left;
            }

        node.setError("Expressions must be of same type");
        return ExpressionType.UNDEFINED;
    }


    private void checkIfReferencedVariableExists(ASTNode node){
        if(ReferencesInCurrentLocalScope.getSize() == 0){
            return;
        }
        ExpressionType expressionType = ExpressionType.UNDEFINED;
        VariableReference reference = (VariableReference) node;
        boolean foundNote = false;
        boolean correctType = false;
        for (int i = 0; i < variableTypes.getSize(); i++) {
            if (variableTypes.get(i).containsKey(reference.name)) {
                expressionType = getExpressionType(((VariableReference) node));
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
        ReferencesInCurrentLocalScope.delete(0);
        if(ReferencesInCurrentLocalScope.getSize() != 0) {
            checkIfReferencedVariableExists(ReferencesInCurrentLocalScope.getFirst());
        }
    }

    private ExpressionType getExpressionType(Expression node) {
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
        } else {
            return ExpressionType.UNDEFINED;
        }
    }
}
