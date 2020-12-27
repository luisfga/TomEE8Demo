package br.com.luisfga.jsf;

import java.util.ResourceBundle;

public class JsfBeanSupport {
    
    protected String getMsgText(String bundle,String key) {
        return ResourceBundle.getBundle(bundle).getString(key);
    }
    
}
