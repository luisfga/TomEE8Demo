package br.com.luisfga.domain.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "app_user")
@NamedQueries({
        @NamedQuery(name = "AppUser.findUserNameByEmail", query = "SELECT au.userName FROM AppUser au WHERE au.email=:email"),
        @NamedQuery(name = "AppUser.findByEmail", query = "SELECT au FROM AppUser au WHERE au.email=:email"),
        @NamedQuery(name = "AppUser.checkIfExists", query = "SELECT au.email FROM AppUser au WHERE au.email=:email"),
        @NamedQuery(name = "AppUser.findByEmailAndBirthday", query = "SELECT au.email FROM AppUser au WHERE au.email=:email AND au.birthday=:birthday"),
        @NamedQuery(name = "AppUser.findAll", query = "SELECT au from AppUser au")
        
})
public class AppUser implements Serializable {
    
    @NotBlank(message = "Nome do usuário é obrigatório")
    @Column(name="user_name")
    private String userName;
    
    private byte[] thumbnail;
    
    @Id
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "validation.invalid.email")
    private String email;
    
    @NotNull(message = "Data de Nascimento é obrigatório")
    @Column(columnDefinition = "DATE")
    private LocalDate birthday;
    
    @Column(name="join_time")
    private OffsetDateTime joinTime;
    
    @NotBlank(message = "Senha é obrigatório")
    private String password;
    
    @Column(name="search_token")
    private String searchToken;
    
    @NotEmpty(message = "Status é obrigatório")
    private String status;
    
    @OneToOne(mappedBy = "appUser", orphanRemoval = true)
    private AppUserOperationWindow operationWindow;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role", 
            joinColumns = @JoinColumn(name = "email"), 
            inverseJoinColumns = @JoinColumn(name = "role_name")
    )
    private Set<AppRole> roles;

    public AppUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public OffsetDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(OffsetDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSearchToken() {
        return searchToken;
    }

    public void setSearchToken(String searchToken) {
        this.searchToken = searchToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AppUserOperationWindow getOperationWindow() {
        return operationWindow;
    }

    public void setOperationWindow(AppUserOperationWindow operationWindow) {
        this.operationWindow = operationWindow;
    }

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> roles) {
        this.roles = roles;
    }
    
}