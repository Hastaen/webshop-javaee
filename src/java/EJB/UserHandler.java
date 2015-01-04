/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJB;

import javax.ejb.Stateless;
import javax.persistence.*;
import Entity.*;
import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 *
 * @author Henrik
 */

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class UserHandler {
@PersistenceContext(unitName = "Project-WebshopPU")
   
    private EntityManager em;
    private String userName;
    private String firstName;
    private String lastName;
    private boolean isAdmin = false;
    private boolean loggedIn = false;
    
    /**
     * Default constructor.
     */
    public void saveUN(String n){
        this.firstName = n;
        System.out.println(n);
    }
    
    /**
     * Log in user.
     * @param username
     * @param password
     * @return 
     */
    
    public boolean isUserAdmin(String username){
        
        Query isadminQuery = em.createNamedQuery("Users.findByUsername", Users.class);
        isadminQuery.setParameter("username", username);
        try {
            Users result = (Users)isadminQuery.getSingleResult();
            if (result.getIsadmin() == true) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
        
        }
        return false;
    }
    
    
    public boolean logIn(String username, String password) {
        
        System.out.println("Before EM init " + username +" " +password);
        Query logInQuery = em.createNamedQuery("Users.findByUsername", Users.class);
        logInQuery.setParameter("username", username);
        System.out.println("Users getsingleres " + username +" " +password);
        try {
            Users result = (Users)logInQuery.getSingleResult();
             System.out.println("Before rescheck " + username +" " +password);
        if (result.getPassword().equals(password)) {
            System.out.println("User could log in with pass (inside UH) " + username +" " +password);
            this.userName = username;
            this.firstName = result.getFirstname();
            this.lastName = result.getLastname();
            if (result.getIsadmin()) {
                this.isAdmin = true;
            }
            loggedIn = true;
            return true;
        }
        else {
            System.out.println("User could NOT log in with pass (inside UH) " + username +" " +password);
            return false;
        }
        } catch (NoResultException e) {
        System.out.println("NORESULT EXCEPTION !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return false;
        }
        
       
    }

    
    private Users findByUsername(String username) {
        Query logInQuery = em.createNamedQuery("Users.findByUsername", Users.class);
        logInQuery.setParameter("username", username);
         Users result =null;
        
            System.out.println("Registeruser, before getsingleresult!!!!!!!!!!!!!!!!!!!");
        try {
            result = (Users)logInQuery.getSingleResult();
        
        } catch (NoResultException e) {
            System.out.println("Exception in finduserby username!!!!!!!!!!!!!!!!!!!");
            return result;
        }
        
        return result;
    }
    
    /**
     * Creates new user.
     * @param username
     * @param password
     * @param isAdmin
     * @return false if user exists or connection failed, else true.
     */
    public boolean registerUser(String username, String password, String lastname, String firstname , String mail) {
        Users result = findByUsername(username);
        
        if (result == null) {
        
            try {
                Users newUser = new Users(username, password, lastname, firstname, mail, false, false);
                em.persist(newUser);
                return true;
            }
            catch (EntityExistsException e){
                System.out.println(e.getStackTrace());
                return false;
            }
          
        }else{
            return false;
        }
        
        
    }
    
    /**
     * Sets user as admin.
     * @param username
     * @return true if successful, else false.
     */
    public boolean setAdmin(String username) {
        Users result = findByUsername(username);
        
        if (result.getUsername().equals(username)) {
            try {
                if (!result.getIsadmin()) {
                    result.setIsadmin(true);
                }
                isAdmin = true;
                return true;
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getStackTrace());
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    /**
     * Sets user as not admin.
     * @param username
     * @return true if successful, else false.
     */
    public boolean removeAdmin(String username) {
        Users result = findByUsername(username);
        
        if (result.getUsername().equals(username)) {
            try {
                if (result.getIsadmin()) {
                    result.setIsadmin(false);
                }
                isAdmin = false;
                return true;
            }
            catch (IllegalArgumentException e){
                System.out.println(e.getStackTrace());
                return false;
            }
        }
        else {
            return false;
        }
    }
    
     /**
     * Gets all the users in DB.
     * @return A List of all the users or empty List if no users exist.
     */
    
    public List<Users> getAllUsers(){
    
     Query getAllQuery = em.createNamedQuery("Users.findAll", Users.class);
       // logInQuery.setParameter("username", username);
    
    return (List<Users>) getAllQuery.getResultList();
    
    
    
}
    
    
    
    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Returns true if user is admin.
     * @return 
     */
    public boolean isIsAdmin() {
        return isAdmin;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
    
}