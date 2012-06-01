
package redesiii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta Clase representan cada uno de los usuarios(Clientes) que se
 * encuentran corriendo en los equipos a monitorizar.
 * 
 * Esta clase dispone de todas las implementaciones necesarias para la
 * ejecucon de scripts enviados por el servidor.
 * 
 * @author Cesar Freitas.
 * @author Edward Zambrano.
 */
public class Cliente implements Interfaz_Servidor_Cliente {

    private Interfaz_Cliente_Servidor servidor; //La interfaz de comunicacion
                                                //con el servidor.
    private int puerto; // El puerto donde se desea establecer
                        //la conexion.
    /**
     * Constructor de la clase Cliente, esta permite inicializar todos 
     * los parametros necesarios para su funcionamiento.
     * 
     * @param ip Un String que representa la direccion ip de del servidor
     *           al cual esta maquina se va a registrar para ser monitorizada.
     * @param puerto Un entero que representa el numero de puerto donde se va
     *        a establecer la conexion.
     */
    public Cliente(String ip, int puerto) {
        try {
            this.puerto = puerto;
            InetAddress direccion = InetAddress.getByName(ip);
            
            String direc = "rmi://" + ip + ":" + puerto + "/Servidor";
            servidor = (Interfaz_Cliente_Servidor) Naming.lookup(direc);
            
        } catch (NotBoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
    }
    
    
    /**
     * Este metodo permite obtener el numero de puerto de donde se establece
     * la conexion.
     * 
     * @return Un entero que representa el numero de puerto.
     *
     */
     public int getPuerto() {
        return puerto;
    }
    
     /**
     * Este metodo permite obtener una interfaz para interactuar con el
     * servidor remoto. 
     * 
     * @return Un objeto de interfaz de conexion cliente-servidor.
     *
     */
    public Interfaz_Cliente_Servidor getServidor() {
        return servidor;
    }
    /**
     * Este metodo permite proporcionar al servidor la lista de los
     * procesos que se encuentran activos en la una instacia de Cliente.
     * 
     * @return Un arreglo de Strings de tamanho 2, donde la posicion
     *          0 representa la salida estandar con los procesos listados 
     *          y la posicion 1 representa error estandar.
     */
    @Override
    public String[] verificar() {
        return ejecutar("ps -e");
    }
    /**
     * Este metodo permite ejecutar un script de bash en una instacia
     * de Cliente.
     * 
     * @param script La representacion en String del Script a ejecutar.
     * 
     * @return Un arreglo de Strings de tamanho 2, donde la posicion
     *          0 representa la salida estandar y la posicion 1 representa
     *          error estandar.
     */
    @Override
    public String[] ejecutar(String script) {

        String s = null;
        String[] salida = new String[2];
        salida[0] = "";
        salida[1] = "";
        
        try {

            // Ejcutamos el comando
            Process p = Runtime.getRuntime().exec(script);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));

            // Leemos la salida del comando
            System.out.println("Ésta es la salida standard del comando:\n");
            while ((s = stdInput.readLine()) != null) {
                salida[0] += s + "\n";
            }
            System.out.println(salida[0]);
            // Leemos los errores si los hubiera
            System.out.println("Ésta es la salida standard de error del comando (si la hay):\n");
            while ((s = stdError.readLine()) != null) {

                salida[1] += s + "\n";
            }

            System.out.println(salida[1]);

            

          
        } catch (IOException ex) {
            
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return salida;
    }

/**
* Metodo principal de ejecucion del cliente.
*
* @param Un arreglo con los String que ingresaron por la entrada
*        estandar.
*/
public static void main(String[] args){
        try {
            
            System.setProperty(
                           "java.rmi.server.codebase",
                           "file:" + System.getProperty("user.dir") + "/");
                   Cliente maquina = new Cliente(args[0],Integer.parseInt(args[1]));        
                   java.rmi.registry.LocateRegistry.createRegistry(maquina.getPuerto());
                   String host = InetAddress.getLocalHost().toString().split("/")[1];

                    
                   Naming.rebind("rmi://" + host + ":" + maquina.getPuerto() + "/Maquina", maquina);
                   
        } catch (MalformedURLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
   
    
}


}
