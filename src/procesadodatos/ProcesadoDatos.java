/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package procesadodatos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
/**
 *
 * @author Hector
 */
public class ProcesadoDatos {

    /*
    java -jar ProcesadoDatos.jar "C:\Users\Hector\Documents\NetBeansProjects\ProcesadoDatos\src\procesadodatos\csv\PUNTOREF_5_5_0_0_500_20180503_115213.csv"
    */
    public static final String SEPARATOR=",";
    public static final char QUOTE='"';
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ArrayList<String> macs = devolverMacs();
        if(args.length > 0){
            System.out.println("Voy a crear la base de datos: ...");
            String nombreArchivo = "PROCESADO.xlsx";
            //Crear libro de trabajo con la estructura basica
            XSSFWorkbook libroTrabajo = new XSSFWorkbook();
            XSSFSheet[] hojas = new XSSFSheet[24];
            int k=7;
            int aux=0;
            for (int i=0; i<24;i++){
                if(i%6==0){
                    k=7;
                    aux++;
                }
                if(i%2==0){
                    hojas[i]=libroTrabajo.createSheet("Canal3"+k+"eBeacon-"+aux);
                }else{
                    hojas[i]=libroTrabajo.createSheet("Canal3"+k+"Edystone-"+aux);
                    k++;
                }
            }
            XSSFRow[] rows =new XSSFRow[hojas.length];
            for (int i=0;i<hojas.length;i++){
                rows[i]=hojas[i].createRow(0);
            }
            boolean par = true;
            int cont = 1;
            for (int c = 0; c < 24; c++) {
                XSSFCell[] cells = new XSSFCell[rows.length];
                for (int i=0;i<cells.length;i++){
                    cells[i]=rows[i].createCell(c);
                }
                if (par) {
                    for (int i=0;i<cells.length;i++){
                        cells[i].setCellValue("Tiempo");
                    }
                    par = false;
                } else {
                    for(int i=0;i<cells.length;i++){
                        cells[i].setCellValue("Beacon " + cont);
                    }
                    cont++;
                    par = true;
                }   
            }
            //Leer csv
            String csvFile = args[0];
            BufferedReader br = null;
            String line = "";
            //Se define separador ","
            String cvsSplitBy = ",";
            try {
                br = new BufferedReader(new FileReader(csvFile));
                while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);
                //Imprime datos.
                for(String dato: datos){
                    System.out.print(dato+", ");
                }
                System.out.println("");
            }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //escribir este libro en un OutputStream.
                FileOutputStream fileOut = new FileOutputStream(nombreArchivo);
                libroTrabajo.write(fileOut);
                fileOut.flush();
                fileOut.close();
                
                //cerramos el csv
                if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }   
        System.out.println("¡Terminado el procesado de datos!.");
        }else{
            System.out.println("Para poder hacer la operacion necesita ficheros.");
        }
        
        //crearBaseDatos(macs,ruta);
    }
    private static String[] removeTrailingQuotes(String[] fields) {

      String result[] = new String[fields.length];

      for (int i=0;i<result.length;i++){
         result[i] = fields[i].replaceAll("^"+QUOTE, "").replaceAll(QUOTE+"$", "");
      }
      return result;
    }
    public static ArrayList<String> devolverMacs() throws IOException {
        ArrayList<String> macs = new ArrayList<String>();
        macs.add("D8:07:9F:BB:65:8E".toLowerCase());
        macs.add("C8:A5:CD:C0:66:8F".toLowerCase());
        macs.add("E7:0D:93:F0:49:92".toLowerCase());
        macs.add("E0:30:8C:37:69:5A".toLowerCase());
        macs.add("E5:15:49:AB:3A:76".toLowerCase());
        macs.add("EB:5F:42:C4:06:48".toLowerCase());
        macs.add("E1:76:DC:38:06:1C".toLowerCase());
        macs.add("F6:FF:9A:02:14:D7".toLowerCase());
        macs.add("DF:CF:D5:9A:C9:7A".toLowerCase());
        macs.add("FB:EB:F0:C8:42:44".toLowerCase());
        macs.add("C0:A3:A0:DE:0C:9F".toLowerCase());
        macs.add("FE:F0:14:E9:1E:59".toLowerCase());
        return macs;
    }
    
    private static String convertirHora(double d) {
        int hora= (int)(d/3600);
        d = d-(hora*3600);
        int minutos=(int)(d/60);
        d=d-(minutos*60);
        return hora+":"+minutos+":"+d;
    }

    private static String reverse(String palabra) {
        if (palabra.length() == 1) {
            return palabra;
        } else {    
            return reverse(palabra.substring(1)) + palabra.charAt(0);
        }
    }
}
