<#include "base.ftl">
<#macro head>
<head>
    <meta charset="UTF-8">
    <title></title>
</head>
</#macro>
<#macro content>

    <#if loginError>
    <div class="alert alert-danger">Login information incorrect. Please try again.</div>
    </#if>

    <#if logout>
    <div class="alert alert-success">Logout successful.</div>
    </#if>
</#macro>
<#--Renders the page-->
<@display_page userData/>