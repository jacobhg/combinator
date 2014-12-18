package combinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase principal desde la que se llama a las diferentes funciones para tratar la información *
 * procedente de ficheros en formato .vcf y formato MIST.                                      *
 * 
 * @author Jacob Henríquez
 */
public class CombinatorMain {
            
    public static void main(String[] args) throws IOException, FileNotFoundException {
        //calcularMediaCalidad();
        //calcularMediaDP();
        //new CombinatorMist().start();
        //new CombinatorVcf().start(); 
        //new CombinatorVEP().start();
        new CombinatorSIFT().start();

    }
        
    /**
     * Función que se utiliza para calcular la media aritmética del campo "QUAL" (calidad)
     * de las diferentes líneas de un fichero .vcf. 
     */
    private static void calcularMediaCalidad() {
        File file = new File("/home/uai02/Investigacion_Jacob/niv32.flt.vcf");
        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line = input.readLine();
            
            //----- Variables utilizadas:
            // Contador del número de calidades leídas:
            int qual_count = -1;
            // Variable para guardar el valor de las calidades leídas:
            double quality;
            // Variable para acumular la suma de las calidades:
            double qualityt = 0.0;
            // Promedio de las calidades:
            double average;
            //----- Fin variables utilizadas.
            
            // Se recorre el fichero:
            while (line != null) {
                String[] row = line.split("\t");
                // Se lee el campo de las calidades:
                if (row.length > 5){
                    if (qual_count >= 0){
                        // Se van sumando las distintas calidades:
                        quality = Double.parseDouble (row[5]);
                        qualityt += quality;
                    }
                    qual_count++; 
                }
                line = input.readLine();
            }
            // Se calcula la media aritmética de las calidades:
            average = qualityt / qual_count;
            System.out.println("Calidad promedio: " + average);
            
            // Cerramos el fichero de entrada:
            input.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }

    /**
     * Función que se utiliza para calcular la media aritmética del campo "DP" de las 
     * diferentes líneas de un fichero .vcf. 
     */
    private static void calcularMediaDP() {
        File file = new File("/home/uai02/Investigacion_Jacob/niv32.flt.vcf");
        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line = input.readLine();
            //----- Variables utilizadas:
            // Contador del número de DP leídos:
            int dp_count = -1;
            // Variable para guardar el valor de DP leído:
            int dpvalue;
            // Variable para acumular la suma de DP:
            int dpt = 0;
            // Valor promedio de DP:
            double dpaverage;
            //----- Fin variables utilizadas:
            
            // Se recorre el fichero:
            while (line != null) {
                String[] row = line.split("\t");
                // Se lee el campo INFO:
                if (row.length > 7){
                    if (dp_count >= 0){ 
                        String[] parts = row[7].split(";");
                        // Dentro del campo INFO se lee el subcampo DP:
                        for (int i = 0; i < parts.length; i++){
                            if(parts[i].startsWith("DP=")){
                                String[] dp = parts[i].split("=");
                                dpvalue = Integer.parseInt (dp[1]);
                                dpt += dpvalue;
                            }
                            
                        }
                    }
                    dp_count++;
                }
                line = input.readLine();
            }
            // Cálculo de la media aritmética de DP:
            dpaverage = (double) dpt / dp_count;
            System.out.println("Promedio de DP: " + dpaverage);
            
            // Cerramos el fichero de entrada:
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
                
}
