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
    
}
