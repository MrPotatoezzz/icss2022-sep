package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;
    private ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues.addFirst(new HashMap<>());
        transformASTNodeReferences(ast.root);

        for (ASTNode node:nodesToRemove) {
            ast.root.removeChild(node);;
        }
    }

    private void transformASTNodeReferences(ASTNode node){
        for (ASTNode childNode : node.getChildren()) {
            if (childNode instanceof VariableAssignment) {
                variableValues.get(0).put(((VariableAssignment) childNode).name.name, (Literal) ((VariableAssignment) childNode).expression);
            nodesToRemove.add(childNode);
            }else if(childNode instanceof Stylerule){
                transformStyleRule((Stylerule) childNode);
            }
        }
    }

    private void transformStyleRule(Stylerule stylerule){
        ArrayList<ASTNode> bodyNodesToRemove = new ArrayList<>();
        for (ASTNode node: stylerule.getChildren()) {
            if (node instanceof VariableAssignment) {
                variableValues.get(0).put(((VariableAssignment) node).name.name, (Literal) ((VariableAssignment) node).expression);
                bodyNodesToRemove.add(node);
            } else if (node instanceof VariableReference) {
                transformReference((VariableReference) node, stylerule);
            }

            if (node instanceof Declaration) {
                Expression expression = ((Declaration) node).expression;
                if (expression instanceof Operation) {
                    for (ASTNode operatorChild : node.getChildren()) {
                        if (operatorChild instanceof VariableReference) {
                            transformReference((VariableReference) operatorChild, node);
                        }
                    }
                    expression = transformOperation((Operation) expression);
                }else if(expression instanceof VariableReference){
                    expression = variableValues.get(0).get(((VariableReference) expression).name);
                }
                ((Declaration) node).expression = expression;
            }

            if (node instanceof IfClause) {
                transformIfNode((IfClause) node, stylerule, 1);
                stylerule.body.removeIf(childNode -> childNode instanceof IfClause);
                stylerule.body.removeIf(childNode -> childNode instanceof VariableReference);
                bodyNodesToRemove.add(node);
            }
        }
        for (ASTNode node : bodyNodesToRemove) {
            stylerule.removeChild(node);;
        }
    }

    private void transformIfNode(IfClause ifClause, Stylerule parent, int place){
        Expression expression = ifClause.conditionalExpression;
        if(ifClause.conditionalExpression instanceof VariableReference){
            expression = variableValues.get(0).get(((VariableReference) ifClause.conditionalExpression).name);
        }

        if (((BoolLiteral) expression).value) {
            for (ASTNode ifNode : ifClause.body) {
                place++;
                if(ifNode instanceof Declaration){
                    parent.body.add(place, ifNode);
                }else if(ifNode instanceof IfClause) {
                        transformIfNode((IfClause) ifNode, parent, place);
                    }
                }
            }
         else {
            if(ifClause.elseClause != null){
            for (ASTNode ifNode : ifClause.elseClause.body) {
                place++;
                    if (ifNode instanceof Declaration) {
                        parent.body.add(place,ifNode);
                    }else if(ifNode instanceof IfClause) {
                        transformIfNode((IfClause) ifNode, parent, place);
                    }
                }
            }
        }
    }

    private void transformReference(VariableReference ref, ASTNode parent){
        parent.addChild(variableValues.get(0).get(ref.name));
    }
    
    private Expression transformOperation(Operation operation){
        Expression left = operation.lhs;
        Expression right = operation.rhs;

        if(left instanceof VariableReference){
            transformReference((VariableReference)left, operation);
            left = variableValues.get(0).get(((VariableReference) left).name);
        }

        if(right instanceof VariableReference){
            transformReference((VariableReference)right, operation);
            right = variableValues.get(0).get(((VariableReference) right).name);
        }

        if(operation.lhs instanceof Operation){
            left = transformOperation((Operation)operation.lhs);
        }

        if(operation.rhs instanceof Operation){
            right = transformOperation((Operation)operation.rhs);
        }

        if(operation instanceof MultiplyOperation){

            if(left instanceof ScalarLiteral) {
                return newLiteral((Literal) right, getValue(left) * getValue(right));
            }else{
                return newLiteral((Literal) left, getValue(left) * getValue(right));
            }
        }else if(operation instanceof AddOperation){
            if(left instanceof ScalarLiteral) {
                return newLiteral((Literal) right, getValue(left) + getValue(right));
            }else{
                return newLiteral((Literal) left, getValue(left) + getValue(right));
            }
        } else if (operation instanceof SubtractOperation) {
            if(left instanceof ScalarLiteral) {
                return newLiteral((Literal) right, getValue(left) - getValue(right));
            }else{
                return newLiteral((Literal) left, getValue(left) - getValue(right));
            }
        }
        return newLiteral(new ScalarLiteral(0), 0);
    }

    private Literal newLiteral(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        } else {
            return new PercentageLiteral(value);
        }
    }

    private int getValue(Expression ex){
        if (ex instanceof PixelLiteral){
            return ((PixelLiteral) ex).value;
        }else if(ex instanceof PercentageLiteral){
            return ((PercentageLiteral) ex).value;
        }else if(ex instanceof ScalarLiteral){
            return ((ScalarLiteral) ex).value;
        }
        return 0;
    }
}
