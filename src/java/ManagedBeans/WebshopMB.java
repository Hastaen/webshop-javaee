/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ManagedBeans;

import EJB.UserHandler;
import EJB.ItemHandler;
import EJB.AdminHandler;
import Classes.CartItem;
import Entity.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import java.util.*;
import javax.faces.context.FacesContext;
/**
 *
 * @author Jonas
 */
@Named("webshopMB")
@SessionScoped
//@ConversationScoped
public class WebshopMB implements Serializable {

    /**
     * Creates a new instance of WebshopMB
     */
    private static final long serialVersionUID = 1L;
    private String username = null;
    private String userpassword = null;
    private boolean isloggedin = false;
    private boolean isadmin = false;
    private String firstname = null;
    private String lastname = null;
    private String mail = null;
    
    
    private List<Items> theproducts;
   // private ArrayList<String> cart = new ArrayList<String>();
    private ArrayList<CartItem> cart =  new ArrayList<CartItem>();
    private int cartsum = 0;

   
    private String message = null;
    private String cartmessage = null;

   

    
    
    @EJB
    private UserHandler USEHAND;
    
    @EJB
    private ItemHandler ITEMHANDLER;
    
    @EJB
    private AdminHandler ADMIN;
    
    @Inject
    private Conversation conversation;
    
    
     public String getCartmessage() {
        return cartmessage;
    }

    public void setCartmessage(String cartmessage) {
        this.cartmessage = cartmessage;
    }
    
    
    public void registeraccount(){
        if(USEHAND.registerUser(username, userpassword, lastname, firstname,mail)){
            
            System.out.println("User is now registered, try to login!");
            this.message = "User is now registered, try to login!";
        }else{
            System.out.println("User is already in database, try different username!");
            this.message = "User is already in database, try different username!";
            
        }
        
        
    }
    
    public String checkout(){
        cartmessage = null;
  
        return "success";
    }
    
    public void buyCart(){
        
        if(cart.isEmpty()){
            cartmessage = "You do not have anything in your cart!";
            
        }else{
            cartmessage = "Your purchase have been registered. Have a good day : )!";
        }
        clearcart();
    }
   
    public void calccartsum(){
        int amount = 0;
        int price = 0;
        cartsum = 0;
        
        
        for (int i = 0; i < cart.size(); i++) {
            amount = cart.get(i).getAmount();
            price = cart.get(i).getPrice();
            cartsum += amount * price;               
            } 
        
    }
    
    public int getCartsum() {
        return cartsum;
    }

    public void setCartsum(int cartsum) {
        this.cartsum = cartsum;
    }
    
    
    public void clearcart(){
        cart.clear();
        cartsum = 0;
    }
    
    public void addToCart(){
        String iname;
        int iprice;
        CartItem ci = new CartItem();
        FacesContext fc = FacesContext.getCurrentInstance();
		iname = getItemNameParam(fc);
                iprice = getItemPriceParam(fc);
      //  cart.add(iname);
        ci.setName(iname);
        ci.setPrice(iprice);
        if(isItemInCart(iname) ) {
            for (int i = 0; i < cart.size(); i++) {
                if(cart.get(i).getName().equalsIgnoreCase(iname)){
                    int temp;
                    temp = cart.get(i).getAmount();
                    cart.get(i).setAmount(temp + 1);
                }
            } 
        }else{
           ci.setAmount(1);
           cart.add(ci);
        }
        calccartsum();
        
    }
    
    private boolean isItemInCart(String name){
        for (int i = 0; i < cart.size(); i++) {
            
            if(cart.get(i).getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
    
    public String getItemNameParam(FacesContext fc ){
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String action = params.get("itemname");
        System.out.println("Button param itemname!! " + action);
        
        return action;
    }
    
    public int getItemPriceParam(FacesContext fc ){
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  int action = Integer.parseInt(params.get("itemprice"));
        System.out.println("Button param itemname!! " + action);
        
        return action;
    }

    public ArrayList<CartItem> getCart() {
        return cart;
    }

    public void setCart(ArrayList<CartItem> cart) {
        this.cart = cart;
    }
    
    
    public List<Items> getTheproducts() {
        this.theproducts =ITEMHANDLER.getAllItems();
        return theproducts;
    }

    public void setTheproducts(List<Items> theproducts) {
        this.theproducts = theproducts;
    }
    
     private void startConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    private void stopConversation() {
        if (!conversation.isTransient()) {
            conversation.end();
        }
    }
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public boolean isIsloggedin() {
        return isloggedin;
    }

    public void setIsloggedin(boolean isloggedin) {
        this.isloggedin = isloggedin;
    }

    public boolean isIsadmin() {
        return isadmin;
    }

    public void setIsadmin(boolean isadmin) {
        this.isadmin = isadmin;
    }
    
   

    
    public WebshopMB() {
       // this.theproducts = new List<Items>;
        
    }
    
    public List<Items> getItems(){
        
       // theproducts = ITEMHANDLER.getAllItems()
        return ITEMHANDLER.getAllItems();
        
        
    }
    
    public String login(){
       
           startConversation();
        
        System.out.println("Login 1" + username +" " +userpassword);
        
            System.out.println("Login 2" + username +" " +userpassword);
        if((username != null) && (userpassword != null)){
            System.out.println("Before testbool call " + username +" " +userpassword);
           boolean testbool = USEHAND.logIn(username, userpassword);
                if(testbool){
                    if(USEHAND.isIsAdmin()) {
                        isadmin = true;
                    }else{
                        isadmin = false;
                    }

                isloggedin = true;
                return "success";
            }
            
            
        }else{
            return "fail";
        }
      
           return "fail";
    }
    
    public void logout(){
        if (isloggedin == true) {
            isloggedin = false;
            isadmin = false;
            clearcart();
        }else{
            
            
        }
    }
    
    
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
