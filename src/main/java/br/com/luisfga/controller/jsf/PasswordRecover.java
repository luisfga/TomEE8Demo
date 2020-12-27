package br.com.luisfga.controller.jsf;

import br.com.luisfga.service.PasswordRecoverUseCase;
import br.com.luisfga.service.exceptions.WrongInfoException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

@Named
@RequestScoped
public class PasswordRecover extends JsfBeanSupport{

    private final Logger logger = Logger.getLogger(PasswordRecover.class.getName());
    
    @EJB private PasswordRecoverUseCase passwordRecoverUseCase;
    
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    private LocalDate birthday;
    public LocalDate getBirthday() {
        return birthday;
    }
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public String execute() {
        String windowToken = UUID.randomUUID().toString();
        try {
            passwordRecoverUseCase.prepareRecovery(email, birthday, windowToken);
            
            ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            
            passwordRecoverUseCase.enviarEmailResetSenha(ctx.getContextPath(), email, windowToken);
            
        } catch (WrongInfoException wbException) {
            String errorMessage = getMsgText("global","action.error.invalid.informations");
            
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,errorMessage, errorMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, message);

            return "";
            
        }
        
        //enviar email para reset de senha
        String successMessage = getMsgText("global","action.message.reset.password.email.sent");

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,successMessage, successMessage);

        FacesContext.getCurrentInstance().addMessage(null, message);
        
        return "";
    }
    
}