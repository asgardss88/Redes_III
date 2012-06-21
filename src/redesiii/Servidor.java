package redesiii;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.n3.nanoxml.*;

public class Servidor extends UnicastRemoteObject implements Interfaz_Cliente_Servidor {

    private ConcurrentHashMap<String, maquinaCliente> clientes;
    private ConcurrentHashMap<String, LinkedList<Caso>> config;
    private int puerto = 1212;
    public boolean active;
    public static int salidaStd = 0;
    public static int errorStd = 1;
    public String mail;
    public String password;
    public int retardo;
    public static String path_config;

    public Servidor() throws RemoteException {
        super();

        clientes = new ConcurrentHashMap<String, maquinaCliente>();
        config = new ConcurrentHashMap<String, LinkedList<Caso>>();
        active = true;
        mail = "redesiii2012@gmail.com";
        password = "claveclave";
        retardo = 5;
        procesarConfiguracion();
        
    }

    /**
     * Permite leer el archivo de configuracion 'config.xml' que permite
     * definir soluciones y descripcion de flujos.
     * 
     */
    private void procesarConfiguracion() {
        try {


            IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader = StdXMLReader.fileReader(Servidor.path_config);
            parser.setReader(reader);
            IXMLElement xml = (IXMLElement) parser.parse();
            Enumeration e = xml.enumerateChildren();

            Vector v, c;
            IXMLElement proc, caso;


            while (e.hasMoreElements()) {
                proc = (IXMLElement) e.nextElement();
                v = proc.getChildren();
                int n = 0;
                LinkedList<Caso> list = new LinkedList<Caso>();
                while (n < v.size()) {
                    caso = (IXMLElement) v.get(n);

                    Caso ca = new Caso(((IXMLElement) caso.getChildrenNamed("solucion").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("normal").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("error").get(0)).getContent());
                    list.add(ca);
                    n++;

                }
                config.put(proc.getName(), list);
            }





        } catch (XMLException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    /**
     * Permite la ejecucion de los metodos que definen las funcionalidades del
     * servido. Ademas inicia el procesamiento y la deduccion de las acciones a
     * tomar en cuanto a los diferentes tipos de solicitudes de servicio, a
     * partir de instrucciones insertadas por consola una vez iniciado el
     * programa principal del servidor.
     *
     */
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("> ");
            String input = in.readLine();
            input = input.trim().toLowerCase();
            InetAddress dir;
            String arg;

            if (!input.isEmpty()) {
                char opcion = input.charAt(0);
                input = input.substring(1).trim();




                switch (opcion) {

                    case 'p':

                        arg = input.split("\\s+")[0];

                        dir = InetAddress.getByName(arg);

                        if (clientes.containsKey(dir.getHostAddress())) {
                            this.listarProcesosCliente(dir.getHostAddress());

                        } else {

                            System.out.println("Direccion de Host no registrada");

                        }


                        break;

                    case 'e': //cerrar conexion y salir
                        System.out.println("Conexion finalizada exitosamente");
                        active = false;

                        break;

                    case 't':

                        this.verificarTodas();

                        break;

                    case 'a':

                        arg = input.split("\\s+")[0];

                        dir = InetAddress.getByName(arg);

                        if (clientes.containsKey(dir.getHostAddress())) {
                            this.verificarActiva(dir.getHostAddress());

                        } else {

                            System.out.println("Direccion de Host no registrada");

                        }

                        break;


                    case 'h': // imprimir ayuda
                        System.out.println("Opciones: \nS|s\ne|E\n"
                                + "d|D <link>[,<link>,...]\nh|H\n");
                        break;

                    case 's':
                        activarModoServidor();
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

    public void activarModoServidor() {
        boolean continuar = true;
        boolean sinreparo = true;
        LinkedList<String> ps;
        LinkedList<Caso> casos;
        String[] salida;
        String errores;

        while (continuar) {
            try {
                Thread.sleep(retardo * 1000);

                for (maquinaCliente mc : clientes.values()) {

                    ps = mc.verificarProcesos();

                    for (String p : ps) {

                        casos = config.get(p);
                        errores="";

                        for (Caso c : casos) {

                            salida = mc.ejecutar(c.solucion);

                            if (salida[errorStd].compareTo("") == 0) {

                                if (salida[salidaStd].matches(c.flujonormal)) {

                                    sinreparo = false;
                                    break;
                                }


                            }else{
                            
                                errores+=salida[errorStd]+"\n";
                            }


                        }
                        
                        if(sinreparo){
                            String msj = "Encontrado problema al intentar levantar "+p+" y se encontraron los siguientes errores:\n"+errores;
                            Correo correo =  new Correo(msj,"Error al levantar servicio",mail,password);
                            correo.enviar();
                        }


                    }


                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }



        }


    }

    /**
     * 
     * 
     */
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

    private void listarProcesosCliente(String ip) {
        try {
            maquinaCliente c = clientes.get(ip);

            String[] s = c.listarProcesos();

            System.out.println("Salida Estandar de " + ip + " \n" + s[salidaStd]);
            System.out.println("\nError Estandar de " + ip + " \n" + s[errorStd] + "\n");

        } catch (RemoteException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private void verificarActiva(String ip) {

        maquinaCliente c = clientes.get(ip);

        if (c.verificarConexion()) {
            System.out.println("Conexion con " + ip + " activa");
        } else {

            System.out.println("Se perdio conexion con " + ip);
        }
    }

    private void verificarTodas() {

        for (ConcurrentHashMap.Entry<String, maquinaCliente> e : clientes.entrySet()) {
            if (e.getValue().verificarConexion()) {
                System.out.println("Conexion con " + e.getKey() + " activa");
            } else {

                System.out.println("Se perdio conexion con " + e.getKey());
            }

        }

    }

    /**
     * Metodo principal de ejecucion del cliente.
     *
     * @param Un arreglo con los String que ingresaron por la entrada
     *        estandar.
     */
    public static void main(String[] args) {
        try {

	path_config = args[0];
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
