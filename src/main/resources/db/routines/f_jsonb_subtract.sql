CREATE OR REPLACE FUNCTION f_jsonb_subtract(arg1 jsonb, arg2 jsonb) RETURNS jsonb AS
$$ SELECT COALESCE(json_object_agg(key, value), '{}')::jsonb
     FROM jsonb_each(arg1)
    WHERE arg1 -> key != arg2 -> key
       OR arg2 -> key IS NULL
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION f_jsonb_subtract_recursive(arg1 jsonb, arg2 jsonb) RETURNS jsonb AS
$$ SELECT COALESCE(json_object_agg(
        key,
        CASE WHEN jsonb_typeof(value) = 'object' AND arg2 -> key IS NOT NULL
			 THEN f_jsonb_subtract_recursive(value, arg2 -> key)
             ELSE value
         END
    ), '{}')::jsonb
     FROM jsonb_each(arg1)
    WHERE arg1 -> key != arg2 -> key
       OR arg2 -> key IS NULL
$$ LANGUAGE SQL;

DROP OPERATOR IF EXISTS - (jsonb, jsonb);
CREATE OPERATOR - (PROCEDURE = f_jsonb_subtract, LEFTARG = jsonb, RIGHTARG = jsonb);
