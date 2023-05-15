DROP TABLE IF EXISTS t_${data.table};
CREATE TABLE t_${data.table}
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
<#list data.columns as column>
  c_${column.name} ${column.sqlType} COMMENT '${column.explain}',
</#list>

  PRIMARY KEY pk(c_id) USING HASH<#if data.key gt 0>,</#if>
<#assign key=data.key/>
<#list data.columns as column>
  <#if column.key?? && column.key gt 0>
    <#assign key--/>
    <#if column.key==1>
  KEY k_${column.name}(c_${column.name}) USING HASH<#if key gt 0>,</#if>
    <#elseif column.key==2>
  UNIQUE KEY uk_${column.name}(c_${column.name}) USING HASH<#if key gt 0>,</#if>
    </#if>
  </#if>
</#list>
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;