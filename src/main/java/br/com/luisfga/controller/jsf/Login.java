package br.com.luisfga.controller.jsf;

import br.com.luisfga.service.LoginUseCase;
import br.com.luisfga.service.exceptions.LoginException;
import br.com.luisfga.service.exceptions.PendingEmailConfirmationException;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

@Named
@RequestScoped
public class Login extends JsfBeanSupport{

    private static final Logger logger = Logger.getLogger(Login.class.getName());
    
    @EJB LoginUseCase loginUseCase;
    
    private String token;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    private String password;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String logout(){
        loginUseCase.logout();
        return "/index?faces-redirect=true";
    }
    
    public String execute() {
        
        try {

            loginUseCase.login(email, password);
            
        } catch (LoginException le) { 

            // Bring the information message using the Faces Context
            String errorMessage = getMsgText("global", "action.error.authentication.exception");
            
            // Add View Faces Message
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            // The component id is null, so this message is considered as a view message
            FacesContext.getCurrentInstance().addMessage(null, message);
            // Return empty token for navigation handler

            return "login";
            
        } catch (PendingEmailConfirmationException pecException) {
            
            // Bring the information message using the Faces Context
            String errorMessage = getMsgText("global", "action.error.pending.email.confirmation");
            
            // Add View Faces Message
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            // The component id is null, so this message is considered as a view message
            FacesContext.getCurrentInstance().addMessage(null, message);            

            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            loginUseCase.enviarEmailConfirmacaoNovoUsuario(ctx.getContextPath(),email);

            return "login"; // Return empty token for navigation handler
            
        }
        
        return "/secure/dashboard?faces-redirect=true";
    }
}
