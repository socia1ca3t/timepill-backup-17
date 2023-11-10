package com.socia1ca3t.timepillbackup.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Slf4j
@Component
public class TemplateUtil {

    private static TemplateEngine templateEngine;

    @Autowired
    public TemplateUtil(SpringTemplateEngine templateEngine) {
        TemplateUtil.templateEngine = templateEngine;
    }

    public static String render2html(final Map map, String templateName) {

        Context context = new Context();
        context.setVariables(map);
        return templateEngine.process(templateName, context);
    }

    public static String render2html(final Context context, String templateName) {

        return templateEngine.process(templateName, context);
    }

}
