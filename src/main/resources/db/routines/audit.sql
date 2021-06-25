create table if not exists audit
(
    event_id   bigserial not null primary key,
    id         bigint    not null,
    table_name text      not null,
    time       timestamp not null,
    action     text      not null check ( action IN ('INSERT', 'UPDATE', 'DELETE') ),
    changes    jsonb
);

drop index if exists audit_id_idx;
create index audit_id_idx ON audit (id);
drop index if exists audit_table_name_idx;
create index audit_table_name_idx ON audit (table_name);

create or replace function audit() returns trigger as
$$
declare
    audit_row     audit;
    excluded_cols text[] = array []::text[];
begin
    if tg_when != 'AFTER' then
        raise exception '[audit] - May only run as an AFTER trigger';
    end if;

    if tg_argv[0] is not null then
        excluded_cols = tg_argv[0]::text[];
    end if;

    audit_row = row (
        nextval('audit_event_id_seq'),
        case when tg_op = 'DELETE' then old.id else new.id end,
        tg_table_name::text,
        coalesce(new.publish_date, now()),
        tg_op,
        null
        );

    if tg_op = 'INSERT' and tg_level = 'ROW' then
        audit_row.changes = row_to_json(NEW)::jsonb - excluded_cols;
    elsif tg_op = 'DELETE' and tg_level = 'ROW' then
        audit_row.changes = null;
    elsif tg_op = 'UPDATE' and tg_level = 'ROW' then
        audit_row.changes = (row_to_json(NEW)::jsonb - row_to_json(OLD)::jsonb) - excluded_cols;
        if (audit_row.changes = '{}') then
            return null;
        end if;
    else
        raise exception '[audit] - Trigger func added as trigger for unhandled case: %, %', tg_op, tg_level;
    end if;

    INSERT INTO audit VALUES (audit_row.*);
    return null;
end;
$$ language plpgsql;

create or replace function enable_audit(target_table regclass, variadic excluded_cols text[]) returns void as
$$
declare
    trigger_sql  text;
    ignored_cols text = '';
begin
    execute 'DROP TRIGGER IF EXISTS audit_trigger ON ' || target_table;

    if array_length(excluded_cols, 1) > 0 then
        ignored_cols = quote_literal(excluded_cols);
    end if;

    trigger_sql = 'CREATE TRIGGER audit_trigger AFTER INSERT OR UPDATE OR DELETE ON ' || target_table ||
                  ' FOR EACH ROW EXECUTE PROCEDURE audit(' || ignored_cols || ');';
    raise notice '%', trigger_sql;
    execute trigger_sql;
end;
$$ language plpgsql;
