/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package EJB;

import javax.ejb.Stateless;
import javax.persistence.*;
import Entity.*;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;
/**
 *
 * @author Jonas
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class ItemHandler {
@PersistenceContext(unitName = "Project-WebshopPU")
   
    private EntityManager em;
    



    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

public List<Items> getAllItems(){
    
     Query getAllQuery = em.createNamedQuery("Items.findAll", Items.class);
       // logInQuery.setParameter("username", username);
    
    return (List<Items>) getAllQuery.getResultList();
    
    
    
}

public boolean getItem(String name){
    
    
    return false;
}

 /**
     * Decreases number of units in stock.
     * @param item Item stock to increase.
     * @param amount Number of units to add.
     * @return True if operation was successful, otherwise false.
     */
    public boolean removeUnits(String item, int amount) {
        try {
            System.out.println("INSIDE ADMIN");
            Query findItemQuery = em.createNamedQuery("Items.findByItemname", Items.class);
            findItemQuery.setParameter("itemname", item);
            Items result = (Items)findItemQuery.getSingleResult();
            if ((result.getItemstock()-amount ) < 0) {
             return false;   
            }else{
            System.out.println("INSIDE ADMIN, stock chnage");
            result.setItemstock(result.getItemstock() - amount);
            em.merge(result);
            return true;
            }
        }
        catch (IllegalArgumentException | TransactionRequiredException e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }    
    
    
    
    



}
