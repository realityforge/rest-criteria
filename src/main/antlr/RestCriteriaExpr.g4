grammar RestCriteriaExpr;

top_level_condition: WS* condition WS* EOF;

condition: atomic_condition | NOT WS* condition | '(' WS* condition WS* ')' | condition WS+ op=( AND | OR ) WS+ condition ;

atomic_condition: var_expr WS* op=( EQUALS | NOT_EQUALS ) WS* (var_expr|expr);

var_expr: ID (DOT ID)*;

expr: INT | BOOLEAN | STRING;

BOOLEAN : 'true' | 'false' ;

EQUALS : '=';

NOT_EQUALS : '!=';

DOT : '.';

AND : 'AND';

OR : 'OR';

NOT : 'NOT';

INT     : '-'?[0-9]+ ;

ID  :   ALPHA (ALPHA|DIGIT|[_])* ;      // match identifiers

STRING
    :   '"' ( ESC | ~[\\"] )*? '"'
    |   '\'' ( ESC | ~[\\'] )*? '\''
    ;

fragment
ESC :   '\\' ('"'|'\'')
    ;

fragment
ALPHA :   [a-zA-Z];

fragment
DIGIT:   [0-9];

WS  :   [ \t\n\r]+ ;//-> skip ;
