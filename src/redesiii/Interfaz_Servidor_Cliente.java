package redesiii;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author cef
 */
public interface Interfaz_Servidor_Cliente extends Remote {
    /**
     * Este metodo permite proporcionar al servidor la lista de los
     * procesos que se encuentran activos en la una instacia de Cliente.
     * 
     * @return Un arreglo de Strings de tamanho 2, donde la posicion
     *          0 representa la salida estandar con los procesos listados 
     *          y la posicion 1 representa error estandar.
     * 
     * @throws Produce RemoteException si ocurre un error en la conexion.
     */
    public String[] verificar() throws RemoteException;
    
    /**
     * Este metodo permite ejecutar un script de bash en una instacia
     * de Cliente.
     * 
     * @param script La representacion en String del Script a ejecutar.
     * 
     * @return Un arreglo de Strings de tamanho 2, donde la posicion
     *          0 representa la salida estandar y la posicion 1 representa
     *          error estandar.
     * @throws Produce RemoteException si ocurre un error en la conexion.
     * 
     */
    public String[] ejecutar(String script) throws RemoteException;
    
    /**
     * Este metodo permite proporcionar al servidor la lista de los
     * procesos criticos que se encuentran inactivos en la maquina
     * cliente.
     * 
     * @return Una lista con los nombres de los procesos inactivos.
     */
    public LinkedList<String> verificarProcesos() throws RemoteException;
    
    public Set<String> procesos_vigilados()throws RemoteException;
    
    public boolean verificarConexion() throws RemoteException;
    
    public void terminar_conexion() throws RemoteException;
    
}
