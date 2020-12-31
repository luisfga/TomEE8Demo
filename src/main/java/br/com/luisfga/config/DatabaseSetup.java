package br.com.luisfga.config;

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
        logger.info("Looking for required data");

        try {

            tx.begin();
            
            EntityManager em = emf.createEntityManager();

            Query findBasicRole = em.createNamedQuery("AppRole.findStandardRoles");
            List<AppRole> roles = findBasicRole.getResultList();

            if(!roles.isEmpty()){
                logger.info("Required data is OK!");
            } else {
                logger.info("Required data not found. Saving them...");
                
                //Role USER
                AppRole normalUserRole = new AppRole();
                normalUserRole.setRoleName("USER");
                em.persist(normalUserRole);
                logger.log(Level.INFO, "Role saved: {0}", normalUserRole);
                
                //Role ADMIN
                AppRole adminRole = new AppRole();
                adminRole.setRoleName("ADMIN");
                em.persist(adminRole);
                logger.log(Level.INFO, "Role saved: {0}", adminRole);
                logger.info("Required data is OK!");
            }

            tx.commit();

        } catch (IllegalStateException | SecurityException | NotSupportedException 
                | SystemException | RollbackException | HeuristicMixedException 
                | HeuristicRollbackException  ex) {
            logger.log(Level.INFO, ex.getMessage());
            
        }
    }
}
