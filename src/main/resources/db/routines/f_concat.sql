-- dont use date variables
CREATE OR REPLACE FUNCTION f_concat(VARIADIC "any")
    RETURNS text AS 'text_concat'
    LANGUAGE internal IMMUTABLE;
