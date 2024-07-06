grammar torqlang;

// Although this grammar may be used to create parsers, it was derived from the hand-written
// Torqlang lexer and parser for documentation purposes.

//******************//
//   PARSER RULES   //
//******************//

program: stmt_or_expr EOF;

stmt_or_expr: meta? assign ';'*;

meta: 'meta' '#' (meta_rec | meta_tuple);

meta_rec: '{' meta_field (',' meta_field)* '}';

meta_tuple: '[' meta_value (',' meta_value)* ']';

meta_field: STR_LITERAL ':' meta_value;

meta_value: meta_rec | meta_tuple | bool |
            STR_LITERAL | INT_LITERAL;

assign: or ('=' or | ':=' or)?;

or: and ('||' and)*;

and: relational ('&&' relational)*;

relational: sum (relational_oper sum)*;

relational_oper: '<' | '>' | '==' | '!=' | '<=' | '>=';

sum: product (sum_oper product)*;

sum_oper: '+' | '-';

product: unary (product_oper unary)*;

product_oper: '*' | '/' | '%';

unary: select_or_apply | (negate_or_not unary)+;

negate_or_not: '-' | '!';

select_or_apply: access (('.' access) | ('[' stmt_or_expr ']') |
                 ('(' arg_list? ')'))*;

access: '@' access | construct;

construct: keyword | ident | value;

keyword: act | actor | begin | 'break' | case | 'continue' |
         for | func | group | if | import_ | local | proc |
         throw | return | 'self' | 'skip' | spawn | try | var |
         while;

act: 'act' stmt_or_expr+ 'end';

actor: 'actor' ident? '(' pat_list? ')' 'in'
       (stmt_or_expr | message_handler)+ 'end';

message_handler: 'handle' (tell_handler | ask_handler);

tell_handler: 'tell' pat ('when' stmt_or_expr)?
              'in' stmt_or_expr+ 'end';

ask_handler: 'ask' pat ('when' stmt_or_expr)? return_type_anno?
             'in' stmt_or_expr+ 'end';

begin: 'begin' stmt_or_expr+ 'end';

case: 'case' stmt_or_expr
      ('of' pat ('when' stmt_or_expr)? 'then' stmt_or_expr+)+
      ('else' stmt_or_expr+)? 'end';

for: 'for' pat 'in' stmt_or_expr 'do' stmt_or_expr+ 'end';

func: 'func' ident? '(' pat_list? ')' return_type_anno?
      'in' stmt_or_expr+ 'end';

group: '(' stmt_or_expr+ ')';

if: 'if' stmt_or_expr 'then' stmt_or_expr+
    ('elseif' stmt_or_expr 'then' stmt_or_expr+)*
    ('else' stmt_or_expr+)? 'end';

// `import` is already an ANTLR4 keyword, therefore we create our keyword
// with a trailing underscore
import_: 'import' ident ('.' ident)* ('[' import_alias (',' import_alias)* ']')?;

import_alias: ident ('as' ident)?;

local: 'local' var_decl (',' var_decl)*
       'in' stmt_or_expr+ 'end';

var_decl: pat ('=' stmt_or_expr)?;

proc: 'proc' ident? '(' pat_list? ')'
      'in' stmt_or_expr+ 'end';

throw: 'throw' stmt_or_expr;

return: 'return' stmt_or_expr?;

spawn: 'spawn' '(' arg_list ')';

try: 'try' stmt_or_expr+ ('catch' pat 'then' stmt_or_expr+)*
     ('finally' stmt_or_expr+)? 'end';

var: 'var' var_decl (',' var_decl)*;

while: 'while' stmt_or_expr 'do' stmt_or_expr+ 'end';

arg_list: stmt_or_expr (',' stmt_or_expr)*;

pat_list: pat (',' pat)*;

pat: rec_pat | tuple_pat |
     (label_pat ('#' (rec_pat | tuple_pat))?) |
     INT_LITERAL | (ident var_type_anno?);

label_pat: ('~' ident) | bool | STR_LITERAL | 'eof' | 'null';

rec_pat: '{' (field_pat (',' field_pat)* (',' '...')?)? '}';

tuple_pat: '[' (pat (',' pat)* (',' '...')?)? ']';

field_pat: (feat_pat ':')? pat;

feat_pat: ('~' ident) | bool | INT_LITERAL | STR_LITERAL |
          'eof' | 'null';

value: rec_value | tuple_value |
       (label_value ('#' (rec_value | tuple_value))?) |
       INT_LITERAL | CHAR_LITERAL | FLT_LITERAL | DEC_LITERAL;

label_value: ident | bool | STR_LITERAL | 'eof' | 'null';

rec_value: '{' (field_value (',' field_value)*)? '}';

tuple_value: '[' (value (',' pat)*)? ']';

field_value: (feat_value ':')? stmt_or_expr;

feat_value: ident | bool | INT_LITERAL | STR_LITERAL |
            'eof' | 'null';

var_type_anno: '::' ident;

return_type_anno: '->' ident;

bool: 'true' | 'false';

ident: IDENT | 'ask' | 'tell';

//*****************//
//   LEXER RULES   //
//*****************//

IDENT: ((ALPHA | '_') (ALPHA_NUMERIC | '_')*) |
       '`' (~('`' | '\\') | ESC_SEQ)+ '`';

CHAR_LITERAL: '&' (~'\\' | ESC_SEQ);

STR_LITERAL: STR_SINGLE_QUOTED | STR_DOUBLE_QUOTED;
STR_SINGLE_QUOTED: '\'' (~('\'' | '\\') | ESC_SEQ)* '\'';
STR_DOUBLE_QUOTED: '"' (~('"' | '\\') | ESC_SEQ)* '"';

INT_LITERAL: DIGIT+ [lL]? |
             ('0x' | '0X') HEX_DIGIT+ [lL]?;

FLT_LITERAL: DIGIT+ '.' DIGIT+
             ([eE] ('+' | '-')? DIGIT+)? [fFdD]?;

DEC_LITERAL: DIGIT+ ('.' DIGIT+)? [mM]?;

//
// Define lexical rules for comments and whitespace
//

LINE_COMMENT : '//' .*? '\r'? '\n' -> skip;
COMMENT : '/*' .*? '*/' -> skip;
WS: [ \r\n\t\f\b]+ -> skip;

//
// Define reusable lexical patterns
//

fragment ALPHA_NUMERIC: ALPHA | DIGIT;

fragment ALPHA: UPPER_CASE_ALPHA | LOWER_CASE_ALPHA;
fragment UPPER_CASE_ALPHA: [A-Z];
fragment LOWER_CASE_ALPHA: [a-z];

fragment DIGIT: [0-9];
fragment NZ_DIGIT: [1-9];
fragment HEX_DIGIT: (DIGIT | [a-f] | [A-F]);

fragment ESC_SEQ: '\\' ([rntfb'"`\\] |
                  'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT);
