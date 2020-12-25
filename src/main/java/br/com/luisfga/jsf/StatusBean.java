package br.com.luisfga.jsf;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author luisfga
 */
@Named
@RequestScoped
public class StatusBean {
    
    @Inject
    private BeanManager beanManager;
    
    private List<Bean<?>> managedBeans;

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
        
    }
}
