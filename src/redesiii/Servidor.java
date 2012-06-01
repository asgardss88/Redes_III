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
    private int puerto=1212;
    public boolean active;
    public Servidor() throws RemoteException {
        super();
        
        clientes = new ConcurrentHashMap<String, maquinaCliente>();
        active=true;
    }
    
    
    /**
     * Permite la ejecucion de los metodos que definen las funcionalidades del
     * cliente. Ademas inicia el procesamiento y la deduccion de las acciones a
     * tomar en cuanto a los diferentes tipos de solicitudes de servicio, a
     * partir de instrucciones insertadas por consola una vez iniciado el
     * programa cliente.
     *
     */
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> ");
            String input = in.readLine();
            input = input.trim().toLowerCase();

            if (!input.isEmpty()) {
                char opcion = input.charAt(0);
                input = input.substring(1).trim();
                
                
                

                switch (opcion) {

                    case 'p':
                        
                        String arg = input.split("\\s+")[0];
                        
                        InetAddress dir = InetAddress.getByName(arg);
                        
                        if(clientes.containsKey(dir.getHostAddress())){
                            this.verificarProcesos(dir.getHostAddress());
                        
                        }else{
                        
                            System.out.println("Direccion de Host no registrada");
                        
                        }
   
                        
                        break;

                    case 'e': //cerrar conexion y salir
                        System.out.println("Conexion finalizada exitosamente");
                        active = false;
                        
                        break;


                    case 'h': // imprimir ayuda
                        System.out.println("Opciones: \nS|s\ne|E\n"
                                + "d|D <link>[,<link>,...]\nh|H\n");
                        break;

                    default:
                        System.out.println("Error Opcion invalida");
                        run();



                }

            } else {

                System.out.println("Error Opcion invalida");
                run();

            }
        } catch (IOException ex) {
            System.out.println("Direccion de Host invalida");
            run();
        }



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
    
    private void verificarProcesos(String ip){
        try {
            maquinaCliente c = clientes.get(ip);
            
            String[] s = c.verificarProcesos();
            
            System.out.println("Salida Estandar de "+ip+" \n"+s[0]);
            System.out.println("\nError Estandar de "+ip+" \n"+s[0]+"\n");
            
        } catch (RemoteException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
    
    
    
  public static void main(String[] args){
        try {
            System.setProperty(
                                "java.rmi.server.codebase",
                                "file:" + System.getProperty("user.dir") + "/");
                        Servidor server = new Servidor();        
                        java.rmi.registry.LocateRegistry.createRegistry(server.puerto);
                        String host = InetAddress.getLocalHost().toString().split("/")[1];

                         
                        Naming.rebind("rmi://" + host + ":" + server.puerto + "/Servidor", server);
                        
                       
         while (server.active) {
                         server.run();
                        }
         
         System.exit(0);

                        
        } catch (MalformedURLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
  
  
  }  
}
