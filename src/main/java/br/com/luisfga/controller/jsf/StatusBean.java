package br.com.luisfga.controller.jsf;

import br.com.luisfga.service.StatusService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.metamodel.EntityType;


/**
 *
 * @author luisfga
 */
@Named
@RequestScoped
public class StatusBean {
    
    @Inject
    private BeanManager beanManager;
    
    @EJB
    private StatusService statusService;
    
    private Set<EntityType<?>> entities;
    private List<Bean<?>> managedBeans;

    public Set<EntityType<?>> getEntities() {
        return entities;
    }

    public void setEntities(Set<EntityType<?>> entities) {
        this.entities = entities;
    }

    public List<Bean<?>> getManagedBeans() {
        return managedBeans;
    }

    public void setManagedBeans(List<Bean<?>> managedBeans) {
        this.managedBeans = managedBeans;
    }
    
    @PostConstruct
    public void onLoad(){
        
        //CDI managed beans
        managedBeans = beanManager
                .getBeans(Object.class)
                .stream()
                .filter(bean -> bean.getBeanClass().isAnnotationPresent(Named.class))
                .sorted(
                        new Comparator<Bean<?>>(){
                            @Override
                            public int compare(Bean<?> b, Bean<?> b1) {
                                return b.getBeanClass().getPackageName().compareTo(b1.getBeanClass().getPackageName());
                            }
                        }
                )
                .collect(Collectors.toList());
        
        //JPA entities
        entities = statusService.getEntities();
        
    }
}
