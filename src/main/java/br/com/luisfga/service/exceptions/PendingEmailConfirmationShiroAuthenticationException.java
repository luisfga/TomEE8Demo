/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.luisfga.service.exceptions;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.realm.jdbc.JdbcRealm;

/**
 * Extensão de {@link AuthenticationException} por conta da assinatura do método
 * {@link JdbcRealm#doGetAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) }
 * 
 * Esta excessão é lançada por implementações de Realm
 * @author Luis
 */
public class PendingEmailConfirmationShiroAuthenticationException extends AuthenticationException{
    
}
