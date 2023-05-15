<#list data.columns as column>
${data.beanName}.${column.field}=${codec.unicode(column.explain)}
</#list>