package redesiii;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.IXMLReader;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;
import org.xml.sax.XMLReader;

/**
 *
 * @author cef
 */
public class LectorConfig {
    
    private IXMLElement xml;
    
    public LectorConfig(String path) throws ClassNotFoundException{
        try {
            IXMLParser parser= XMLParserFactory.createDefaultXMLParser();
           IXMLReader reader= StdXMLReader.fileReader(path);
           parser.setReader(reader);
           xml = (IXMLElement) parser.parse();
           
           
        } catch (XMLException | InstantiationException | IllegalAccessException | IOException ex) {
            Logger.getLogger(LectorConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            
            IXMLParser parser= XMLParserFactory.createDefaultXMLParser();
            IXMLReader reader= StdXMLReader.fileReader(System.getProperty("user.dir")+"/src/redesiii/NewFile.xml");
            parser.setReader(reader);
            IXMLElement xml = (IXMLElement) parser.parse();
            Enumeration e = xml.enumerateChildren();
            
            while(e.hasMoreElements()){
                System.out.println(((IXMLElement)e.nextElement()).getName());
            }
//            Vector<IXMLElement> v = xml.getChildrenNamed("expresion");
         //  IXMLElement c=(IXMLElement) v.get(0);
            
                
//                System.out.println(c.getName());
 //              System.out.println(c.getContent() );
            Vector v, c;
            
             e = xml.enumerateChildren();
           while(e.hasMoreElements()){
                v = ((IXMLElement)e.nextElement()).getChildren();
                int n=0;
               while(n<v.size()){
                   System.out.println(((IXMLElement) v.get(n)).getName());
                   c = ((IXMLElement)v.get(n)).getChildren();
                   
                   int i=0;
                   while(i<c.size()){
                       System.out.println("\t"+((IXMLElement) c.get(i)).getName());
                       i++;
                   }
                   n++;
               
               }
            }
            
        
        } catch (XMLException | ClassNotFoundException | InstantiationException | IllegalAccessException | IOException ex) {
            Logger.getLogger(LectorConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
