create table kv
(
    id           bigserial     not null primary key,
    ext_id       bigint        not null,

    -- must be consistent with KvEntity.generateUniqueId
    unique_id    text          not null
        generated always as (md5(f_concat(type, county, area, district, address, rooms, room_size, floor))) STORED,
    -- must be consistent with KvEntity.generateChangeId
    change_id    text          not null
        generated always as (md5(f_concat(booked, removed, img_link, price, price_per_m2, county, area, district,
                                          address, rooms, room_size, floor, floor_total, year))) STORED,

    type         text          not null,
    publish_date timestamp     not null,
    booked       boolean       not null,
    removed      boolean       not null,

    link         text          not null,
    img_link     text          not null,
    price        numeric(8, 2) not null,
    price_per_m2 numeric(8, 2),

    county       text          not null,
    area         text          not null,
    district     text,
    address      text,

    rooms        integer       not null,
    room_size    numeric(8, 2),
    floor        integer,
    floor_total  integer,

    year         integer,
    details      text,
    description  text
);

create unique index kv_ext_type_county_unique_idx on kv (ext_id, type, county);
create index kv_unique_id_idx on kv (unique_id);
create index kv_type_idx on kv (type);
create index kv_county_idx on kv (county);

select enable_audit('kv', 'unique_id', 'change_id');
create index kv_audit_price_idx on audit ((changes -> 'price')) where changes -> 'price' is not null;