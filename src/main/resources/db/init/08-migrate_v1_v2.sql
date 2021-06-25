CREATE OR REPLACE PROCEDURE p_migrate_v1_v2() AS
$$
DECLARE
    rec record;
BEGIN
    RAISE NOTICE 'Inserting kv items based on earliest kvitem and kvchangeitem';
    INSERT INTO kv (ext_id, type, publish_date, booked, removed, link, img_link, price, price_per_m2,
                    county, area, district, address, rooms, room_size, floor, floor_total)
         SELECT k.externalid, UPPER(k.dealtype), c.publishdate, c.broneeritud, k.removed, k.link, c.imglink,
                c.price, c.priceperm2, UPPER(k.county), k.area, k.district, k.address, k.rooms, k.roomsize,
                k.roomfloor, k.totalfloor
           FROM kvitem k
           JOIN kvchangeitem c ON k.id = c.kvitem_id
          WHERE c.insertdate = (SELECT MIN(c2.insertdate) FROM kvchangeitem c2 WHERE c2.kvitem_id = k.id)
          ORDER BY k.id;

    RAISE NOTICE 'Updating kv items based on kvchangeitems';
    FOR rec IN (SELECT *
                  FROM kvitem k
                  JOIN kvchangeitem c ON k.id = c.kvitem_id
                 WHERE c.insertdate != (SELECT MIN(c2.insertdate) FROM kvchangeitem c2 WHERE c2.kvitem_id = k.id)
                 ORDER BY c.insertdate) LOOP

        UPDATE kv SET img_link = rec.imglink
                     ,price = rec.price
                     ,price_per_m2 = rec.priceperm2
                     ,booked = rec.broneeritud
                     ,publish_date = rec.publishdate
         WHERE ext_id = rec.externalid
           AND type = UPPER(rec.dealtype);
    END LOOP;
END
$$ LANGUAGE plpgsql;

CALL p_migrate_v1_v2();