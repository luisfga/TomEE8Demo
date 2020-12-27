package br.com.luisfga.service;

import br.com.luisfga.domain.entities.AppRole;
import br.com.luisfga.domain.entities.AppUser;
import br.com.luisfga.service.exceptions.EmailAlreadyTakenException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.shiro.authc.credential.DefaultPasswordService;

@Stateless
public class RegisterUseCase {
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    public EntityManager em;
    
    @EJB private MailHelper mailHelper;
    
    public void registerNewAppUser(String email, String password, String userName, LocalDate birthday) 
            throws EmailAlreadyTakenException {
        
        try {
            //verifica se o email informado está disponível
            Query checkIfExists = em.createNamedQuery("AppUser.checkIfExists");
            checkIfExists.setParameter("email", email);
            checkIfExists.getSingleResult();
            
            //se não lançou NoResultException é porque já existe tal email cadastrado
            throw new EmailAlreadyTakenException();
            
        } catch (NoResultException nrException) {
        }
        
        //configura novo usuário
        AppUser newAppUser = new AppUser();
        newAppUser.setEmail(email);
        newAppUser.setUserName(userName);
        newAppUser.setBirthday(birthday);
        newAppUser.setStatus("new");
        newAppUser.setJoinTime(OffsetDateTime.now());

        DefaultPasswordService defaultPasswordService = new DefaultPasswordService();
        String encryptedPassword = defaultPasswordService.encryptPassword(password);
        newAppUser.setPassword(encryptedPassword);
        
        //configura ROLE
        newAppUser.setRoles(getRolesForNewUser().stream().collect(Collectors.toSet()));
        

        //salva novo usuário
        em.persist(newAppUser);

    }
    
    public void enviarEmailConfirmacaoNovoUsuario(String contextPath, String email) {
        
        mailHelper.enviarEmailConfirmacaoNovoUsuario(contextPath, email);

    }
    
    private List<AppRole> getRolesForNewUser(){
        try {
            Query findRolesForNewUser = em.createNamedQuery("AppRole.findRolesForNewUser");
            //Assumimos que um novo usuário não tem nada e sua lista de Roles está vazia.
            List<AppRole> roles = findRolesForNewUser.getResultList();
            return roles;
            
        } catch (NoResultException nrException) {
            //no op
        }
        return null;
    }
    
}