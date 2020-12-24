package br.com.luisfga.domain.config;

import br.com.luisfga.domain.entities.AppRole;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

@WebListener
public class DatabaseSetup implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(DatabaseSetup.class.getName());
    
    @Resource
    private UserTransaction tx;

    @PersistenceUnit(unitName = "applicationJpaUnit")
    EntityManagerFactory emf;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        checkRequiredData();
    }

    private void checkRequiredData() {
        logger.info("Checando dados necessários");

        try {

            tx.begin();
            
            EntityManager em = emf.createEntityManager();

            Query findBasicRole = em.createNamedQuery("AppRole.findStandardRoles");
            List<AppRole> roles = findBasicRole.getResultList(); //apenas faz a query pra ver se vai dar NoResultException

            if(!roles.isEmpty()){
                logger.info("Dados OK!");
            } else {
                logger.info("Dados não encontrados");
                
                //Role USER
                AppRole normalUserRole = new AppRole();
                normalUserRole.setRoleName("USER");
                em.persist(normalUserRole);
                logger.log(Level.INFO, "Salvou: {0}", normalUserRole);
                
                //Role ADMIN
                AppRole adminRole = new AppRole();
                adminRole.setRoleName("ADMIN");
                em.persist(adminRole);
                logger.log(Level.INFO, "Salvou: {0}", adminRole);
                logger.info("Dados OK!");
            }

            tx.commit();

        } catch (IllegalStateException | SecurityException | NotSupportedException 
                | SystemException | RollbackException | HeuristicMixedException 
                | HeuristicRollbackException  ex) {
            logger.log(Level.INFO, ex.getMessage());
            
        }
    }
}
