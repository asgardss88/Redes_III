/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cef
 */
public class Servidor extends UnicastRemoteObject implements Interfaz_Cliente_Servidor{
    
    private ConcurrentHashMap<String,maquinaCliente> clientes;
    private final int puerto=1212;
    public Servidor() throws RemoteException {
        super();
        
        clientes = new ConcurrentHashMap<String, maquinaCliente>();
        
    }
    
    

    @Override
    public void registrar() throws RemoteException {
        try {
            
            String ip = getClientHost();
            maquinaCliente maquina_cliente = new maquinaCliente(ip, puerto);
            
            clientes.put(ip, maquina_cliente);
            
             
        } catch (ServerNotActiveException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
}
