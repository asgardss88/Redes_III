package redesiii;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author cef
 */
public interface Interfaz_Cliente_Servidor extends Remote  {
    
    public LinkedList<String> registrar() throws RemoteException;
    
    public boolean chequear_conexion () throws RemoteException;
    
}
