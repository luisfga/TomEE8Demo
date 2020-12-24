package br.com.luisfga.jsf;

import br.com.luisfga.service.StatusService;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.metamodel.EntityType;


/**
 *
 * @author luisfga
 */
@Named
@RequestScoped
public class StatusBean {
    
    @EJB
    private StatusService statusService;
    
    private Set<EntityType<?>> entities;
    private Set<String> managedBeans;

    public Set<EntityType<?>> getEntities() {
        return entities;
    }

    public void setEntities(Set<EntityType<?>> entities) {
        this.entities = entities;
    }

    public Set<String> getManagedBeans() {
        return managedBeans;
    }

    public void setManagedBeans(Set<String> managedBeans) {
        this.managedBeans = managedBeans;
    }
    
    @PostConstruct
    public void onLoad(){
        //jpa entities
        entities = statusService.getEntities();

    }
    
}
