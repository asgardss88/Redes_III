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
public interface Interfaz_Cliente_Maquina extends Remote {
    
    public String verificar() throws RemoteException;
    
    public String[] ejecutar(String script) throws RemoteException;
    
}
