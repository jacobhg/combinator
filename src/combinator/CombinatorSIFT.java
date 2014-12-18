package combinator;

import utilities.CombinatorAnnotator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;

/**
 * ----- Añadido el 17/12/2014 -----
 * Clase en la que tendremos las funciones necesarias para que dados dos ficheros de entrada, uno en formato .vcf obtenido de *
 * CombinatorVcf y otro obtenido del anotador SIFT, nos devuelva un fichero de salida en el que tendremos las líneas del      *
 * fichero .vcf pero en el campo INFO se habrán añadido los campos de interés del fichero SIFT; También se añaden las líneas  *
 * de cabecera correspondientes a los campos que se encuentran en el fichero SIFT.                                            *
 *                                                                                                                            *
 * @author Jacob Henríquez
 */
public class CombinatorSIFT {
    // Vector de string que representa los distintos campos que nos podemos encontrar en el fichero obtenido de SIFT:
    public final String [] sift_fields_long_name = {
        "Codons", "Ensembl Transcript ID", "RefSeq Transcript ID", "Known Transcript ID", "CCDS Transcript ID", "Ensembl Protein ID",
        "RefSeq Protein ID", "Known Protein ID", "Substitution", "Region", "SNP Type", "Prediction", "Score", "Median Info",
        "# Seqs at position"};
    
    // Vector que representa las siglas o el nombre corto que se ha establecido para los diferentes campos que podemos encontrarnos 
    // en el fichero obtenido de SIFT:
    public final String [] sift_fields_short_name = {
        "COD", "ENt", "RFt", "KNt", "CCt", "ENp", "RFp", "KNp", "SUBS", "REG", "SYN", "SIFTp", "SIFTs", "SIFTm", "SEQS"};
    
    // Vector en el que tenemos las líneas de cabecera correspondientes a los campos del fichero obtenido de SIFT y que se incluirán 
    // como líneas de cabecera en el fichero de salida:
    public final String[] sift_header_lines = {
        "##INFO=<ID=COD,Number=1,Type=String,Description=\"The alternative codons\">", 
        "##INFO=<ID=ENt,Number=1,Type=String,Description=\"Ensemble transcript ID\">",
        "##INFO=<ID=RFt,Number=1,Type=String,Description=\"RefSeq transcript ID\">",
        "##INFO=<ID=KNt,Number=1,Type=String,Description=\"Known transcript ID\">",
        "##INFO=<ID=CCt,Number=1,Type=String,Description=\"CCDS transcript ID\">",
        "##INFO=<ID=ENp,Number=1,Type=String,Description=\"Ensembl Protein ID\">",
        "##INFO=<ID=RFp,Number=1,Type=String,Description=\"RefSeq Protein ID\">",
        "##INFO=<ID=KNp,Number=1,Type=String,Description=\"Known Protein ID\">",
        "##INFO=<ID=SUBS,Number=1,Type=String,Description=\"Amino acid substitution\">",
        "##INFO=<ID=REG,Number=1,Type=String,Description=\"SIFT region\">",
        "##INFO=<ID=SYN,Number=1,Type=String,Description=\"SNP Type. Synonymous, nonsynonymous\">",
        "##INFO=<ID=SIFTp,Number=1,Type=String,Description=\"SIFT prediction\">",
        "##INFO=<ID=SIFTs,Number=1,Type=String,Description=\"SIFT score\">",
        "##INFO=<ID=SIFTm,Number=1,Type=String,Description=\"SIFT median info\">",
        "##INFO=<ID=SEQS,Number=1,Type=String,Description=\"Number of sequences\">" };
    
    // Buffered Reader para el fichero de entrada (en formato .vcf) obtenido de CombinatorVcf:
    public static BufferedReader vcf_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero .vcf:
    public static String vcf_line;
    
    // Buffered Reader para el fichero de entrada obtenido de SIFT:
    public static BufferedReader sift_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero SIFT:
    public static String sift_line;
    
    
    public CombinatorSIFT(){
        
    }
    
    void start() throws FileNotFoundException, IOException{
        // Fichero de entrada obtenido de CombinatorVcf (en formato .vcf):
        File vcf_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084.vcf");
        // Fichero de salida del anotador SIFT en formato .txt:
        File sift_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084_SIFT.tsv");

        // Fichero de salida (en formato .vcf) en el que tendremos las líneas del fichero obtenido de CombinatorVcf con 
        // la adición de la información de interés del anotador SIFT como subcampos del campo INFO. El fichero también 
        // tendrá sus correspondientes líneas de cabecera:
        String vcf_name = vcf_file.getName();
        File output_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/add_SIFT" + vcf_name);
        
        // Función que añade la información obtenida de VEP al fichero .vcf obtenido de CombinatorVcf:
        addSiftToVcf (vcf_file, sift_file, output_file);
        
    }
    
     /**
     * ----- Añadido el 17/12/2014 -----
     * Función que se utiliza para generar un fichero de salida en el que se ha añadido la información de interés obtenida
     * del anotador SIFT al fichero .vcf que tenemos como resultado de CombinatorVcf.
     * @param vcf_file : Fichero de entrada en formato .vcf obtenido de CombinatorVcf.
     * @param sift_file : Fichero de entrada con la información del anotador SIFT. 
     * @param output_file : Fichero de salida en el que aparecerá combinada la información de los ficheros de entrada.
     * @throws IOException
     * @throws FileNotFoundException 
     */
    private void addSiftToVcf(File vcf_file, File sift_file, File output_file) throws IOException, FileNotFoundException{
         // Se lee la primera línea del fichero de entrada .vcf obtenido de CombinatorVcf:
        vcf_br = new BufferedReader(new FileReader(vcf_file));
        vcf_line = vcf_br.readLine();
        
        // Se lee la primera línea del fichero de entrada obtenido de SIFT (línea que indica la versión de SIFT):
        sift_br = new BufferedReader(new FileReader(sift_file));
        sift_line = sift_br.readLine();
        // Se lee la línea que contiene las cabeceras del fichero SIFT:
        sift_line = sift_br.readLine();
        // Vector en el que tendremos todas las cabeceras de los campos de SIFT:
        String[] sift_headers = sift_line.split("\t");
        // Se lee la primera línea con datos del fichero de SIFT:
        sift_line = sift_br.readLine();
        
        // Fichero de salida en el que tendremos las líneas del fichero .vcf con la adición de los campos de SIFT como 
        // subcampos del campo INFO:
        FileWriter output_wr = new FileWriter(output_file);
        PrintWriter print_out = new PrintWriter(output_wr);
        
        // Se copian en el fichero de salida las líneas de cabecera del fichero obtenido de CombinatorVcf y se añaden las 
        // líneas de cabecera correspondientes a los campos de SIFT (al acabar esta función nos encontraremos situados en 
        // la primera línea con datos del fichero .vcf obtenido de CombinatorVcf):
        generateOutputHeader(print_out);
        
        // Recorrido del fichero de entrada .vcf:
        while (vcf_line != null){
            // En un vector almacenamos los campos del fichero .vcf de la línea leída:
            String[] vcf_fields = vcf_line.split("\t");
            // Recorrido del fichero de entrada VEP:
            while (sift_line != null){
                // Almacenamos en un vector los campos del fichero SIFT de la línea leída:
                String[] sift_fields = sift_line.split("\t");
                // Del campo "Coordinates" (campo 1) del fichero SIFT, obtenemos el cromosoma y la posición para poder comparar
                // con el cromosoma (CHROM) y la posición (POS) del .vcf:
                String[] sift_chrom_pos = sift_fields[0].split(",");
                
                // Comprobar que el cromosoma y la posición de .vcf y SIFT sean los mismos:
                // Caso 1: Si el cromosoma (CHROM) de ambos ficheros coincide, se comprobará la posición (POS) dentro del cromosoma.
                if (CombinatorVcf.posOfChrom(vcf_fields[0]) == CombinatorVcf.posOfChrom(sift_chrom_pos[0])){
                    // Caso 1.1: Si la posición (POS) del fichero SIFT es mayor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero .vcf (es decir, se lee la siguiente línea del fichero .vcf).
                    if ((Integer.parseInt(sift_chrom_pos[1])) > (Integer.parseInt(vcf_fields[1]))){  
                        // Escribimos líneas del fichero .vcf que no coinciden con el fichero SIFT:
                        print_out.println(vcf_line);
                        
                        break;
                    }                  
                    // Caso 1.2: Si la posición (POS) del fichero SIFT es menor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero SIFT (es decir, se lee la siguiente línea del fichero SIFT).
                    else if ((Integer.parseInt(sift_chrom_pos[1])) < (Integer.parseInt(vcf_fields[1]))){                        
                        sift_line = sift_br.readLine();
                    }
                    // Caso 1.3: Si las posiciones (POS) coinciden, se combina la información de esas líneas (se añade al campo INFO del
                    // fichero .vcf de salida: el campo INFO del fichero .vcf de entrada y los campos de interés de VEP).
                    else{
                        // Se genera el map con los subcampos del campo INFO del fichero .vcf y los campos de interés del fichero SIFT:
                        generateSiftVcfMap (vcf_fields, sift_fields, sift_headers);
                                                
                        // Se genera la línea que se escribirá en el fichero de salida:
                        String output_line = vcf_fields[0];
                        // Agregamos los campos principales del fichero .vcf (todos menos INFO):
                        for (int i = 1; i < 7; i++){
                            output_line += "\t" + vcf_fields[i];
                        }
                        // Agregamos a la línea el nuevo campo INFO que contiene los campos del .vcf y del VEP:
                        output_line += "\t" + CombinatorAnnotator.generateOutputInfoField ();
                        
                        // Se escribe la línea generada en el fichero de salida:
                        print_out.println(output_line);
                        
                        // Vaciamos el map para utilizarlo en la siguiente iteración: 
                        CombinatorAnnotator.info_fields_map.clear();
                        
                        // Se lee la siguiente línea en el fichero VEP (se avanza a la siguiente posición (POS)):
                        sift_line = sift_br.readLine();
                        break;
                    }
                }
                // Caso 2: Si el cromosoma (CHROM) es diferente en ambos ficheros.
                else{  
                    // Caso 2.1: Si el cromosoma (CHROM) del fichero .vcf es menor que el del fichero SIFT, avanzamos en el fichero .vcf 
                    // (se lee la siguiente línea del fichero .vcf).
                    if (CombinatorVcf.posOfChrom(vcf_fields[0]) < CombinatorVcf.posOfChrom(sift_chrom_pos[0])){
                        // Escribimos líneas del fichero .vcf que no coinciden con el fichero SIFT:
                        print_out.println(vcf_line);
                        
                        break;
                    }
                    // Caso 2.2: Si el cromosoma (CHROM) del fichero .vcf es mayor que el del fichero SIFT, avanzamos en el fichero SIFT 
                    // (se lee la siguiente línea del fichero SIFT).
                    else if (CombinatorVcf.posOfChrom(vcf_fields[0]) > CombinatorVcf.posOfChrom(sift_chrom_pos[0])){
                        // El fichero SIFT tiene una serie de líneas al final que vuelven a empezar desde el cromosoma 1. Ese grupo de 
                        // líneas no los comprobaremos y seguiremos copiando las líneas del fichero .vcf de entrada:
                        if (CombinatorVcf.posOfChrom(vcf_fields[0]) - CombinatorVcf.posOfChrom(sift_chrom_pos[0]) > 2){
                            // Escribimos líneas del fichero .vcf que no coinciden con el fichero SIFT:
                            print_out.println(vcf_line);
                            
                            break;
                        }
                        else{
                            sift_line = sift_br.readLine();
                        }
                    } 
                }
            }
            vcf_line = vcf_br.readLine();
        }
        
        // Se cierran los dos ficheros de entrada y el fichero de salida:
        vcf_br.close();
        sift_br.close();
        output_wr.close();  
    }

    /**
     * ----- Añadido el 17/12/2014 -----
     * Función que se utiliza para copiar en el fichero de salida las cabeceras del fichero .vcf y además se añaden las
     * cabeceras para los campos obtenidos del fichero SIFT.
     * @param print_out
     * @throws IOException 
     */
    private void generateOutputHeader(PrintWriter print_out) throws IOException {
        // Se recorre el fichero .vcf:
        while (vcf_line != null){
            // Leemos todas las líneas que son de cabecera (las que empiezan por "#"):
            while (vcf_line.startsWith("#")){
                // Copiamos las líneas de cabeceras del campo INFO:
                if (vcf_line.startsWith("##INFO")){
                    print_out.println(vcf_line);
                    vcf_line = vcf_br.readLine();
                    while (vcf_line.startsWith("##INFO")){
                        print_out.println(vcf_line);
                        vcf_line = vcf_br.readLine();
                    }
                    // Añadimos debajo de las líneas de cabecera del campo INFO obtenidas del .vcf, las líneas de cabecera 
                    // del campo INFO para la información de SIFT:
                    for (int i = 0; i < sift_header_lines.length; i++){
                        print_out.println(sift_header_lines[i]);
                    }
                }
                // Copiamos el resto de líneas de cabecera del fichero .vcf de entrada:
                else {
                    print_out.println(vcf_line);
                    vcf_line = vcf_br.readLine();
                }
            }
            break;
        }  
    }

    /**
     * ----- Añadido el 17/12/2014 -----
     * Función que nos indica la posición de un campo del fichero SIFT en el vector de string que representa los campos que 
     * nos interesan de este tipo de ficheros.
     * @param sift_field_name : Nombre del campo del fichero SIFT que queremos comprobar si se encuentra en el listado
     *                         de campos definidos en el vector "sift_fields_long_name".
     * @return : Devuelve la posición del vector en la que se encuentra el campo y si no lo ha encontrado devuelve -1.
     */
    private int indexOfSiftField (String sift_field_name) {
        for (int i = 0; i < sift_fields_long_name.length; i++) {
            if (sift_fields_long_name[i].equals(sift_field_name)) {
                return i;
            }
        }
        return -1;
    } 
    
    /**
     * ----- Añadido el 17/12/2014 -----
     * Función que se encargará de generar un map con todos los campos de interés obtenidos del fichero .vcf y del fichero 
     * SIFT, donde la clave del map será el acrónimo o nombre corto que representa el campo y el valor será el valor del 
     * propio campo.
     * @param vcf_fields : Campos obtenidos del fichero .vcf de entrada.
     * @param sift_fields : Campos obtenidos del fichero SIFT.
     * @param sift_headers : Nombre de las cabeceras del fichero SIFT.
     */
    private void generateSiftVcfMap(String[] vcf_fields, String[] sift_fields, String[] sift_headers) {
        // Nos quedamos con los subcampos del campo INFO (campo 8) de los campos pasados por parámetro del fichero .vcf:
        String[] vcf_info = vcf_fields[7].split(";");
                        
        // Map en el que tendremos almacenados los campos que necesitemos de los ficheros .vcf y VEP (los campos se 
        // ordenarán alfabéticamente): 
        CombinatorAnnotator.info_fields_map = new TreeMap();
        
        // PASO 1: Almacenamos en el map los subcampos del campo INFO del fichero .vcf.
        for (int i = 0; i < vcf_info.length; i++){
            // Controlamos que se añadan correctamente las etiquetas MistZone, ya que es un campo que no es del tipo
            // "nombre_del_campo=valor":
            if (vcf_info[i].startsWith("MistZone")){
                CombinatorAnnotator.info_fields_map.put(vcf_info[i], vcf_info[i]);
            }
            // Para el resto de los subcampos:
            else{
                String[] vcf_sub_info = vcf_info[i].split("=");                
                CombinatorAnnotator.info_fields_map.put(vcf_sub_info[0], vcf_sub_info[1]);
            }
        }
        
        // PASO 2: Almacenamos en el map los campos de interés del fichero SIFT.
        for (int i = 0; i < sift_fields.length; i++){
            // Si el campo se encuentra dentro de la lista de campos que nos interesan:
            if (indexOfSiftField(sift_headers[i]) >= 0){
                // Comprobamos si existe un valor para ese campo y así evitamos insertar en el map campos que tengan 
                // valores perdidos (los valores perdidos en el fichero SIFT se representan por el símbolo "-" o " "):
                if (!((sift_fields[i].equals("-")) || (sift_fields[i].equals(" ")))){
                    CombinatorAnnotator.info_fields_map.put(sift_fields_short_name[indexOfSiftField(sift_headers[i])], sift_fields[i]); 
                }
            } 
        }   
    }
       
}
