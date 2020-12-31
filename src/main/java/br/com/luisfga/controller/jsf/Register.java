package br.com.luisfga.controller.jsf;

import br.com.luisfga.service.RegisterUseCase;
import br.com.luisfga.service.exceptions.EmailAlreadyTakenException;

import java.time.LocalDate;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@RequestScoped
public class Register extends JsfBeanSupport{

    @EJB 
    private RegisterUseCase registerUseCase;
    
    @Inject
    private LocaleBean localeBean;

    private String token;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    private LocalDate birthday;
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    private String userName;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
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
    
    private String passwordConfirmation;
    public String getPasswordConfirmation() { return passwordConfirmation; }
    public void setPasswordConfirmation(String passwordConfirmation) { this.passwordConfirmation = passwordConfirmation; }
    
    public void validatePasswordEquality(ComponentSystemEvent event){
        
        FacesContext fc = FacesContext.getCurrentInstance();

        UIComponent components = event.getComponent();

        // get password
        UIInput uiInputPassword = (UIInput) components.findComponent("password");
        String passwordVal = uiInputPassword.getLocalValue() == null ? ""
                : uiInputPassword.getLocalValue().toString();

        // get confirm password
        UIInput uiInputConfirmPassword = (UIInput) components.findComponent("passwordConfirmation");
        String confirmPassword = uiInputConfirmPassword.getLocalValue() == null ? ""
                : uiInputConfirmPassword.getLocalValue().toString();
        String passwordConfirmationId = uiInputConfirmPassword.getClientId();

        // Let required="true" do its job.
        if (passwordVal.isEmpty() || confirmPassword.isEmpty()) {
            return;
        }

        if (!passwordVal.equals(confirmPassword)) {

            String errorMessage = FacesContext.getCurrentInstance().getApplication().
                    getResourceBundle(FacesContext.getCurrentInstance(),"global").
                    getString("validation.error.password.confirmation");
            
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            
            fc.addMessage(passwordConfirmationId, message);
            fc.renderResponse();

        }

    }
    
    public String execute() {

        try {
            registerUseCase.registerNewAppUser(email, password, userName, birthday);
            
            //enviar email para confirmação
            registerUseCase.enviarEmailConfirmacaoNovoUsuario(email, localeBean.getLocale());
            
        } catch (EmailAlreadyTakenException ex) {

            String errorMessage = getMsgText("global","validation.error.email.already.taken").replace("{0}", email);
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, message);
            
            //mensagem com um link que não será "escapado" no html
            String infoMessage = getMsgText("global","validation.error.account.recovery.link");
            
            FacesMessage faceLinkMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,infoMessage, infoMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, faceLinkMessage);
            
            return "register";
            
        }
        
        String successMessage = getMsgText("global","action.message.account.created");

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,successMessage, successMessage);

        FacesContext.getCurrentInstance().addMessage(null, message);
        
        return "register";

    }
}