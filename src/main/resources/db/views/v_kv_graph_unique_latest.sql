CREATE VIEW v_kv_graph_unique_latest AS
WITH kv_unique_latest AS (
    SELECT *
          ,row_number() OVER (PARTITION BY unique_id ORDER BY publish_date DESC) rn
      FROM kv
)
SELECT concat_ws(' - ', concat_ws(', ', initcap(county), area, district, address), rooms, room_size, floor) AS full_name
      ,*
  FROM kv_unique_latest
 WHERE rn = 1;
