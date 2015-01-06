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


import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Main ManagedBean that handles calls from JSF view.
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
    
    
     /**
     * Creates new user.
     * @param Will take param from form.
     * @return The account will be registered or not. The var message will show output in view.
     */
     
    public void registeraccount(){
        if(USEHAND.registerUser(username, userpassword, lastname, firstname,mail)){
            this.message = "User is now registered, try to login!";
        }else{
            this.message = "User is already in database, try different username!";  
        }
    }
    
    /**
     * Will send the client to the checkout.xhtml page where the items may be bought.
     * @return Will redirect the user to checkout.xhtml through faces-config.xml.
     */
    
    public String checkout(){
        cartmessage = null;
  
        return "success";
    }
    
    /**
     * Will buy the items in the shopping cart.
     * @return A cartmessage will show. Depending if there is enough stock of the items the order will be cleared.
     */
    
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
     
    /**
     * Will clear the shopping cart
     */
    public void clearcart(){
        cart.clear();
        cartsum = 0;
    }
    
    
    /**
     * Will remove item from products. In Admin page
     * @Param Will take item name from form parameter
     * @return the item will be deleted from DB. It will not show in webpage anymore.
     */
    
    public void deleteItem(){
       
        String itemname = getParam("itemname");
        ADMIN.removeItem(itemname);
    }
    
    
    
    /**
     * Will ban user, user will not be able to log in . In Admin page
     * @Param Will take user name from form parameter
     * @return The user will not be able to log in as normal.
     */
    public void ban(){
        String username = getParam("username");
        ADMIN.banUser(username);
          
    }
    
    
    /**
     * Will remove ban from user. In Admin page
     * @Param Will take user name from form parameter
     * @return The user will be able to log in as normal.
     */
    
    public void unBan(){
        String username = getParam("username");
        ADMIN.unbanUser(username);
        
    }
    
    
    /**
     * Will give user admin access. In Admin page
     * @Param Will take user name from form parameter
     * @return The user admin access boolean will show in admin page.
     */
    
    public void giveAdmin(){
        String username = getParam("username");
       ADMIN.setAdmin(username);
    }
    
    /**
     * Will remove users admin access. In Admin page
     * @Param Will take user name from form parameter
     * @return The user admin access boolean will show in admin page.
     */
    
    public void removeAdmin(){
        
        String username = getParam("username");
       ADMIN.removeAdmin(username);
    }
    
    /**
     * Will add a new item to the database. The new item will be seen by everyone and can be bought
     * @return The new item will be rendered in the webshop.
     */
    public void addItem(){
        ADMIN.addItem(newitemname, Integer.parseInt(newitemstock), Integer.parseInt(newitemprice), newitemdesc);
        
        
    }
    
    /**
     * Will add more stock to a specific item. (I admin page).
     * @return will always add the number of units to the stock. No max for units except int.max.
     */
    
    public void addItemQuantity(){
        
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String item = params.get("itemname");
          //UI:repeat row
          String uiindex = params.get("uiindex");
          //This is to find the specific item that is to be given more stock. The param is depending on the DOM-tree of UI:REPEATE row 
          String DOMparam = params.get("uirepeat:"+uiindex+":inputform:inputtext");
        
          ADMIN.addUnits(item, Integer.parseInt(DOMparam));
         
        
    }
    
   /**
    * Will add the specific item to the users cart.
    * @Param The item name will be given through form param
    */
    
    public void addToCart(){
        String iname;
        int iprice;
        CartItem ci = new CartItem();
    
		iname = getParam("itemname");
                iprice = Integer.parseInt(getParam("itemprice"));
      
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
    
    /**
     * Will calculate the total of the items that have been put in the cart.
     * 
     */
    
     private void calccartsum(){
        int amount = 0;
        int price = 0;
        cartsum = 0;
        
        
        for (int i = 0; i < cart.size(); i++) {
            amount = cart.get(i).getAmount();
            price = cart.get(i).getPrice();
            cartsum += amount * price;               
            } 
        
    }
     
     /**
     * Will check if specific item in in shopping cart. Private helper function.
     * @Param name
     * @Return true of false depending on if the item is in the cart 
     */
    
    private boolean isItemInCart(String name){
        for (int i = 0; i < cart.size(); i++) {
            
            if(cart.get(i).getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Will get form parameter specified parameter name. Private helper function.
     * @Param The parameter name 
     * @Return Will return parameter value if the parameter exists 
     */
    private String getParam(String paramName){
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String,String> params = 
                fc.getExternalContext().getRequestParameterMap();
	  String paramValue = params.get(paramName);
     
        
        return paramValue;
    }
    
    
    
    
    /**
     * Login for the user.
     * @param No param, but will use var that are set with JSF binding.
     * @return If the password and username is ok, and the user is not banned the function will return success. Else false.
     */
    
    public String login(){
       
        //   startConversation();
        
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
    
     public int getCartsum() {
        return cartsum;
    }

    public void setCartsum(int cartsum) {
        this.cartsum = cartsum;
    }
    
}
