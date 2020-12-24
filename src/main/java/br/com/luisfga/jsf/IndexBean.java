package br.com.luisfga.jsf;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author luisfga
 */
@Named
@RequestScoped
public class IndexBean {
    
    private String message = "hi";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
    
}
