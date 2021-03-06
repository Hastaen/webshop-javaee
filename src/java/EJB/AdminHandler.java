/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import Entity.*;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceContext;
/**
 * Class that provides administrator functions.
 * @author Henrik
 */

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class AdminHandler {
@PersistenceContext(unitName = "Project-WebshopPU")
    private EntityManager em;
    
    /**
     * Default constructor.
     */
    public AdminHandler() {}
    
    /**
     * Sets user as banned.
     * @param username Users username.
     * @return True if ban was successful, otherwise false.
     */
    public boolean banUser(String username) {
        try {
            Query banQuery = em.createNamedQuery("Users.findByUsername", Users.class);
            banQuery.setParameter("username", username);
            Users result = (Users)banQuery.getSingleResult();
            if (result.getUsername().equals(username) && !result.getIsbanned()) {
            result.setIsbanned(Boolean.TRUE);
            em.merge(result);
            return true;
            }
            else {
                return false;
            }
        }
        catch (IllegalArgumentException e){
                System.out.println(e.getStackTrace());
                return false;
        }
        
    }
    
    /**
     * Sets user as not banned.
     * @param username Users username.
     * @return true if ban was successful, otherwise false.
     */
    public boolean unbanUser(String username) {
        Query unbanQuery = em.createNamedQuery("Users.findByUsername", Users.class);
        unbanQuery.setParameter("username", username);
        Users result = (Users)unbanQuery.getSingleResult();
        if (result.getUsername().equals(username) && result.getIsbanned()) {
            result.setIsbanned(Boolean.FALSE);
            em.merge(result);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds type of item to inventory.
     * @param name Name of new item.
     * @return True if operation was successful, otherwise false.
     */
    public boolean addItem(String name, int itemstock, int itemprice, String itemdesc) {
        try {
            Items newItem = new Items(name, itemstock, itemprice, itemdesc);
            em.persist(newItem);
            return true;
        }
        catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    /**
     * Removes type of item from inventory.
     * @param name Name of item to remove.
     * @return True if operation was successful, otherwise false.
     */
    public boolean removeItem(String name) {
        try {
            Query removeItemQuery = em.createNamedQuery("Items.findByItemname", Items.class);
            removeItemQuery.setParameter("itemname", name);
            Items result = (Items)removeItemQuery.getSingleResult();
            em.remove(result);
            return true;
        }
        catch (IllegalArgumentException | TransactionRequiredException e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    /**
     * Increases number of units in stock.
     * @param item Item stock to increase.
     * @param amount Number of units to add.
     * @return True if operation was successful, otherwise false.
     */
    public boolean addUnits(String item, int amount) {
        try {
            System.out.println("INSIDE ADMIN");
            Query findItemQuery = em.createNamedQuery("Items.findByItemname", Items.class);
            findItemQuery.setParameter("itemname", item);
            Items result = (Items)findItemQuery.getSingleResult();
            System.out.println("INSIDE ADMIN, stock chnage");
            result.setItemstock(result.getItemstock() + amount);
            em.merge(result);
            return true;
        }
        catch (IllegalArgumentException | TransactionRequiredException e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }
    /**
     * Sets user as not admin.
     * @param username
     * @return true if successful, else false.
     */
    public boolean removeAdmin(String username) {
        try {
            Query setAdminQuery = em.createNamedQuery("Users.findByUsername", Users.class);
            setAdminQuery.setParameter("username", username);
            Users result = (Users)setAdminQuery.getSingleResult();
            if (result.getUsername().equals(username) && result.getIsadmin()) {
            result.setIsadmin(Boolean.FALSE);
            em.merge(result);
            return true;
            }
            else {
                return false;
            }
        }
        catch (IllegalArgumentException e){
                System.out.println(e.getStackTrace());
                return false;
        }
    }
    
    
    /**
     * Sets user as admin.
     * @param username
     * @return true if successful, else false.
     */
    public boolean setAdmin(String username) {
        try {
            Query setAdminQuery = em.createNamedQuery("Users.findByUsername", Users.class);
            setAdminQuery.setParameter("username", username);
            Users result = (Users)setAdminQuery.getSingleResult();
            if (result.getUsername().equals(username) && !result.getIsadmin()) {
                result.setIsadmin(Boolean.TRUE);
                em.merge(result);
                return true;
            }
            else {
                return false;
            }
        }
        catch (IllegalArgumentException e){
                System.out.println(e.getStackTrace());
                return false;
        }
    }
}
