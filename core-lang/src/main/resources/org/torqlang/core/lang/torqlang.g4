grammar torqlang;

//******************//
//   PARSER RULES   //
//******************//

program: sntc_or_expr EOF;

sntc_or_expr: meta? assign ';'*;

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

select_or_apply: access (('.' access) | ('[' sntc_or_expr ']') |
                 ('(' arg_list? ')'))*;

access: '@' access | construct;

construct: keyword | ident | value;

keyword: act | actor | begin | 'break' | case | 'continue' |
         for | func | group | if | import_ | local | proc |
         throw | return | 'self' | 'skip' | spawn | try | var |
         while;

act: 'act' sntc_or_expr+ 'end';

actor: 'actor' ident? '(' pat_list? ')' 'in'
       (sntc_or_expr | message_handler)+ 'end';

message_handler: tell_handler | ask_handler;

tell_handler: 'tell' pat
              'in' sntc_or_expr+ 'end';

ask_handler: 'ask' pat return_type_anno?
             'in' sntc_or_expr+ 'end';

begin: 'begin' sntc_or_expr+ 'end';

case: 'case' sntc_or_expr
      ('of' pat ('if' sntc_or_expr)? 'then' sntc_or_expr+)+
      ('else' sntc_or_expr+)? 'end';

for: 'for' pat 'in' sntc_or_expr 'do' sntc_or_expr+ 'end';

func: 'func' ident? '(' pat_list? ')' return_type_anno?
      'in' sntc_or_expr+ 'end';

group: '(' sntc_or_expr+ ')';

if: 'if' sntc_or_expr 'then' sntc_or_expr+
    ('elseif' sntc_or_expr 'then' sntc_or_expr+)*
    ('else' sntc_or_expr+)? 'end';

// `import` is an ANTLR4 keyword, therefore we add an underscore
import_: 'import' '(' arg_list ')';

local: 'local' var_decl (',' var_decl)*
       'in' sntc_or_expr+ 'end';

var_decl: pat ('=' sntc_or_expr)?;

proc: 'proc' ident? '(' pat_list? ')'
      'in' sntc_or_expr+ 'end';

throw: 'throw' sntc_or_expr;

return: 'return' sntc_or_expr?;

spawn: 'spawn' '(' arg_list ')';

try: 'try' sntc_or_expr+ ('catch' pat 'then' sntc_or_expr+)*
     ('finally' sntc_or_expr+)? 'end';

var: 'var' var_decl (',' var_decl)*;

while: 'while' sntc_or_expr 'do' sntc_or_expr+ 'end';

arg_list: sntc_or_expr (',' sntc_or_expr)*;

pat_list: pat (',' pat)*;

pat: rec_pat | tuple_pat |
     (label_pat ('#' (rec_pat | tuple_pat))?) |
     INT_LITERAL | (ident var_type_anno?);

label_pat: ('~' ident) | bool | STR_LITERAL | 'eof' | 'nothing';

rec_pat: '{' (field_pat (',' field_pat)* (',' '...')?)? '}';

tuple_pat: '[' (pat (',' pat)* (',' '...')?)? ']';

field_pat: (feat_pat ':')? pat;

feat_pat: ('~' ident) | bool | INT_LITERAL | STR_LITERAL |
          'eof' | 'nothing';

value: rec_value | tuple_value |
       (label_value ('#' (rec_value | tuple_value))?) |
       INT_LITERAL | CHAR_LITERAL | FLT_LITERAL | DEC_LITERAL;

label_value: ident | bool | STR_LITERAL | 'eof' | 'nothing';

rec_value: '{' (field_value (',' field_value)*)? '}';

tuple_value: '[' (value (',' pat)*)? ']';

field_value: (feat_value ':')? sntc_or_expr;

feat_value: ident | bool | INT_LITERAL | STR_LITERAL |
            'eof' | 'nothing';

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
