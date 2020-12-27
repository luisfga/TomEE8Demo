package br.com.luisfga.business;

import br.com.luisfga.business.exceptions.LoginException;
import br.com.luisfga.business.exceptions.PendingEmailConfirmationException;
import br.com.luisfga.business.exceptions.PendingEmailConfirmationShiroAuthenticationException;
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
    public EntityManager em;
    
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
    
    public void logout() {

        SecurityUtils.getSubject().logout();
        
    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String contextPath, String email) {

        mailHelper.enviarEmailConfirmacaoNovoUsuario(contextPath, email);

    }

}
