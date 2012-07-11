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
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.n3.nanoxml.*;

public class Servidor extends UnicastRemoteObject implements Interfaz_Cliente_Servidor {

    public ConcurrentHashMap<String, maquinaCliente> clientes;
    public ConcurrentHashMap<String, LinkedList<Caso>> config;
    private int puerto = 1212;
    public boolean active;
    public static int salidaStd = 0;
    public static int errorStd = 1;
    public String mail;
    public String password;
    public int retardo;
    public static String path_config;
    public static final Logger logger = Logger.getLogger(Servidor.class.getName());
    public LinkedList<String> servidores_backup;
    public InterfazPrincipal interfaz;
    public boolean gui;
    public boolean continuar;

    public Servidor(String correo, String pass) throws RemoteException {
        super();

        servidores_backup = new LinkedList<>();
        clientes = new ConcurrentHashMap<>();
        config = new ConcurrentHashMap<>();
        active = true;
        mail =  correo;                          //"redesiii2012@gmail.com";
        password = pass;                         //"claveclave";
        retardo = 2;
        continuar=true;
        procesarConfiguracion();
        gui = false;
        System.setProperty("java.rmi.server.codebase", "file:" + System.getProperty("user.dir") + "/");
        try {
            java.rmi.registry.LocateRegistry.createRegistry(puerto);
        } catch (java.rmi.server.ExportException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.INFO, null, "El puerto ya esta ocupado, igual se inicia");
        }

        String host;
        try {
            host = InetAddress.getLocalHost().toString().split("/")[1];


            Naming.rebind("rmi://" + host + ":" + puerto + "/Servidor", this);
            servidor_web_starter webserver = new servidor_web_starter(this);

        } catch (MalformedURLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setInterfaz(InterfazPrincipal i) {

        interfaz = i;
        gui = true;

    }

    /**
     * Permite leer el archivo de configuracion 'config.xml' que permite definir
     * soluciones y descripcion de flujos.
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
                LinkedList<Caso> list = new LinkedList<>();
                while (n < v.size()) {
                    caso = (IXMLElement) v.get(n);

                    Caso ca = new Caso(((IXMLElement) caso.getChildrenNamed("solucion").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("normal").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("error").get(0)).getContent());
                    list.add(ca);
                    n++;

                }
                config.put(proc.getName(), list);
            }





        } catch (XMLException | IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Servidor.logger.severe(ex.toString());
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
                    case 'c':
                        arg = input.split("\\s+")[0];
                        maquinaCliente m;
                        dir = InetAddress.getByName(arg);
                        if (clientes.containsKey(dir.getHostAddress())) {
                            m = clientes.get(dir.getHostAddress());
                            if (m.verificarConexion()) {
                                chequear_maquina(m);
                            } else {
                                Servidor.logger.log(Level.INFO, "Se perdio conexion con {0}", dir.getHostAddress());
                                clientes.remove(dir.getHostAddress());
                            }
                        } else {
                            System.out.println("Direccion de Host no registrada");
                        }
                        break;

                    case 'e': //cerrar conexion y salir
                        Servidor.logger.log(Level.INFO, "Conexion finalizada exitosamente");
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
                            Servidor.logger.log(Level.INFO, "Direccion de Host no registrada");
                        }
                        break;
                    case 'h': // imprimir ayuda
                        System.out.println("AYUDA:");
                        System.out.println("c <host>: \tPermite chequear el status del hosts con sus procesos asociados");
                        System.out.println("a <host>: \tPermite verificar la conexion con un host en especifico");
                        System.out.println("p <host>: \tPermite listar todos los procesos que corre un host en especifico");
                        System.out.println("t: \t Permite verificar todas las conexiones con todos los host");
                        System.out.println("s: \t Permite activar el modo de servidor de verificacion automatica");
                        System.out.println("e: \t Cerrar la conexion.");
                        break;
                    case 'l':
                        Enumeration e = clientes.keys();
                        if (e.hasMoreElements()) {
                            while (e.hasMoreElements()) {
                                System.out.println("Host " + ((String) e.nextElement()));
                            }
                        } else {
                            System.out.println("No hay host registrados.");
                        }
                        break;
                    case 'p':
                        arg = input.split("\\s+")[0];
                        dir = InetAddress.getByName(arg);
                        if (clientes.containsKey(dir.getHostAddress())) {
                            listarProcesosCliente(dir.getHostAddress());
                        } else {
                            Servidor.logger.log(Level.INFO, "Direccion de Host no registrada");
                        }
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
        


        while (continuar) {
            try {
                Thread.sleep(retardo * 1000);
                for (maquinaCliente mc : clientes.values()) {

                    chequear_maquina(mc);
                }
            } catch (InterruptedException ex) {
                Servidor.logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     *
     *
     */
    @Override
    public boolean chequear_conexion() throws RemoteException {
        return true;
    }

    /**
     *
     *
     */
    @Override
    public synchronized LinkedList<String> registrar() throws RemoteException {
        try {
            String ip = getClientHost();
            maquinaCliente maquina_cliente = new maquinaCliente(ip, puerto);
            clientes.put(ip, maquina_cliente);

            if (gui) {
                interfaz.lista.add(0, new itemLista(ip));
                interfaz.agregarAConsola("host " + ip + " conectandose...\n" + ip + " conectado");
            }

            return (LinkedList<String>) servidores_backup.clone();
        } catch (ServerNotActiveException ex) {
            Servidor.logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String[] listarProcesosCliente(String ip) {
        try {
            maquinaCliente c = clientes.get(ip);
            String[] s = c.listarProcesos();
            System.out.println("Salida Estandar de " + ip + " \n" + s[salidaStd]);
            return s;
        } catch (RemoteException ex) {
            Servidor.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void verificarActiva(String ip) {

        maquinaCliente c = clientes.get(ip);
        if (c.verificarConexion()) {
            System.out.println("Conexion con " + ip + " activa");
        } else {
            System.out.println("Se perdio conexion con " + ip);
            clientes.remove(ip);
        }
    }

    private void verificarTodas() {

        for (ConcurrentHashMap.Entry<String, maquinaCliente> e : clientes.entrySet()) {
            if (e.getValue().verificarConexion()) {
                System.out.println("Conexion con " + e.getKey() + " activa");
            } else {
                System.out.println("Se perdio conexion con " + e.getKey());
                clientes.remove(e.getKey());
            }
        }
    }

    public void agregar_servidores_backup(String linea) {
        StringTokenizer tokens = new StringTokenizer(linea, ",");
        while (tokens.hasMoreTokens()) {
            String t = tokens.nextToken();
            Servidor.logger.log(Level.INFO, "Agregado servidor de backup {0}", t);
            servidores_backup.add(t);
        }

    }

    public void chequear_maquina(maquinaCliente mc) {
        try {
            boolean sinreparo = true;
            LinkedList<String> ps;
            LinkedList<Caso> casos;
            String[] salida;
            String errores = new String();
            String msj;
                if (gui) {
                     itemLista i = interfaz.buscarIp(mc.ip);
                     i.cambiarEstado(i.estado);
                     interfaz.clientList.updateUI();
                            }
            if (mc.verificarConexion()) {

                ps = mc.verificarProcesos();

                for (String p : ps) {
                    Servidor.logger.log(Level.INFO, "Proceso __{0}__ esta caido", p);
                    
                    if (gui) {

                        itemLista i = interfaz.buscarIp(mc.ip);
                        i.agregarLog(i.obtenerFecha()+" El proceso " + p + "se encuentra caido");
                        interfaz.agregarAConsola(i.obtenerFecha()+" El proceso " + p + "se encuentra caido en "+mc.ip);
                    }
                    
                    sinreparo = true;
                    if (!mc.procesos_caidos.contains(p)) {
                        casos = config.get(p);
                        if (casos == null) {
                            Servidor.logger.log(Level.INFO, "no Hay casos para el proceso __{0}__", p);
                        } else {
                            errores = "";
                            for (Caso c : casos) {
                                salida = mc.ejecutar(c.solucion);
                                /*
                                 * si no hubo salida de error
                                 */
                                if (salida[errorStd].isEmpty()) {
                                    if (salida[salidaStd].contains(c.flujonormal)) {
                                        Servidor.logger.log(Level.INFO, "Proceso __{0}__ arreglado", p);
                                        sinreparo = false;
                                        
                                        if (gui) {

                                            itemLista i = interfaz.buscarIp(mc.ip);
                                            i.agregarLog(i.obtenerFecha()+" Proceso " + p + "leavantado");
                                            interfaz.agregarAConsola(i.obtenerFecha()+" Proceso " + p + " nuevamente levantado en "+mc.ip);
                                        }
                                        
                                        break;
                                    } else {
                                    }
                                } else {
                                    errores += salida[errorStd] + "\n";
                                }
                            }
                        }

                        if (sinreparo) {
                            msj = "Encontrado problema al intentar levantar " + p + " en "+mc.ip+" y se encontraron los siguientes errores:\n" + errores;
                            if (gui) {

                                itemLista i = interfaz.buscarIp(mc.ip);
                                i.agregarLog(i.obtenerFecha()+" "+msj);
                                i.cambiarEstado("PS");
                                interfaz.agregarAConsola(i.obtenerFecha()+" "+msj);
                                interfaz.clientList.updateUI();
                            }
                            Correo correo = new Correo(msj, "Error al levantar servicio", mail, password);
                            correo.enviar();
                            Servidor.logger.log(Level.INFO, "Enviado correo notificando del error al levantar __{0}__", p);
                            mc.procesos_caidos.add(p);
                        } else {
                            Servidor.logger.log(Level.INFO, "Proceso __{0}__ levantado correctamente", p);
                        }
                    } else {
                        Servidor.logger.log(Level.INFO, "El proceso __{0}__ se encuentra caido, ya fue notificado por correo", p);
                    }
                }
            } else {
                if (gui) {

                    itemLista i = interfaz.buscarIp(mc.ip);
                    i.cambiarEstado("FL");
                    i.agregarLog("Fallo en la conexión con" + i.ip);
                    interfaz.clientList.updateUI();
                }
                clientes.remove(mc.ip);

                msj = "No se pudo establecer conexion con " + mc.ip + " revise si el equipo esta encendido o si hay algun problema en la red";
                Correo correo = new Correo(msj, "Problema estableciendo conexion", mail, password);
                correo.enviar();
            }

        } catch (RemoteException ex) {
            Servidor.logger.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metodo principal de ejecucion del cliente.
     *
     * @param Un arreglo con los String que ingresaron por la entrada estandar.
     */
    public static void main(String[] args) {
        try {

            path_config = args[0];

            Servidor server = new Servidor(args[2],args[3]);

            server.agregar_servidores_backup(args[1]);




            
            while (server.active) {
                server.run();
            }
            System.exit(0);


        } catch (RemoteException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
