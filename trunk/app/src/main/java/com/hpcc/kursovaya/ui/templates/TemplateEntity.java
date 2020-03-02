package com.hpcc.kursovaya.ui.templates;
//!!sample class!! you should use realm class instead
public class TemplateEntity{
    public String getTemplateName() {
        return templateName;
    }

    public String getTemplateWeek() {
        return templateWeek;
    }

    String templateName;
    String templateWeek;


    public TemplateEntity(String templateName) {
        this.templateName = templateName;

    }
}
