/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ManagedBeans;

import Classes.CartItem;
import EJB.AdminHandler;
import EJB.ItemHandler;
import EJB.UserHandler;
import Entity.*;
import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
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
    private List<Users> theusers;

    
   
    private ArrayList<CartItem> cart =  new ArrayList<CartItem>();
    private int cartsum = 0;

   
    private String message = null;
    private String errormessage = null;
    private String cartmessage = null;
    private int units;

    
    private String newitemname = null;
    private String newitemstock = null;
    private String newitemprice = null;
    private String newitemdesc = null;
    
    @EJB
    private UserHandler USEHAND;
    
    @EJB
    private ItemHandler ITEMHANDLER;
    
    @EJB
    private AdminHandler ADMIN;
    
    @Inject
    private Conversation conversation;
    
    
     public WebshopMB() {
      
        
    }
    
    
    
     
    
    
    public void registeraccount(){
        if(USEHAND.registerUser(username, userpassword, lastname, firstname,mail)){
            this.message = "User is now registered, try to login!";
        }else{
            this.message = "User is already in database, try different username!";  
        }
    }
    
    public String checkout(){
        cartmessage = null;
  
        return "success";
    }
    
    public void buyCart(){
        String tmpName;
        int tmpAmount;
        
        if(cart.isEmpty()){
            cartmessage = "You do not have anything in your cart!";
            
        }else{
            for (int i = 0; i < cart.size(); i++) {
                tmpName = cart.get(i).getName();
                tmpAmount = cart.get(i).getAmount();
                if (ITEMHANDLER.removeUnits(tmpName, tmpAmount)) {
                    cartmessage = "Your purchase have been registered. Have a good day : )";
                }else{
                       cartmessage = "Not enough stock to complete the purchase, pleas try again!. Have a good day : )";
                }

            }
            
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
    
    
    public void deleteItem(){
        FacesContext fc = FacesContext.getCurrentInstance();
        String itemname = getItemNameParam(fc);
        ADMIN.removeItem(itemname);
    }
    
    
    public void ban(){
        String username = getParamUsername();
        ADMIN.banUser(username);
          
    }
    
    public void unBan(){
        String username = getParamUsername();
        ADMIN.unbanUser(username);
        
    }
    
    public void giveAdmin(){
        String username = getParamUsername();
       ADMIN.setAdmin(username);
    }
    
    public void removeAdmin(){
        
        String username = getParamUsername();
       ADMIN.removeAdmin(username);
    }
    
    public void addItem(){
        ADMIN.addItem(newitemname, Integer.parseInt(newitemstock), Integer.parseInt(newitemprice), newitemdesc);
        
        
    }
    
    
    
    
    public void addItemQuantity(){
        
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String action = params.get("itemname");
          String uiindex = params.get("uiindex");
          String un = params.get("uirepeat:"+uiindex+":inputform:inputtext");
        
          if (ADMIN.addUnits(action, Integer.parseInt(un))) {
            //worked
            }else{
              //did not work
          }
        
    }
    
    /**
     * Creates new user.
     * @param Will take param from form, that has the item name.
     * @return The item will be added to .
     */
    
    public void addToCart(){
        String iname;
        int iprice;
        CartItem ci = new CartItem();
        FacesContext fc = FacesContext.getCurrentInstance();
		iname = getItemNameParam(fc);
                iprice = getItemPriceParam(fc);
      
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
    
    private String getItemNameParam(FacesContext fc ){
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String action = params.get("itemname");
        System.out.println("Button param itemname!! " + action);
        
        return action;
    }
    
    private int getItemPriceParam(FacesContext fc ){
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  int action = Integer.parseInt(params.get("itemprice"));
        System.out.println("Button param itemname!! " + action);
        
        return action;
    }

    private String getParamUsername(){
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String username = params.get("username");
          System.out.println("getParamUsername"+username);
          return username;
        
    }
    
    
    /**
     * Login for the user.
     * @param No param, but will use var that are set with JSF binding.
     * @return If the password and username is ok, and the user is not banned the function will return success. Else false.
     */
    
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
            }else{
                    //User has wrong password or is banned.
                    errormessage = "The Password/Username is wrong or account have been BANNED";
                    return "error";
                }
            
            
        }else{
            return "fail";
        }
      
           
    }
    
    /**
     * Logut for the user .
     * @param No parameter but will global var that is set in login function
     * @return Void function, but it will set booleans to false and clear the shopping cart.
     */
    
    public void logout(){
        if (isloggedin == true) {
            isloggedin = false;
            isadmin = false;
            clearcart();
        }else{
            
            
        }
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
    
    
   
    
    public List<Items> getTheproducts() {
        this.theproducts =ITEMHANDLER.getAllItems();
        return theproducts;
    }

    public void setTheproducts(List<Items> theproducts) {
        this.theproducts = theproducts;
    }

    
   
    
    public List<Users> getTheusers() {
        this.theusers = USEHAND.getAllUsers();
        return theusers;
    }

    public void setTheusers(List<Users> theusers) {
        this.theusers = theusers;
    }
    
    public List<Items> getItems(){
        
        return ITEMHANDLER.getAllItems();
        
    }
    
    public List<Users> getUsers(){
        return USEHAND.getAllUsers();
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
    
    public String getCartmessage() {
        return cartmessage;
    }

    public void setCartmessage(String cartmessage) {
        this.cartmessage = cartmessage;
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
    
    public ArrayList<CartItem> getCart() {
        return cart;
    }

    public void setCart(ArrayList<CartItem> cart) {
        this.cart = cart;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getNewitemname() {
        return newitemname;
    }

    public void setNewitemname(String newitemname) {
        this.newitemname = newitemname;
    }

    public String getNewitemstock() {
        return newitemstock;
    }

    public void setNewitemstock(String newitemstock) {
        this.newitemstock = newitemstock;
    }

    public String getNewitemprice() {
        return newitemprice;
    }

    public void setNewitemprice(String newitemprice) {
        this.newitemprice = newitemprice;
    }

    public String getNewitemdesc() {
        return newitemdesc;
    }

    public void setNewitemdesc(String newitemdesc) {
        this.newitemdesc = newitemdesc;
    }
    
    
    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }
    
}
