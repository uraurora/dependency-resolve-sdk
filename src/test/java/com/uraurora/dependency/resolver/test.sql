select
    'FBI函数接入数',
	y.${dateDim} as `日期`,
	count(1) as `函数接入数`
from nest_function f
JOIN yuntu_dt y
ON
	date_format(f.create_time, '%Y-%m-%d')=y.day_date
WHERE
    y.${dateDim} <= CURDATE()  AND
    y.${dateDim} >= CONCAT(YEAR(CURDATE()),'-01-01') AND
  	f.id in
(
    SELECT b.id
    FROM nest_serverless_service a
    LEFT JOIN nest_function b
    ON a.id=b.service_id
    WHERE b.occasion_id=6 ORDER BY a.app_key
)
group by `日期`
ORDER by `日期`

SELECT
  'S/A类集群签署率(%)' as '集群等级',
  case '${date_dim}' WHEN 'week' THEN DATE_FORMAT(ssis.stat_date,'%x-第%v周') WHEN 'day' THEN DATE_FORMAT(ssis.stat_date,'%Y-%m-%d') WHEN 'month' THEN DATE_FORMAT(ssis.stat_date, '%Y-%m') end,
  round(sum(CASE
       WHEN signed_status =2 THEN 1
       ELSE 0
      END )/ count(*) * 100,2) AS '签署率(%)',
            sum(CASE
       WHEN signed_status =2 THEN 1
       ELSE 0
      END ) as '已签署集群个数',
      COUNT(*) as 'S/A类集群总数'
FROM
  stat_sign_info_snapshot as ssis,
  database_servicegroup as dsg
WHERE
  ssis.stat_date>='${start_date}'
  AND ssis.stat_date<='${end_date}'
  AND dsg.id=ssis.service_group_id
  AND dsg.sg_level in ('S', 'A')
  AND dsg.is_test = 0
GROUP BY ssis.stat_date;

select

SELECT
    'FBI函数接入总览'
    count(a.app_key) as `服务数`,
    count(b.name) AS `函数数`
FROM
    serverless_service a
LEFT JOIN function b
ON a.id=b.service_id
WHERE
b.occasion_id=6