/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package redesiii;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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
    public String ip;
    
    public maquinaCliente(String ip, int puerto){
        try {
            this.ip = ip;
            cliente = (Interfaz_Servidor_Cliente) Naming.lookup("rmi://" + ip + ":" + puerto + "/Maquina");
            
        } catch (NotBoundException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    public String[] verificarProcesos() throws RemoteException{
    
        return cliente.verificar();
    }
    
    public boolean verificarConexion(){
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException ex) {
           return false;
        }
        
    }
    
}
