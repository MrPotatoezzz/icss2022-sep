package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

public class Generator {
	public String generate(AST ast) {
         return generateAST(ast.root);
	}

    private String generateAST(ASTNode node){
        StringBuilder css = new StringBuilder();
        for (ASTNode childNode: node.getChildren()) {
            if(childNode instanceof Stylerule){
                for (ASTNode selector: ((Stylerule) childNode).selectors) {
                    if(selector instanceof ClassSelector){
                        css.append(((ClassSelector) selector).cls);
                    }else if(selector instanceof IdSelector){
                        css.append(((IdSelector) selector).id);
                    } else if (selector instanceof TagSelector) {
                        css.append(((TagSelector) selector).tag);
                    }
                    css.append(" {\n");
                }

                for (ASTNode declaration: ((Stylerule) childNode).body) {
                     if (declaration instanceof Declaration) {
                         css.append("  ").append(((Declaration) declaration).property.name).append(": ");
                         Expression expression = ((Declaration) declaration).expression;
                         if(expression instanceof ColorLiteral){
                             css.append(((ColorLiteral) expression).value).append(";\n");
                         }else if(expression instanceof PercentageLiteral){
                             css.append(((PercentageLiteral) expression).value).append("%;\n");
                         } else if (expression instanceof ScalarLiteral) {
                             css.append(((ScalarLiteral) expression).value).append(";\n");
                         }else if(expression instanceof PixelLiteral) {
                             css.append(((PixelLiteral) expression).value).append("px;\n");
                         }
                     }
                }
                css.append("}\n");
            }
        }
        return css.toString();
    }
}
