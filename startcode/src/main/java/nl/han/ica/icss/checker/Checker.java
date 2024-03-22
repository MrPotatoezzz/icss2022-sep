package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.Node;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.ASTNode;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;



public class Checker<T> {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;
    HashMap<String, ExpressionType> hashMap = new HashMap<>();
    HashMap<String, ExpressionType> globalScope = new HashMap<>();
    public void check(AST ast) {
        variableTypes = new HANLinkedList<>();
        checkTree(ast.root);
        variableTypes.insert(variableTypes.getSize() - 1,hashMap);
    }

    public void checkTree(ASTNode node){
        if(node == null){
            return;
        }

        if(node.getClass() == Stylerule.class){
            //Oude hashmap toevoegen aan lijst
            if(variableTypes.getSize() == 0){
                variableTypes.addFirst(globalScope);
            }else{
                variableTypes.insert(variableTypes.getSize() - 1,hashMap);
            }

            hashMap = new HashMap<>();
        }

        ExpressionType expressionType = ExpressionType.UNDEFINED;

        for (ExpressionType type : ExpressionType.values()) {
            if (type.name().equalsIgnoreCase(node.getNodeLabel())) {
                expressionType = type;
                break;
            }
        }

        if(node.getClass() == VariableAssignment.class){
            globalScope.put(node.getNodeLabel(), expressionType);
        }else {
            hashMap.put(node.getNodeLabel(), expressionType);
        }

        for (ASTNode childNode : node.getChildren()) {
            checkTree(childNode);
        }
    }
}
