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
    java -jar ProcesadoDatos.jar "C:\Users\Hector\Documents\NetBeansProjects\ProcesadoDatos\src\procesadodatos\csv\PUNTOREF_5_5_0_0_500_20180503_115213.csv" "C:\Users\Hector\Documents\NetBeansProjects\ProcesadoDatos\src\procesadodatos\csv\PUNTOREF_5_5_180_0_500_20180503_115435.csv" "C:\Users\Hector\Documents\NetBeansProjects\ProcesadoDatos\src\procesadodatos\csv\PUNTOREF_5_5_270_0_500_20180503_115547.csv"
    */
    public static final String SEPARATOR=",";
    public static final char QUOTE='"';
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ArrayList<String> macs = devolverMacs();
        if(args.length > 0){
            String[] ruta = args[0].split("\\\\");
            String[] archivo = ruta[ruta.length-1].split("_");
            String nombreArchivo = "";
            for(int i = 0; i< ruta.length-1; i++){
                nombreArchivo += ruta[i] + "\\";
            }
            nombreArchivo +=  archivo[0]+"_"+archivo[1]+"_"+archivo[2]+"_"+archivo[4]+"_"+archivo[5]+"_"+archivo[6]+"_"+archivo[7].substring(0,archivo[7].length()-5)+"_PROCESADO.xlsx";
            System.out.println("Voy a crear la base de datos en " + nombreArchivo);
            //Crear libro de trabajo con la estructura basica
            XSSFWorkbook libroTrabajo = new XSSFWorkbook();
            for(int fichero = 0; fichero<args.length; fichero++){
                XSSFSheet[] hojas = new XSSFSheet[6];
                int k=7;
                String[] file = args[fichero].split("\\\\");
                String[] procesada = file[file.length-1].split("_");
                String grados = procesada[procesada.length-5];
                String hours = procesada[procesada.length-1];
                int horas = Integer.parseInt((Character.toString(hours.charAt(0))+Character.toString(hours.charAt(1))))*3600
                        +Integer.parseInt((Character.toString(hours.charAt(2))+Character.toString(hours.charAt(3))))*60
                        +Integer.parseInt((Character.toString(hours.charAt(4))+Character.toString(hours.charAt(5))));
                for (int i=0; i<6;i++){
                    if(i%2==0){
                        hojas[i]=libroTrabajo.createSheet("Canal3"+k+"eBeacon-"+grados);
                    }else{
                        hojas[i]=libroTrabajo.createSheet("Canal3"+k+"Edystone-"+grados);
                        k++;
                    }
                }
                XSSFRow[] rows =new XSSFRow[hojas.length];
                for(int i=0;i<hojas.length;i++){
                    rows[i]=hojas[i].createRow(0);
                }
                boolean par = true;
                int cont = 1;
                for (int c = 0; c < macs.toArray().length*2; c++) {
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
                String csvFile = args[fichero];
                BufferedReader br = null;
                String line = "";
                //Se define separador ","
                String cvsSplitBy = ",";
                System.out.println("Procesando fichero: "+args[fichero]+" ...");
                System.out.println("-------------------------------------");
                try {
                    br = new BufferedReader(new FileReader(csvFile));
                    boolean first = true;
                    int time = 1, source = 2, length = 5, chanel = 6, rssi = 7, crc = 8, row = 0;
                    while ((line = br.readLine()) != null) {
                        if(first){
                            first=false;
                            String[] datos = line.split(cvsSplitBy);
                            for(int dato=0; dato < datos.length; dato++){
                                //System.out.print(datos[dato]+", ");
                                if(datos[dato].trim().equals("\"Time\"")){
                                    time=dato;
                                }
                                if(datos[dato].trim().equals("\"Source\"")){
                                    source=dato;
                                }
                                if(datos[dato].trim().equals("\"Length\"")){
                                    length=dato;
                                }
                                if(datos[dato].trim().equals("\"Channel\"")){
                                    chanel=dato;
                                }
                                if(datos[dato].trim().equals("\"RSSI (dBm)\"")){
                                    rssi=dato;
                                }
                                if(datos[dato].trim().equals("\"CRC\"")){
                                    crc=dato;
                                }
                            }
                        }else{
                            String[] datos = line.split(cvsSplitBy);
                            //Imprime datos.
                            int hoja= -1;
                            boolean ebeacon = true;
                            if (datos[length].equals("\"63\"")) {
                                ebeacon = false;
                            } else {
                                ebeacon = true;
                            }
                            switch(datos[chanel]){
                                case "\"37\"":
                                    if(ebeacon){
                                        hoja =0;
                                    }else{
                                        hoja =1;
                                    }
                                    break;
                                case "\"38\"":
                                    if(ebeacon){
                                        hoja =2;
                                    }else{
                                        hoja =3;
                                    }
                                    break;
                                case "\"39\"":
                                    if(ebeacon){
                                        hoja =4;
                                    }else{
                                        hoja =5;
                                    }
                                    break;
                            }
                            if(hoja > -1 && macs.indexOf(datos[source]) >= 0 && datos[crc].equals("\"OK\"")){
                               for(int i = 0; i <= row; i++){
                                    Row row1 = hojas[hoja].getRow(i);
                                    if(row1 == null) {
                                        row1 = hojas[hoja].createRow(i);
                                        i=row+1;
                                    }
                                    Cell celda1 = row1.getCell(macs.indexOf(datos[source]) * 2);
                                    if (celda1 == null) {
                                        celda1 = row1.createCell(macs.indexOf(datos[source]) * 2);
                                        celda1.setCellType(Cell.CELL_TYPE_NUMERIC);
                                        celda1.setCellValue(convertirHora(Double.parseDouble(datos[time].substring(1,datos[time].length()-1))+horas));
                                        celda1 = row1.createCell((macs.indexOf(datos[source]) * 2) + 1);
                                        celda1.setCellValue(Integer.parseInt(datos[rssi].substring(1,datos[rssi].length()-1)));
                                        i=row+1;
                                    }
                                }
                                row++;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //cerramos el csv
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //escribir este libro en un OutputStream.
            FileOutputStream fileOut = new FileOutputStream(nombreArchivo);
            libroTrabajo.write(fileOut);
            fileOut.flush();
            fileOut.close();
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
        macs.add("\"D8:07:9F:BB:65:8E\"".toLowerCase());
        macs.add("\"C8:A5:CD:C0:66:8F\"".toLowerCase());
        macs.add("\"E7:0D:93:F0:49:92\"".toLowerCase());
        macs.add("\"E0:30:8C:37:69:5A\"".toLowerCase());
        macs.add("\"E5:15:49:AB:3A:76\"".toLowerCase());
        macs.add("\"EB:5F:42:C4:06:48\"".toLowerCase());
        macs.add("\"E1:76:DC:38:06:1C\"".toLowerCase());
        macs.add("\"F6:FF:9A:02:14:D7\"".toLowerCase());
        macs.add("\"DF:CF:D5:9A:C9:7A\"".toLowerCase());
        macs.add("\"FB:EB:F0:C8:42:44\"".toLowerCase());
        macs.add("\"C0:A3:A0:DE:0C:9F\"".toLowerCase());
        macs.add("\"FE:F0:14:E9:1E:59\"".toLowerCase());
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
