grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
//stylesheet: variableAssesment* stylerule* EOF;
//text: LOWER_IDENT+ | CAPITAL_IDENT+;
//head: selector;
//body: OPEN_BRACE declaration* CLOSE_BRACE;
//declaration: (text COLON (literal | expression) SEMICOLON ) | ifClause;
//literal: COLOR | PERCENTAGE | PIXELSIZE | TRUE | FALSE | text | SCALAR;
//stylerule: head body;
//variableAssesment: propertyName ASSIGNMENT_OPERATOR literal SEMICOLON;
//operator: PLUS | MIN | MUL;
//expression: literal operator (expression | literal);
//ifClause: IF BOX_BRACKET_OPEN literal BOX_BRACKET_CLOSE body elseClause?;
//elseClause: ELSE body elseClause?;
//propertyName: text;
//selector: ID_IDENT | CLASS_IDENT | text;

//stylesheet: variableAssesment* stylerule* EOF;
stylesheet: variableAssignment* stylerule* EOF;

stylerule: selector OPEN_BRACE declaration* CLOSE_BRACE;
declaration: (propertyName COLON (variableReference | literal | expression) SEMICOLON ) | ifClause;

//variableAssesment: propertyName ASSIGNMENT_OPERATOR literal SEMICOLON;
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression SEMICOLON;

expression: (literal operator (expression | literal)) | literal;
ifClause: IF BOX_BRACKET_OPEN literal BOX_BRACKET_CLOSE OPEN_BRACE declaration* CLOSE_BRACE elseClause?;
elseClause: ELSE OPEN_BRACE declaration* CLOSE_BRACE elseClause?;
propertyName: CAPITAL_IDENT+ | LOWER_IDENT+;

//literals
//literal: colorLiteral | percentageLiteral | pixelLiteral | booleanLiteral | variableReference | scalarLiteral;
literal: colorLiteral | percentageLiteral | pixelLiteral | booleanLiteral | variableReference | scalarLiteral;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
booleanLiteral: TRUE | FALSE;

//variableReference: propertyName;
variableReference: CAPITAL_IDENT+ | LOWER_IDENT+;

scalarLiteral: SCALAR;

//selectors
selector: idSelector | classSelector | tagSelector;
idSelector: ID_IDENT;
classSelector: CLASS_IDENT;
tagSelector: LOWER_IDENT+ | CAPITAL_IDENT+;

//operations
operator: multiplyOperator | addOperator | minOperator;
addOperator: PLUS;
multiplyOperator: MUL;
minOperator: MIN;