/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cef
 */
public class maquinaCliente {
    
    public Interfaz_Servidor_Cliente cliente;
    
    public maquinaCliente(String ip, int puerto){
        try {
            cliente = (Interfaz_Servidor_Cliente) Naming.lookup("rmi://" + ip + ":" + puerto + "/Cliente");
            
        } catch (NotBoundException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}
