package ${data.pkg};

<#if data.execute?contains("user")>
import org.lpw.clivia.user.UserService;
</#if>
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(${data.name}Model.NAME + ".ctrl")
@Execute(name = "${data.uri}", key = ${data.name}Model.NAME, code = "${data.code}")
public class ${data.name}Ctrl {
    @Inject
    private Request request;
    @Inject
    private ${data.name}Service ${data.lowerName}Service;
<#if data.search gt 0 && (data.execute?contains("query") || data.execute?contains("user"))>
    <#assign search=""/>
    <#list data.columns as column>
        <#if column.search?? && column.search==1>
            <#if column.type=="int">
                <#assign search+="request.getAsInt(\""+column.field+"\"), "/>
            <#else>
                <#assign search+="request.get(\""+column.field+"\"), "/>
            </#if>
        </#if>
    </#list>
</#if>
<#if data.execute?contains("query")>

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return ${data.lowerName}Service.query(<#if search??>${search?keep_before_last(",")}</#if>);
    }
</#if>
<#if data.execute?contains("user")>

    @Execute(name = "user", validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return ${data.lowerName}Service.user(<#if search??>${search?keep_before_last(",")}</#if>);
    }
</#if>
<#if data.execute?contains("save")>

    @Execute(name = "save", validates = {
    <#assign code=3 />
    <#list data.columns as column>
        <#if column.require?? && column.require==1>
            <#if column.type=="id">
            @Validate(validator = Validators.ID, parameter = "${column.field}", failureCode = ${code}),
            <#elseif column.type=="date" || column.type=="datetime">
            @Validate(validator = Validators.DATE_TIME, parameter = "${column.field}", failureCode = ${code}),
            <#else>
            @Validate(validator = Validators.NOT_EMPTY, parameter = "${column.field}", failureCode = ${code}),
            </#if>
            <#assign code++ />
        </#if>
        <#if column.type=="string">
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "${column.field}", failureCode = ${code}),
            <#assign code++ />
        </#if>
    </#list>
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        ${data.lowerName}Service.save(request.setToModel(${data.name}Model.class));

        return "";
    }
</#if>
<#if data.execute?contains("delete")>

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        ${data.lowerName}Service.delete(request.get("id"));

        return "";
    }
</#if>
}