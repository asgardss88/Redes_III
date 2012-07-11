/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

/**
 *
 * @author cef
 */
public class Verificador extends Thread {
    public Servidor server;
    public Verificador(Servidor serv) {
    
        server = serv;
    }

    @Override
    public void run() {
        
        server.activarModoServidor();
    }
    
    
    
    
    
}
