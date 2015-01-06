/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Classes;

/**
 *
 * @author Jonas
 */
public class CartItem {

   
    
   private String name;
   private int price;
   private int amount;
   private int total;
   
   
   /**
     * Default constructor.
     */
   public CartItem(){
       
   }
   
   /**
     * Constructor.
     */
   public CartItem(String name, int price, int amount, int total) {
        this.name = name;
        this.price = price;
        this.amount = amount;
   }
   
   
   /**
     * Constructor.
     */
   
   public CartItem(String name, int price) {
        this.name = name;
        this.price = price;
       
   }

   public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
   

}
