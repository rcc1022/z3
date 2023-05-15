{
    "key": "${data.beanName}",
    "uri": "${data.uri}",
    "props": [
<#list data.columns as column>
        {
            "name": "${column.field}"
        }<#if column_index<data.columns?size-1>,</#if>
</#list>
    ]<#if data.execute?? && data.execute?length gt 0>,</#if>
<#if data.execute?contains("query")>
    "query": {
        "type": "grid"<#if data.search gt 0 || data.execute?contains("save") || data.execute?contains("delete")>,</#if>
    <#if data.search gt 0>
        "search": [
        <#assign s=data.search/>
        <#list data.columns as column>
            <#if column.search?? && column.search==1>
                <#assign s--/>
            {
                "name": "${column.field}"
            }<#if s gt 0>,</#if>
            </#if>
        </#list>
        ]<#if data.execute?contains("save") || data.execute?contains("delete")>,</#if>
    </#if>
    <#if data.execute?contains("save") || data.execute?contains("delete")>
        "ops": [
        <#if data.execute?contains("save")>
            {
                "type": "modify"
            }<#if data.execute?contains("delete")>,</#if>
        </#if>
        <#if data.execute?contains("delete")>
            {
                "type": "delete"
            }
        </#if>
        ]<#if data.execute?contains("save")>,</#if>
    </#if>
    <#if data.execute?contains("save")>
        "toolbar": [
            {
                "type": "create"
            }
        ]
    </#if>
    }<#if data.execute?contains("save") || data.execute?contains("user")>,</#if>
</#if>
<#if data.execute?contains("save")>
    "create": {
        "type": "form",
        "toolbar": [
            {
                "type": "save",
                "success": "query"
            }
        ]
    },
    "modify": {
        "type": "form",
        "toolbar": [
            {
                "type": "save",
                "success": "query"
            }
        ]
    }<#if data.execute?contains("user")>,</#if>
</#if>
<#if data.execute?contains("user")>
    "user": {
        "type": "grid"<#if data.search gt 0>,</#if>
    <#if data.search gt 0>
        "search": [
        <#assign s=data.search/>
        <#list data.columns as column>
            <#if column.search?? && column.search==1>
                <#assign s--/>
            {
                "name": "${column.field}"
            }<#if s gt 0>,</#if>
            </#if>
        </#list>
        ]
    </#if>
    }
</#if>
}
