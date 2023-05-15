package ${data.pkg};

<#if data.execute?contains("query") || data.execute?contains("user")>
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
</#if>
<#if data.execute?contains("save")>
import org.lpw.photon.util.Validator;
</#if>
import org.springframework.stereotype.Service;

import javax.inject.Inject;
<#if data.date==2>
import java.sql.Date;
</#if>
<#if data.timestamp==2>
import java.sql.Timestamp;
</#if>

@Service(${data.name}Model.NAME + ".service")
public class ${data.name}ServiceImpl implements ${data.name}Service {
<#if data.execute?contains("save")>
    @Inject
    private Validator validator;
</#if>
<#if data.execute?contains("query") || data.execute?contains("user")>
    @Inject
    private Pagination pagination;
    <#if data.search gt 0>
        <#assign search=""/>
        <#list data.columns as column>
            <#if column.search?? && column.search==1>
                <#assign search+=column.field+", "/>
            </#if>
        </#list>
    </#if>
</#if>
    @Inject
    private ${data.name}Dao ${data.lowerName}Dao;
<#if data.execute?contains("query")>

    @Override
    public JSONObject query(<#if data.searchArg??>${data.searchArg?keep_before_last(",")}</#if>) {
        return ${data.lowerName}Dao.query(<#if search??>${search}</#if>pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }
</#if>
<#if data.execute?contains("user")>

    @Override
    public JSONObject user(<#if data.searchArg??>${data.searchArg?keep_before_last(",")}</#if>) {
        return ${data.lowerName}Dao.query(<#if search??>${search}</#if>pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }
</#if>
<#if data.execute?contains("save")>

    @Override
    public void save(${data.name}Model ${data.lowerName}) {
        ${data.name}Model model = validator.isId(${data.lowerName}.getId()) ? ${data.lowerName}Dao.findById(${data.lowerName}.getId()) : null;
        if (model == null)
            ${data.lowerName}.setId(null);
        ${data.lowerName}Dao.save(${data.lowerName});
    }
</#if>
<#if data.execute?contains("delete")>

    @Override
    public void delete(String id) {
        ${data.lowerName}Dao.delete(id);
    }
</#if>
}
