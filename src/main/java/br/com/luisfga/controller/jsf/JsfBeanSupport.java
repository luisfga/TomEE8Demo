package br.com.luisfga.controller.jsf;

import java.util.ResourceBundle;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class JsfBeanSupport {
    
    @Inject
    private LocaleBean localeBean;
    
    protected String getMsgText(String bundle,String key) {
        return ResourceBundle.getBundle(bundle,localeBean.getLocale()).getString(key);
    }

    protected String getParameterizedMsgText(String bundle,String key, String[] params) {
        
        String message = ResourceBundle.getBundle(bundle,localeBean.getLocale()).getString(key);
        
        for (int i = 0; i < params.length; i++) {
            String parameterToken = "{"+i+"}";
            message = message.replace(parameterToken, params[i]);
        }
        
        /*
        TODO se for reaproveitar esse método, é uma boa ideia tratar 
        a quantidade de parâmetros, lançando um excessão se necessário.
        */
        
        return message;
    }    
}
