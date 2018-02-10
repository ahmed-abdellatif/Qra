/*
 * Ahmed Abdellatif
 * Initiative: Issues with reading excel sheet delivered by
 * Qradar, & appending IP addresses
 * 
 */
package iplocator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.Math.log;
import static java.lang.StrictMath.log;
import static java.lang.System.exit;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import com.opencsv.CSVReader;

import java.lang.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class Iplocator {

    public static void main(String[] args) throws IOException {
        File f;
        f = getFile();
        System.out.println(f.toString());
        CSVreader csv = new CSVreader();
        CSVwriter write = new CSVwriter();
        String csvFile = f.toString();
        BufferedReader br = null;
        String line = "";
        String splitby = ",";
        String[] ip = new String[3000];
        String[][] loc = new String[3000][3];
        csv.readCSV(ip, csvFile, br, line, splitby);
                
        int i=1;
        while(i<=3000) {
        try {    
        loc[i] = locator(ip[i]);
        loc[i][2] = ip[i];
        i+=1; }
        catch(FileNotFoundException ae) {break; }
        
    }
        JFileChooser jfc = new JFileChooser();
        int result = jfc.showSaveDialog(null);
        if(result == JFileChooser.CANCEL_OPTION)
            return;
        File file = jfc.getSelectedFile();
        
        
        System.out.println(Arrays.toString(loc[1]));
        write.writeCSV(loc, file.toString());
        
        Workbook wb = new HSSFWorkbook();
        CreationHelper helper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
        CellStyle style = wb.createCellStyle();
        style.setFillBackgroundColor(IndexedColors.AQUA.getIndex());
        style.setFillPattern(CellStyle.BIG_SPOTS);
        CSVReader reader = new CSVReader(new FileReader(file.toString()));
        String[] line1;
        int r = 0;
        while ((line1 = reader.readNext()) != null) {
            Row row = sheet.createRow((short) r++);
            
            for (int k = 0; i < line1.length; k++)
                row.createCell(k)
                   .setCellValue(helper.createRichTextString(line1[k]));
            if(line1[1] != "United States") {
                row.setRowStyle(style);
            }
        
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        wb.write(fileOut);
        fileOut.close();
        
        
    }}  
    
    public static File getFile() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(null);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        }
        return file;
    }
   
    
    public static String[] locator(String ip1) throws MalformedURLException, IOException {
        String[] locdetail = new String[3];
        URL url = new URL("http://freegeoip.net/csv/" + ip1);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream is = connection.getInputStream();

        int status = connection.getResponseCode();
        if (status != 200) {
            return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        for (String line; (line = reader.readLine()) != null;) {
            System.out.println(line);
            locdetail[0] = line.split(",")[2];
            locdetail[1] = line.split(",")[7].split("/")[0];
            
        } 
       return locdetail;
        
       }   
    
class CSVreader {
    
    int counter = 0;
    public String[] readCSV(String[] ip,String file, BufferedReader br, String line, String splitby) {
        try {
            
            br = new BufferedReader(new FileReader(file));
            while (( line = br.readLine()) != null) {
                if(counter == 0) {
                    counter += 1;
                    continue;
                }
                ip[counter] = line.split(splitby)[0]; 
                counter += 1;
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(br != null) {
                try {
                    br.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    return ip;    
    }  
}

class CSVwriter {
    
    private static final String FILE_HEADER = "IP address,Region,Continent";
    
    public void writeCSV(String[][] details, String fileName) {
        FileWriter fileWriter = null; 
        
        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());
            fileWriter.append("\n");
            int i = 1;
            while(i<3000) {
                try {
                    fileWriter.append(details[i][2]);
                    fileWriter.append(",");
                    fileWriter.append(details[i][0]);
                    fileWriter.append(",");
                    fileWriter.append(details[i][1]);
                    fileWriter.append("\n");
                    i+=1;
                }
                catch(FileNotFoundException ae) { break;} 
            }
            
            System.out.println("Written to CSV");
            
        }
        catch(Exception e) {}
        finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}









