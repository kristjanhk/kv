create table if not exists kvitem
(
    id             bigserial    not null primary key,
    address        varchar(255),
    area           varchar(255),
    county         varchar(255),
    dealtype       varchar(255) not null,
    district       varchar(255),
    externalid     bigint       not null,
    insertdate     timestamp    not null,
    link           varchar(255) not null,
    prevexternalid bigint,
    removed        boolean      not null,
    removeddate    timestamp,
    roomfloor      integer,
    roomsize       double precision,
    rooms          integer      not null,
    totalfloor     integer
);

drop index if exists kvitem_externalid_idx;
create index kvitem_externalid_idx on kvitem (externalid);

create table if not exists kvchangeitem
(
    id          bigserial    not null primary key,
    imglink     varchar(255) not null,
    insertdate  timestamp    not null,
    price       double precision,
    priceperm2  double precision,
    publishdate timestamp    not null,
    kvitem_id   bigint       not null references kvitem,
    broneeritud boolean      not null default false
);

drop index if exists kvchangeitem_kvitem_id_idx;
create index kvchangeitem_kvitem_id_idx on kvchangeitem (kvitem_id);
drop index if exists kvchangeitem_insert_date_idx;
create index kvchangeitem_insert_date_idx on kvchangeitem (insertdate)
