package nl.han.ica.icss.parser;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        ASTNode stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.setRoot((Stylesheet) currentContainer.pop());
    }

    @Override public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        ASTNode styleRule = new Stylerule();
        currentContainer.push(styleRule);
    }
    @Override public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override
    public void enterExpression(ICSSParser.ExpressionContext ctx) {
        ASTNode expression;
        if (ctx.getChildCount() == 3) {
            String operator = ctx.getChild(1).getText();
            if (operator.equals("*")) {
                expression = new MultiplyOperation();
            } else if (operator.equals("+")) {
                expression = new AddOperation();
            } else if (operator.equals("-")) {
                expression = new SubtractOperation();
            } else {
                return;
            }
            currentContainer.push(expression);
        }
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        if(expressionHasTerminalNode(ctx)) {
            ASTNode operation = currentContainer.pop();
            currentContainer.peek().addChild(operation);
        }
    }

    private boolean expressionHasTerminalNode(ICSSParser.ExpressionContext ctx) {
        return ctx.operator() != null;
    }

    @Override public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode declaration = new Declaration();
        currentContainer.push(declaration);
    }
    @Override public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }

    @Override public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
//        ASTNode variableAssignment = new VariableAssignment();
//        currentContainer.push(variableAssignment);
        ASTNode variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }
    @Override public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode ifClause = new IfClause();
        currentContainer.push(ifClause);
    }
    @Override public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        ASTNode elseClause = new ElseClause();
        currentContainer.push(elseClause);
    }
    @Override public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode propertyName = new PropertyName(ctx.getText());
        currentContainer.push(propertyName);
    }

    @Override public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }

    //literals
//    @Override public void enterLiteral(ICSSParser.LiteralContext ctx) { }
//    @Override public void exitLiteral(ICSSParser.LiteralContext ctx) { }
    @Override public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ASTNode colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.peek().addChild((colorLiteral));
    }
//    @Override public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }
    @Override public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        ASTNode percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.peek().addChild((percentageLiteral));
    }
//    @Override public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }
    @Override public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        ASTNode pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.peek().addChild(pixelLiteral);
    }
//    @Override public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }
    @Override public void enterBooleanLiteral(ICSSParser.BooleanLiteralContext ctx) {
        ASTNode booleanLiteral = new BoolLiteral(ctx.getText());
        currentContainer.peek().addChild((booleanLiteral));
    }
//    @Override public void exitBooleanLiteral(ICSSParser.BooleanLiteralContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }
    @Override public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        ASTNode variableReference = new VariableReference(ctx.getText());
        currentContainer.peek().addChild((variableReference));
    }

//    @Override public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }
    @Override public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ASTNode scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.peek().addChild((scalarLiteral));
    }
//    @Override public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
//        ASTNode last = currentContainer.pop();
//        currentContainer.peek().addChild(last);
//    }

    //selectors
//    @Override public void enterSelector(ICSSParser.SelectorContext ctx) { }
//    @Override public void exitSelector(ICSSParser.SelectorContext ctx) { }
    @Override public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        ASTNode idSelector = new IdSelector(ctx.getText());
        currentContainer.push(idSelector);
    }
    @Override public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ASTNode classSelector = new ClassSelector(ctx.getText());
        currentContainer.push(classSelector);
    }
    @Override public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }
    @Override public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        ASTNode defaultCssItemSelector = new TagSelector(ctx.getText());
        currentContainer.push(defaultCssItemSelector);
    }
    @Override public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        ASTNode last = currentContainer.pop();
        currentContainer.peek().addChild(last);
    }

    //operators
//    @Override public void enterAddOperator(ICSSParser.AddOperatorContext ctx) {
//        ASTNode plusOperator = new AddOperation();
//        currentContainer.push(plusOperator);
//    }
//    @Override public void exitAddOperator(ICSSParser.AddOperatorContext ctx) { }
//    @Override public void enterMultiplyOperator(ICSSParser.MultiplyOperatorContext ctx) {
//        ASTNode mulOperator = new MultiplyOperation();
//        currentContainer.push(mulOperator);
//    }
//    @Override public void exitMultiplyOperator(ICSSParser.MultiplyOperatorContext ctx) { }
//    @Override public void enterMinOperator(ICSSParser.MinOperatorContext ctx) {
//        ASTNode minOperator = new SubtractOperation();
//        currentContainer.push(minOperator);
//    }
//    @Override public void exitMinOperator(ICSSParser.MinOperatorContext ctx) { }

    //default stuff
    @Override public void enterEveryRule(ParserRuleContext ctx) { }
    @Override public void exitEveryRule(ParserRuleContext ctx) { }
    @Override public void visitTerminal(TerminalNode node) { }
    @Override public void visitErrorNode(ErrorNode node) { }
}