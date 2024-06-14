

with R(n) as (select 1 as n from dual
union all
select n+1 as n from R where n<=19)
select rpad('* ', n*2, '* ' ) from R where n>=1;




==========================

SELECT question_id,
       LISTAGG(element_id, ',') WITHIN GROUP (ORDER BY element_id)
FROM YOUR_TABLE
GROUP BY question_id;

==================

with r(n) as (select 1 as n from dual
union all
select 2 as n from dual)
select LISTAGG(n, ',') within group (order by n) from r;

with r(n) as (select 1 as n from dual
union all
select 2 as n from dual)
select LISTAGG(n, '&') within group (order by n) from r;
