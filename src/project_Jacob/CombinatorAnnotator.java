package project_Jacob;

import java.util.Map;

/**
 * ----- Añadido el 18/12/2014 -----
 * Clase en la que tendremos las funciones de uso común para las clases que se encargan de tratar la información  *
 * obtenida de los anotadores VEP (CombinatorVEP), SIFT (CombinatorSIFT) y ANNOVAR (CombinatorANNOVAR).           *
 *                                                                                                                *
 * @author Jacob Henríquez
 */
public class CombinatorAnnotator {
    // Map que utilizarán las clases que se encargan de tratar la información obtenida de los anotadores VEP (CombinatorVEP), 
    // SIFT (CombinatorSIFT) y ANNOVAR (CombinatorANNOVAR) en el que se guardarán todos los campos de los ficheros obtenidos 
    // de dichos anotadores:
    public static Map<String, String> info_fields_map;
    
    /**
     * Función que se utiliza para generar la línea INFO de salida con todos los campos que nos interesan de los ficheros 
     * .vcf de entrada y cualquiera de los ficheros obtenidos de uno de los anotadores (VEP, SIFT o ANNOVAR). 
     * @return : Devuelve la línea generada.
     */
    public static String generateOutputInfoField() {
        // Nueva línea de info que se escribirá en el fichero de salida:
        String output_info_line = "";
        
        // Se genera la primera parte de la línea INFO de salida:
        output_info_line += info_fields_map.keySet().toArray()[0].toString() + "=" + info_fields_map.values().toArray()[0].toString();
        // Se genera el resto de la línea INFO de salida:
        for (int i = 1; i < info_fields_map.size(); i++){
            // Controlamos la etiqueta "MistZone" ya que es la única que no es del tipo: "Etiqueta=Valor":
            if (info_fields_map.keySet().toArray()[i].toString().equals("MistZone")){
                output_info_line += ";" + info_fields_map.values().toArray()[i].toString(); 
            }
            // Para los campos que no son la etiqueta "MistZone":
            else {
                output_info_line += ";" + info_fields_map.keySet().toArray()[i].toString() + "=" + info_fields_map.values().toArray()[i].toString();
            }
        }
        
        // Se devuelve la línea generada:
        return output_info_line;
    }  
    
}
