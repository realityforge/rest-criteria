grammar RestCriteriaExpr;

top_level_condition: WS* condition WS* EOF;

condition:
  '(' WS* condition WS* ')' |
  op=NOT WS* condition |
  condition WS+ op=AND WS+ condition  |
  condition WS+ op=OR WS+ condition  |
  atomic_condition;

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

ID  :   (DOLLAR|ALPHA) (DOLLAR|ALPHA|DIGIT|[_])* ;      // match identifiers

STRING
    :   '"' ( ESC | ~[\\"] )*? '"'
    |   '\'' ( ESC | ~[\\'] )*? '\''
    ;

fragment
ESC :   '\\' ('"'|'\'')
    ;

fragment
DOLLAR :   '$';

fragment
ALPHA :   [a-zA-Z];

fragment
DIGIT:   [0-9];

WS  :   [ \t\n\r]+ ;//-> skip ;
