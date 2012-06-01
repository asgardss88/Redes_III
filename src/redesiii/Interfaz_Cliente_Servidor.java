/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author cef
 */
public interface Interfaz_Cliente_Servidor extends Remote  {
    
    public void registrar() throws RemoteException;
    
}
