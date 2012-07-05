package redesiii;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta Clase representan cada uno de los usuarios(Clientes) que se encuentran
 * corriendo en los equipos a monitorizar.
 *
 * Esta clase dispone de todas las implementaciones necesarias para la ejecucon
 * de scripts enviados por el servidor.
 *
 * @author Cesar Freitas.
 * @author Edward Zambrano.
 */
public final class Cliente extends UnicastRemoteObject implements Interfaz_Servidor_Cliente {

    public Interfaz_Cliente_Servidor servidor; //La interfaz de comunicacion
    public LinkedList<String> servidores_backup;
    //con el servidor.
    private int puerto; // El puerto donde se desea establecer
    //la conexion.
    public static String process_path;
    public LinkedList<String> procesos;
    public static final Logger logger = Logger.getLogger(Servidor.class.getName());

    /**
     * Constructor de la clase Cliente, esta permite inicializar todos los
     * parametros necesarios para su funcionamiento.
     *
     * @param ip Un String que representa la direccion ip de del servidor al
     * cual esta maquina se va a registrar para ser monitorizada.
     * @param puerto Un entero que representa el numero de puerto donde se va a
     * establecer la conexion.
     */
    public Cliente(String ip, int puerto) throws RemoteException {

        this.puerto = puerto;
        this.servidores_backup = new LinkedList<>();
        conectar_con_servidor(ip);

        procesos = new LinkedList<>();

        this.leerProcesos("process");



    }

    public boolean conectar_con_servidor(String s) {
        try {
            String direc = "rmi://" + s + ":" + puerto + "/Servidor";
            servidor = (Interfaz_Cliente_Servidor) Naming.lookup(direc);
            Cliente.logger.log(Level.INFO, "Conectado con el servidor {0}", s);
            return true;

        } catch (NotBoundException | MalformedURLException ex) {
            Cliente.logger.log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Cliente.logger.log(Level.SEVERE, "Hubo un error al conectar con el servidor");
        }
        return false;
    }

    @Override
    /**
     * Este metodo permite garantizar la conexion con el cliente
     *
     * @return Un booleano para garantizar la conexion.
     *
     */
    public boolean verificarConexion() {
        return true;
    }

    @Override
    public void terminar_conexion() {
        Cliente.logger.info("Conexion terminada desde el servidor. Adios");
        System.exit(0);
    }

    /**
     * Este metodo permite obtener el numero de puerto de donde se establece la
     * conexion.
     *
     * @return Un entero que representa el numero de puerto.
     *
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * Este metodo permite obtener una interfaz para interactuar con el servidor
     * remoto.
     *
     * @return Un objeto de interfaz de conexion cliente-servidor.
     *
     */
    public Interfaz_Cliente_Servidor getServidor() {
        return servidor;
    }

    /**
     * Este metodo permite proporcionar al servidor la lista de los procesos que
     * se encuentran activos en la una instacia de Cliente.
     *
     * @return Un arreglo de Strings de tamanho 2, donde la posicion 0
     * representa la salida estandar con los procesos listados y la posicion 1
     * representa error estandar.
     */
    @Override
    public String[] verificar() {
        return ejecutar("ps -eo fname");
    }

    /**
     * Este metodo permite ejecutar un script de bash en una instacia de
     * Cliente.
     *
     * @param script La representacion en String del Script a ejecutar.
     *
     * @return Un arreglo de Strings de tamanho 2, donde la posicion 0
     * representa la salida estandar y la posicion 1 representa error estandar.
     */
    @Override
    public String[] ejecutar(String script) {

        String s;
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
            Cliente.logger.log(Level.INFO, "Servidor solicita ejecutar el comando __{0}__", script);
            while ((s = stdInput.readLine()) != null) {
                salida[0] += s + "\n";
            }
            // Leemos los errores si los hubiera
            Cliente.logger.log(Level.INFO, "Salida standar del comando __{0}__", script);
            System.out.println(salida[0]);

            Cliente.logger.log(Level.INFO, "Salida ERROR del comando __{0}__", script);
            while ((s = stdError.readLine()) != null) {
                salida[1] += s + "\n";
            }
            System.out.println("__" + salida[1] + "__");


        } catch (IOException ex) {

            Servidor.logger.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return salida;
    }

    public void leerProcesos(String arch) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(process_path));

            String linea;
            try {
                while ((linea = buffer.readLine()) != null) {
                    if (!linea.matches("\\s*")) { //se ignora lineas en blanco

                        procesos.add(linea);


                    }
                }
            } catch (IOException ex) {
                Cliente.logger.log(Level.SEVERE, "Error de lectura sobre el archivo {0}", arch);
                System.exit(-1);
            }
        } catch (FileNotFoundException ex) {
            Cliente.logger.log(Level.SEVERE, "Error al intentar abrir el archivo{0}", arch);
            System.exit(-1);
        }
    }

    /**
     * Este metodo permite proporcionar al servidor la lista de los procesos
     * criticos que se encuentran inactivos en la maquina cliente.
     *
     * @return Una lista con los nombres de los procesos inactivos.
     */
    @Override
    public LinkedList<String> verificarProcesos() {

        String[] salidas = this.ejecutar("ps -eo fname");

        String[] separado = salidas[0].split("\n");
        int n;

        Cliente.logger.info("El servidor solicita la verificacion de procesos");

        LinkedList<String> falta = new LinkedList();

        for (String p : procesos) {
            n = 1;

            while (n < separado.length) {

                if (separado[n].compareTo(p) == 0) {
                    break;

                }
                n++;
            }

            if (n >= separado.length) {

                falta.addLast(p);
            }
        }

        for (String x : falta) {

            System.out.println("no encontrado " + x);

        }

        return falta;

    }
    
    @Override
    public LinkedList<String> procesos_vigilados () {
        return procesos;
    }

    /**
     * Metodo principal de ejecucion del cliente.
     *     
* @param Un arreglo con los String que ingresaron por la entrada estandar.
     */
    public static void main(String[] args) {
        try {
            process_path = args[2];
            System.setProperty(
                    "java.rmi.server.codebase",
                    "file:" + System.getProperty("user.dir") + "/");
            Cliente maquina = new Cliente(args[0], Integer.parseInt(args[1]));

            try {
                java.rmi.registry.LocateRegistry.createRegistry(maquina.getPuerto());
            } catch (java.rmi.server.ExportException ex) {
                Cliente.logger.info("El puerto ya esta ocupado, igual se inicia");
            }

            String host = InetAddress.getLocalHost().toString().split("/")[1];
            Naming.rebind("rmi://" + host + ":" + maquina.getPuerto() + "/Maquina", maquina);

            maquina.servidores_backup = maquina.servidor.registrar();

            boolean continuar = true;

            while (continuar) {
                Thread.sleep(1000);
                try {
                    maquina.servidor.chequear_conexion();
                } catch (RemoteException ex) {
                    if (!maquina.servidores_backup.isEmpty()) {
                        Cliente.logger.warning("Error en la conexion con el servidor principal, se procede a conectar con el servidor de backup");
                        Thread.sleep(5000);
                        Cliente.logger.warning("CONTINUO");
                        boolean reconexion = maquina.conectar_con_servidor(maquina.servidores_backup.pop());
                        if (reconexion) {
                            Naming.rebind("rmi://" + host + ":" + maquina.getPuerto() + "/Maquina", maquina);
                            maquina.servidores_backup = maquina.servidor.registrar();
                            Cliente.logger.info("Reconexion exitosa");
                        } else {
                            Cliente.logger.info("Reconexion fracasada, se intentara conectar con el siguiente servidor");
                        }
                    } else {
                        Cliente.logger.warning("No hay servidores de backup, se termina la ejecucion");
                        System.exit(-1);
                    }
                }

            }



        } catch (InterruptedException ex) {
            Cliente.logger.severe("error con el sleep");
        } catch (MalformedURLException | RemoteException ex) {
            Cliente.logger.severe("error conectando con el servidor");
        } catch (UnknownHostException ex) {
            Cliente.logger.severe("Algo paso con el servidor");
        }




    }
}
