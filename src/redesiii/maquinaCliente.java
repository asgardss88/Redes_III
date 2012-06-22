package redesiii;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cef
 */
public class maquinaCliente {
    
    public Interfaz_Servidor_Cliente cliente;
    public String ip;
    public LinkedList<String> procesos_caidos;
    
    public maquinaCliente(String ip, int puerto){
        try {
            this.ip = ip;
            cliente = (Interfaz_Servidor_Cliente) Naming.lookup("rmi://" + ip + ":" + puerto + "/Maquina");
            procesos_caidos = new LinkedList<>();
            
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(maquinaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    public String[] ejecutar(String script) throws RemoteException{
    
        return cliente.ejecutar(script);
    }
    
    public String[] listarProcesos() throws RemoteException{
    
        return cliente.verificar();
    }
    
    public LinkedList<String> verificarProcesos() throws RemoteException{
    
        return cliente.verificarProcesos();
    }
    
    public boolean verificarConexion(){
        
            try {
                cliente.verificarConexion();
                return true;
                
                
            } catch (RemoteException ex) {
                return false;
            }
            
        
        
    }
}    

