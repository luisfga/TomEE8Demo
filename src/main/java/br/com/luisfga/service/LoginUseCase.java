package br.com.luisfga.service;

import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.exceptions.LoginException;
import br.com.luisfga.service.exceptions.PendingEmailConfirmationException;
import br.com.luisfga.service.exceptions.PendingEmailConfirmationShiroAuthenticationException;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

@Stateless //TODO atenção para os testes. Ver se pode ser Stateless mesmo ou se tem que ser Statefull
public class LoginUseCase {
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    private EntityManager em;
    
    @EJB private MailHelper mailHelper;
    
    public void login(String email, String password) throws LoginException, PendingEmailConfirmationException {

        UsernamePasswordToken authToken = new UsernamePasswordToken(email, password);
        authToken.setRememberMe(false);
        
        Subject currentUser = SecurityUtils.getSubject();
        
        try {
            currentUser.login(authToken);
            
        } catch ( UnknownAccountException | IncorrectCredentialsException | LockedAccountException | ExcessiveAttemptsException ice ) {
            throw new LoginException();
            
        } catch (PendingEmailConfirmationShiroAuthenticationException ex){
            throw new PendingEmailConfirmationException();
        }

    }
    
    //pegar os dados do usuário que existe mas ainda não foi habilitado
    public AppUser getUserFailedWithPendencies(String email){
        return em.find(AppUser.class, email);
    }
    
    public void logout() {

        SecurityUtils.getSubject().logout();
        
    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String destEmail, Locale locale) {

        mailHelper.enviarEmailConfirmacaoNovoUsuario(destEmail, locale);

    }

}
