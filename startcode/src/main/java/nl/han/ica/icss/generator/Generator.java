package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {
    private final StringBuilder stringBuilder = new StringBuilder();
	public String generate(AST ast) {
         return generateAST(ast.root);
	}

    private String generateAST(ASTNode node){
        String css = "";
        for (ASTNode childNode: node.getChildren()) {
            if(childNode instanceof Stylerule){
                for (ASTNode selector: ((Stylerule) childNode).selectors) {
                    if(selector instanceof ClassSelector){
                        css += ((ClassSelector) selector).cls;
                    }else if(selector instanceof IdSelector){
                        css += ((IdSelector) selector).id;
                    } else if (selector instanceof TagSelector) {
                        css += ((TagSelector) selector).tag;
                    }
                    css += "{\n";
                }

                for (ASTNode declaration: ((Stylerule) childNode).body) {
                     if (declaration instanceof Declaration) {
                         css += ((Declaration) declaration).property.name + ": ";
                         Expression expression = ((Declaration) declaration).expression;
                         if(expression instanceof ColorLiteral){
                             css += ((ColorLiteral) expression).value + ";\n";
                         }else if(expression instanceof PercentageLiteral){
                             css += ((PercentageLiteral) expression).value + "%;\n";
                         } else if (expression instanceof ScalarLiteral) {
                             css += ((ScalarLiteral) expression).value + ";\n";
                         }else if(expression instanceof PixelLiteral) {
                             css += ((PixelLiteral) expression).value + "px;\n";
                         }
                     }
                }
                css += "}\n\n";
            }
        }
        return css;
    }
}
