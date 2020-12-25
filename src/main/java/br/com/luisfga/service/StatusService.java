package br.com.luisfga.service;

import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

@Stateless
public class StatusService {
    
    @PersistenceContext(unitName = "applicationJpaUnit")
    public EntityManager em;
    
    public Set<EntityType<?>> getEntities(){
        
        return em.getMetamodel().getEntities();
    }
}
