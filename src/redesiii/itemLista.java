/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package redesiii;

import java.util.Calendar;


/**
 *
 * @author cef
 */
public class itemLista {
    
    public String ip;
    public String log;
    public String estado;
    public String fecha;
    
    public itemLista(String ip) {
        this.ip = ip;
        log = "";
        estado = "OK";
        fecha = obtenerFecha();
    
    }
    
    public void agregarLog(String msj){
        log+="\n"+msj;
        
    }
    
    public void cambiarEstado(String e){
        estado=e;
        fecha = obtenerFecha();
    
    }
    
    @Override
    public String toString(){
        return ip+"                 "+estado+"                             "+fecha;
    }
    
    public String obtenerFecha(){
    
        Calendar c = Calendar.getInstance();
        
        String ano = Integer.toString( c.get(Calendar.YEAR));
        String mes = Integer.toString(c.get(Calendar.MONTH)+1);
        String dia = Integer.toString(c.get(Calendar.DATE));
        String hora = Integer.toString( c.get(Calendar.HOUR));
        String minuto = Integer.toString(c.get(Calendar.MINUTE));
        String segundo = Integer.toString(c.get(Calendar.SECOND));
        
        return dia+"/"+mes+"/"+ano+" "+hora+":"+minuto+":"+segundo;
    }
    
    
}
