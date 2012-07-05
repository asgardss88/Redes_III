/*Servidor web
 * codigo original http://fragments.turtlemeat.com/javawebserver.php*/
package redesiii;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class servidor_web extends Thread {

    public servidor_web(int listen_port, servidor_web_starter to_send_message_to, Servidor mi_servidorn) {
        message_to = to_send_message_to;
        port = listen_port;
        mi_servidor = mi_servidorn;
        this.start();
    }

    private void s(String s2) { //an alias to avoid typing so much!
        message_to.send_message_to_window(s2);
    }
    private servidor_web_starter message_to; //the starter class, needed for gui
    private int port; //port we are going to listen to
    public Servidor mi_servidor;

//this is a overridden method from the Thread class we extended from
    public void run() {
        //we are now inside our own thread separated from the gui.
        ServerSocket serversocket = null;
        //To easily pick up lots of girls, change this to your name!!!
        s("Servidor iniciado\n");
        //Pay attention, this is where things starts to cook!
        try {
            //print/send message to the guiwindow
            s("Tratando de bindearse en localhost en el puerto " + Integer.toString(port) + "...");
            //make a ServerSocket and bind it to given port,
            serversocket = new ServerSocket(port);
        } catch (Exception e) { //catch any errors and print errors to gui
            s("\nError Fatal:" + e.getMessage());
            return;
        }
        s("OK!\n");
        //go in a infinite loop, wait for connections, process request, send response
        while (true) {
            s("\nServidor listo, esperando por solicitudes...\n");
            try {
                //this call waits/blocks until someone connects to the port we
                //are listening to
                Socket connectionsocket = serversocket.accept();
                //figure out what ipaddress the client commes from, just for show!
                InetAddress client = connectionsocket.getInetAddress();
                //and print it to gui
                s(client.getHostName() + " conectado al servidor.\n");
                //Read the http request from the client from the socket interface
                //into a buffer.
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(connectionsocket.getInputStream()));
                //Prepare a outputstream from us to the client,
                //this will be used sending back our response
                //(header + requested file) to the client.
                DataOutputStream output =
                        new DataOutputStream(connectionsocket.getOutputStream());

//as the name suggest this method handles the http request, see further down.
//abstraction rules
                http_handler(input, output);
            } catch (Exception e) { //catch any errors, and print them
                s("\nerror 2:" + e.getMessage());
            }

        } //go back in loop, wait for next request
    }

//our implementation of the hypertext transfer protocol
//its very basic and stripped down
    private void http_handler(BufferedReader input, DataOutputStream output) {
        int method = 0; //1 get, 2 head, 0 not supported
        String http = new String(); //a bunch of strings to hold
        String path = new String(); //the various things, what http v, what path,
        String file = new String(); //what file
        String user_agent = new String(); //what user_agent
        try {
            //This is the two types of request we can handle
            //GET /index.html HTTP/1.0
            //HEAD /index.html HTTP/1.0
            String tmp = input.readLine(); //read from the stream
            String tmp2 = new String(tmp);
            tmp.toUpperCase(); //convert it to uppercase
            if (tmp.startsWith("GET")) { //compare it is it GET
                method = 1;
            } //if we set it to method 1
            if (tmp.startsWith("HEAD")) { //same here is it HEAD
                method = 2;
            } //set method to 2

            if (method == 0) { // not supported
                try {
                    output.writeBytes(construct_http_header(501, 0));
                    output.close();
                    return;
                } catch (Exception e3) { //if some error happened catch it
                    s("error 1:" + e3.getMessage());
                } //and display error
            }
            //}

            //tmp contains "GET /index.html HTTP/1.0 ......."
            //find first space
            //find next space
            //copy whats between minus slash, then you get "index.html"
            //it's a bit of dirty code, but bear with me...
            int start = 0;
            int end = 0;
            for (int a = 0; a < tmp2.length(); a++) {
                if (tmp2.charAt(a) == ' ' && start != 0) {
                    end = a;
                    break;
                }
                if (tmp2.charAt(a) == ' ' && start == 0) {
                    start = a;
                }
            }
            path = tmp2.substring(start + 2, end); //fill in the path
        } catch (Exception e) {
            s("error 3" + e.getMessage());
        } //catch any exception

        //path do now have the filename to what to the file it wants to open
        s("\nCliente solicita :" + path + "\n");

        //happy day scenario
        try {

            /*
             * cabecera
             */
            output.writeBytes(construct_http_header(200, 5));

            //if it was a HEAD request, we don't print any BODY
            if (method == 1) { //1 is GET 2 is head and skips the body

                /*
                 * se maneja path, que es lo que solicita el usuario
                 */
                String salida = "<html>";
                salida += "<head>";
                salida += "<script language=\"JavaScript\">function setVisibility(id, visibility) {document.getElementById(id).style.display = visibility;};";
                salida += "</script>";
                salida += "</head>";
                salida += "<body>";
                salida += "<h1>Sistema de monitoreo</h1>";
                if (path.isEmpty()) {
                    salida += "<h2>Maquinas conectadas:</h2>";
                    if (mi_servidor.clientes.isEmpty()) {
                        salida += "No hay clientes conectados";
                    } else {
                        salida += "<table><tr><th>Direccion</th><th>Acciones</th></tr>";
                        Iterator itr = mi_servidor.clientes.keySet().iterator();
                        while (itr.hasNext()) {
                            String ip = (String) itr.next();
                            maquinaCliente mc = mi_servidor.clientes.get(ip);
                            salida += "<tr><td>" + ip + "</td><td><a href='verificar/" + ip + "'>verificar procesos</a></td></tr>";
                        }

                        salida += "</table>";
                    }


                } else if (path.startsWith("verificar/")) {
                    String ip_c = path.substring(path.lastIndexOf("/") + 1);
                    salida += "<h2>Informacion detallada: " + ip_c + "</h2>";
                    maquinaCliente mc = mi_servidor.clientes.get(ip_c);
                    
                    LinkedList<String> procesos_caidos = mc.procesos_caidos;
                    salida += "<h3>Procesos vigilados en el cliente</h3>";
                    salida += "<table><tr><th>Nombre</th><th>Estado</th></tr>";
                    for (String s : mc.cliente.procesos_vigilados()) {
                        salida += "<tr><td>" + s + "</td><td>";
                        if (procesos_caidos.contains(s))
                        {
                            salida += "Caido";
                        }
                        else
                        {
                            salida += "Ok";
                        }
                        
                        salida += "</td></tr>";
                    }
                    salida += "</table>";


                    salida += "<hr><h3>Procesos corriendo en el cliente</h3>";
                    salida += "<input type=button name=type value='Mostrar' onclick=\"setVisibility('sub3', 'inline');\";><input type=button name=type value='Ocultar' onclick=\"setVisibility('sub3', 'none');\";> ";
                    salida += "<br><div id=\"sub3\">";
                    String[] tmp = mc.listarProcesos();
                    tmp[0] = tmp[0].replaceAll("\\n", "<br>");
                    salida += tmp[0];
                    salida += "</div>";


                } else {
                    salida += "Error: el recurso solicitado NO existe";
                }

                if (!path.isEmpty()) {
                    salida += "<br><br><A HREF=\"javascript:javascript:history.go(-1)\">Regresar</A>";
                }

                salida += "</body></html>";
                output.writeBytes(salida);
            }
            output.close();

        } catch (Exception e) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, e);
        }
    }
//this method makes the HTTP header for the response
//the headers job is to tell the browser the result of the request
//among if it was successful or not.

    private String construct_http_header(int return_code, int file_type) {
        String s = "HTTP/1.0 ";
        //you probably have seen these if you have been surfing the web a while
        switch (return_code) {
            case 200:
                s = s + "200 OK";
                break;
            case 400:
                s = s + "400 Bad Request";
                break;
            case 403:
                s = s + "403 Forbidden";
                break;
            case 404:
                s = s + "404 Not Found";
                break;
            case 500:
                s = s + "500 Internal Server Error";
                break;
            case 501:
                s = s + "501 Not Implemented";
                break;
        }

        s = s + "\r\n"; //other header fields,
        s = s + "Conexion cerrada\r\n"; //we can't handle persistent connections
        s = s + "Server: Interfaz web\r\n"; //server name

        switch (file_type) {
            //plenty of types for you to fill in
            case 0:
                break;
            case 1:
                s = s + "Content-Type: image/jpeg\r\n";
                break;
            case 2:
                s = s + "Content-Type: image/gif\r\n";
                break;
            case 3:
                s = s + "Content-Type: application/x-zip-compressed\r\n";
                break;
            case 4:
                s = s + "Content-Type: image/x-icon\r\n";
                break;
            default:
                s = s + "Content-Type: text/html\r\n";
                break;
        }

        ////so on and so on......
        s = s + "\r\n"; //this marks the end of the httpheader
        return s;
    }
}
