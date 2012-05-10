/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cef
 */
public class Maquina implements Interfaz_Cliente_Maquina {

    public Maquina() {
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
            Logger.getLogger(Maquina.class.getName()).log(Level.SEVERE, null, ex);
        }
        return salida;
    }
}
