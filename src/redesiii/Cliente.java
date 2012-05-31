/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author cef
 */
public class Cliente implements Interfaz_Servidor_Cliente {

    private Interfaz_Cliente_Servidor servidor;
    private int puerto;

    public int getPuerto() {
        return puerto;
    }
    

    public Interfaz_Cliente_Servidor getServidor() {
        return servidor;
    }
    
    public Cliente(String ip, int puerto) {
        try {
            this.puerto = puerto;
            InetAddress direccion = InetAddress.getByName(ip);
            
            String direc = "rmi://" + ip + ":" + puerto + "/Cliente";
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

    @Override
    public String verificar() {
        return ejecutar("ps -e")[0];
    }

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

            System.exit(0);

          
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return salida;
    }

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
