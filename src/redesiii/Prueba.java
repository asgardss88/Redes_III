package redesiii;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
//para la lectura de la configuracion
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;

/**
 *
 * @author cef
 */
public class Prueba {
    
    public LinkedList<String> procesos;
     private ConcurrentHashMap<String, LinkedList<Caso>> config= new ConcurrentHashMap<String, LinkedList<Caso>>();

    public Prueba() {
        procesos = new LinkedList<String>();
    
    }
    
    public void procesarConfiguracion(){
        try {
            
            
            
            
            IXMLParser parser= XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader= StdXMLReader.fileReader(System.getProperty("user.dir")+"/src/redesiii/config.xml");
            parser.setReader(reader);
            IXMLElement xml = (IXMLElement) parser.parse();
            Enumeration e = xml.enumerateChildren();
            
             Vector v, c;
             IXMLElement proc, caso;
            
             e = xml.enumerateChildren();
           while(e.hasMoreElements()){
                proc = (IXMLElement)e.nextElement();
                v = proc.getChildren();
                int n=0;
                LinkedList<Caso> list = new LinkedList<Caso>();
               while(n<v.size()){
                   System.out.println(((IXMLElement) v.get(n)).getName());
                   caso = (IXMLElement)v.get(n);
                   
                   Caso ca = new Caso(((IXMLElement) caso.getChildrenNamed("solucion").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("normal").get(0)).getContent(), ((IXMLElement) caso.getChildrenNamed("error").get(0)).getContent());
                   list.add(ca); 
                   System.out.println(ca.solucion+" "+ca.flujonormal+" "+ca.flujoerror);
                 
                   
                   n++;
               
               }
               config.put(proc.getName(), list);
            }
           
           Enumeration<String> aux = config.keys();
           LinkedList<Caso> caux;
           String nombre;
           while(aux.hasMoreElements()){
               nombre = aux.nextElement();
               caux = config.get(nombre);
               System.out.println(nombre);
               
               for(Caso i:caux){
                  System.out.println("\t"+i.solucion+" "+i.flujonormal+" "+i.flujoerror); 
               
               }
           
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
     * Este metodo permite ejecutar un script de bash en una instacia
     * de Cliente.
     * 
     * @param script La representacion en String del Script a ejecutar.
     * 
     * @return Un arreglo de Strings de tamanho 2, donde la posicion
     *          0 representa la salida estandar y la posicion 1 representa
     *          error estandar.
     */
    
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
            System.out.println("Esta es la salida standard del comando:\n");
            while ((s = stdInput.readLine()) != null) {
                salida[0] += s + "\n";
            }
            System.out.println(salida[0]);
            // Leemos los errores si los hubiera
            System.out.println("Esta es la salida standard de error del comando (si la hay):\n");
            while ((s = stdError.readLine()) != null) {

                salida[1] += s + "\n";
            }

            System.out.println(salida[1]);

            

          
        } catch (IOException ex) {
            
            
            System.exit(0);
        }
        return salida;
    }
    
    
    public LinkedList<String> verificarProcesos(){
    
        String[] salidas = this.ejecutar("ps -eo fname");
        
        String[] separado = salidas[0].split("\n");
        
        int n=0;
        
        while(n<separado.length){
            System.out.println("|"+separado[n]+"|");
            n++;
        
        }
        
        
        LinkedList<String> falta = new LinkedList();
        
        for(String p:procesos){
            n=1;
            
            while(n<separado.length){
                
                if(separado[n].compareTo(p)==0){
                    System.out.println(p+" encontrado");
                    break;
                    
                }
                n++;
            }
            
            if(n>=separado.length){
                System.out.println(p+" no encontrado");
                falta.addLast(p);
            }
        }
        
        for(String x:falta){
        
            System.out.println("no encontrado "+x);
        
        }
        
        
        return falta;
    
    }
    
    
    
    public void leerProcesos(String arch) {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(arch));

            String linea;
            try {
                while ((linea = buffer.readLine()) != null) {
                    if (!linea.matches("\\s*")) { //se ignora lineas en blanco

                        procesos.add(linea);
                      

                    }
                }
            } catch (IOException ex) {
                System.out.println("Error de lectura sobre el archivo " + arch);
                System.exit(-1);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Error al intentar abrir el archivo" + arch);
            System.exit(-1);
        }
    }

    public static void main(String[] args){
        try {
            Prueba p =  new Prueba();
            
            p.procesos.add("pppppppp");
            p.procesos.add("plugin-c");
            p.procesos.add("evince");
            p.procesos.add("java");
            p.procesos.add("update-n");
            p.procesos.add("aaaaaaa");
            
          //  while(true){
                Thread.sleep(0);
                p.procesarConfiguracion();
            //    p.verificarProcesos();
                
           // }
        } catch (InterruptedException ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
 
    
}
