/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ManagedBeans;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author Jonas
 */
@Named(value = "cartMB")
@Dependent
public class CartMB {

    /**
     * Creates a new instance of CartMB
     */
    public CartMB() {
    }
    
}
