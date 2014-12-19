package combinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import utilities.CombinatorAnnotator;

/**
 * ----- Añadido el 18/12/2014 -----
 * Clase en la que tendremos las funciones necesarias para que dados dos ficheros de entrada, uno en formato .vcf obtenido de *
 * CombinatorVcf y otro obtenido del anotador ANNOVAR, nos devuelva un fichero de salida en el que tendremos las líneas del   *
 * fichero .vcf pero en el campo INFO se habrán añadido los campos de interés del fichero ANNOVAR; También se añaden las      *
 * líneas de cabecera correspondientes a los campos que se encuentran en el fichero ANNOVAR.                                  *
 *                                                                                                                            *
 * @author Jacob Henríquez
 */
public class CombinatorANNOVAR {
    // Vector de string que representa los distintos campos que nos podemos encontrar en el fichero obtenido de ANNOVAR:
    public final String [] annovar_filter_long_name = {
        "Func.refgene", "Gene.refgene", "GeneDetail.refgene", "ExonicFunc.refgene", "AAChange.refgene", "phastConsElements46way",
        "esp6500si_all", "1000g2014oct_all", "snp138", "SIFT_score", "SIFT_pred", "Polyphen2_HDIV_score", "Polyphen2_HDIV_pred",
        "Polyphen2_HVAR_score", "Polyphen2_HVAR_pred", "LRT_score", "LRT_pred", "MutationTaster_score", "MutationTaster_pred",
        "MutationAssessor_score", "MutationAssessor_pred", "FATHMM_score", "FATHMM_pred", "RadialSVM_score", "RadialSVM_pred",
        "LR_score", "LR_pred", "VEST3_score", "CACADD_raw", "CADD_phred", "GERP++_RS", "phyloP46way_placental", 
        "phyloP100way_vertebrate", "SiPhy_29way_logOdds", "genomicSuperDups", "clinvar_20140929", "gwasCatalog" };
    
    // Vector que representa las siglas o el nombre corto que se ha establecido para los diferentes campos que podemos encontrarnos 
    // en el fichero obtenido de ANNOVAR:
    public final String [] annovar_filter_short_name = {
        "RGN", "GNAME", "DETAIL", "FUNC", "AACH", "C46W", "E6K", "1KG14", "SNP138", "SIFTs", "SIFTp", "PPD2s", "PPD2p", "PPV2s", 
        "PPV2p", "LRTs", "LRTp", "MTs", "MTp", "MAs", "MAp", "FATs", "FATp", "RSs", "RSp", "LRs", "LRp", "VEs", "CAC", "CACp",
        "GERP", "PHYp", "PHYv", "SIPHY", "GSD", "CLIN", "GWAS" };
    
    // Vector en el que tenemos las líneas de cabecera correspondientes a los campos del fichero obtenido de ANNOVAR y que se incluirán 
    // como líneas de cabecera en el fichero de salida:
    public final String[] annovar_header_lines = {
        "##INFO=<ID=RGN,Number=1,Type=String,Description=\"exonic,splicing,ncRNA,UTR5,UTR3,intronic,upstream,downstream,intergenic\">",
        "##INFO=<ID=GNAME,Number=1,Type=String,Description=\"Gene symbol or name\">",
        "##INFO=<ID=DETAIL,Number=1,Type=String,Description=\"Distance to nearest genes\">",
        "##INFO=<ID=FUNC,Number=1,Type=String,Description=\"Exonic variant function\">",
        "##INFO=<ID=AACH,Number=1,Type=String,Description=\"Amino acid changes\">",
        "##INFO=<ID=C46W,Number=1,Type=String,Description=\"phastConsElements46way\">",
        "##INFO=<ID=E6K,Number=1,Type=String,Description=\"esp6500si_all\">",
        "##INFO=<ID=1KG14,Number=1,Type=String,Description=\"1000g2014oct_all\">",
        "##INFO=<ID=SNP138,Number=1,Type=String,Description=\"snp138\">", 
        "##INFO=<ID=SIFTs,Number=1,Type=String,Description=\"SIFT_score\">",
        "##INFO=<ID=SIFTp,Number=1,Type=String,Description=\"SIFT_prediction\">",
        "##INFO=<ID=PPD2s,Number=1,Type=String,Description=\"Polyphen2 HDIV score\">",
        "##INFO=<ID=PPD2p,Number=1,Type=String,Description=\"Polyphen2 HDIV prediction\">",
        "##INFO=<ID=PPV2s,Number=1,Type=String,Description=\"Polyphen2 HVAR score\">",
        "##INFO=<ID=PPV2p,Number=1,Type=String,Description=\"Polyphen2 HVAR prediction\">",
        "##INFO=<ID=LRTs,Number=1,Type=String,Description=\"LRT score\">",
        "##INFO=<ID=LRTp,Number=1,Type=String,Description=\"LRT prediction\">",
        "##INFO=<ID=MTs,Number=1,Type=String,Description=\"MutationTaster score\">",
        "##INFO=<ID=MTp,Number=1,Type=String,Description=\"MutationTaster prediction\">",
        "##INFO=<ID=MAs,Number=1,Type=String,Description=\"MutationAssessor score\">",
        "##INFO=<ID=MAp,Number=1,Type=String,Description=\"MutationAssessor prediction\">",
        "##INFO=<ID=FATs,Number=1,Type=String,Description=\"FATHMM score\">",
        "##INFO=<ID=FATp,Number=1,Type=String,Description=\"FATHMM prediction\">",
        "##INFO=<ID=RSs,Number=1,Type=String,Description=\"RadialSVM score\">",
        "##INFO=<ID=RSp,Number=1,Type=String,Description=\"RadialSVM prediction\">",
        "##INFO=<ID=LRs,Number=1,Type=String,Description=\"LR_score\">",
        "##INFO=<ID=LRp,Number=1,Type=String,Description=\"LR prediction\">",
        "##INFO=<ID=VEs,Number=1,Type=String,Description=\"VEST3 score\">",
        "##INFO=<ID=CAC,Number=1,Type=String,Description=\"CACADD raw\">",
        "##INFO=<ID=CACp,Number=1,Type=String,Description=\"CADD phred\">",
        "##INFO=<ID=GERP,Number=1,Type=String,Description=\"GERP++_RS\">",
        "##INFO=<ID=PHYp,Number=1,Type=String,Description=\"phyloP46way_placental\">",
        "##INFO=<ID=PHYv,Number=1,Type=String,Description=\"phyloP100way_vertebrate\">",
        "##INFO=<ID=SIPHY,Number=1,Type=String,Description=\"SiPhy_29way_logOdds\">",
        "##INFO=<ID=GSD,Number=1,Type=String,Description=\"genomicSuperDups\">",
        "##INFO=<ID=CLIN,Number=1,Type=String,Description=\"clinvar_20140929\">",
        "##INFO=<ID=GWAS,Number=1,Type=String,Description=\"gwasCatalog\">" };
    
    // Buffered Reader para el fichero de entrada (en formato .vcf) obtenido de CombinatorVcf:
    public static BufferedReader vcf_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero .vcf:
    public static String vcf_line;
    
    // Buffered Reader para el fichero de entrada obtenido de ANNOVAR:
    public static BufferedReader annovar_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero ANNOVAR:
    public static String annovar_line;
    
    
    public CombinatorANNOVAR(){
        
    }
    
    void start() throws FileNotFoundException, IOException{
        // Fichero de entrada obtenido de CombinatorVcf (en formato .vcf):
        File vcf_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084.vcf");
        // Fichero de salida del anotador SIFT en formato .txt:
        File annovar_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084_ANNOVAR.csv");
        
        // Fichero de salida (en formato .vcf) en el que tendremos las líneas del fichero obtenido de CombinatorVcf con 
        // la adición de la información de interés del anotador SIFT como subcampos del campo INFO. El fichero también 
        // tendrá sus correspondientes líneas de cabecera:
        String vcf_name = vcf_file.getName();
        File output_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/add_ANNOVAR_" + vcf_name);
        
        // Función que añade la información obtenida de ANNOVAR al fichero .vcf obtenido de CombinatorVcf:
        addAnnovarToVcf (vcf_file, annovar_file, output_file);
        
    }
    
    /**
     * ----- Añadido el 18/12/2014 -----
     * Función que se utiliza para generar un fichero de salida en el que se ha añadido la información de interés obtenida
     * del anotador ANNOVAR al fichero .vcf que tenemos como resultado de CombinatorVcf.
     * @param vcf_file : Fichero de entrada en formato .vcf obtenido de CombinatorVcf.
     * @param annovar_file : Fichero de entrada con la información del anotador ANNOVAR. 
     * @param output_file : Fichero de salida en el que aparecerá combinada la información de los ficheros de entrada.
     * @throws IOException
     * @throws FileNotFoundException 
     */
    private void addAnnovarToVcf(File vcf_file, File annovar_file, File output_file) throws IOException, FileNotFoundException{
        // Se lee la primera línea del fichero de entrada .vcf obtenido de CombinatorVcf:
        vcf_br = new BufferedReader(new FileReader(vcf_file));
        vcf_line = vcf_br.readLine();
        
        // Se lee la primera línea del fichero de entrada obtenido de ANNOVAR (línea de cabecera con el nombre de los campos):
        annovar_br = new BufferedReader(new FileReader(annovar_file));
        annovar_line = annovar_br.readLine();
        // Vector en el que tendremos todas las cabeceras de los campos de ANNOVAR:
        String[] annovar_headers = annovar_line.split(",");
        // Se lee la primera línea con datos del fichero de ANNOVAR:
        annovar_line = annovar_br.readLine();
        
        // Fichero de salida en el que tendremos las líneas del fichero .vcf con la adición de los campos de SIFT como 
        // subcampos del campo INFO:
        FileWriter output_wr = new FileWriter(output_file);
        PrintWriter print_out = new PrintWriter(output_wr);
        
        // Se copian en el fichero de salida las líneas de cabecera del fichero obtenido de CombinatorVcf y se añaden las 
        // líneas de cabecera correspondientes a los campos de ANNOVAR (al acabar esta función nos encontraremos situados en 
        // la primera línea con datos del fichero .vcf obtenido de CombinatorVcf):
        generateOutputHeader(print_out);
        
        // Recorrido del fichero de entrada .vcf:
        while (vcf_line != null){
            // En un vector almacenamos los campos del fichero .vcf de la línea leída:
            String[] vcf_fields = vcf_line.split("\t");
            // Recorrido del fichero de entrada ANNOVAR:
            while (annovar_line != null){
                // Filtramos la línea leída por comas",", ya que en un principio es el símbolo que separa los campos. Pero hemos
                // de tener en cuenta que hay subcampos de ANNOVAR que también están separados por coma ",", por lo que deberemos
                // hacer un tratamiento del vector "annovar_filter" para obtener los campos correctamente:
                String[] annovar_filter = annovar_line.split(",");
                
                // Tratamieto de los campos de ANNOVAR para obtener los valores de los campos correctamente:
                // Índice para acceder al vector "annovar_filter":
                int annovar_filter_index = 0;
                // vector donde tendremos los valores correctos para los campos obtenidos de ANNOVAR:
                String[] annovar_fields = new String[annovar_headers.length];
                // Índice para acceder al vector "annovar_fields" donde tendremos los valores correctos para los campos obtenidos
                // de ANNOVAR:
                int annovar_fields_index = 0;
                // Recorremos el vector que contiene el filtrado que hemos hecho al fichero ANNOVAR:
                while (annovar_filter_index < annovar_filter.length){
                        // Si es un campo cuyo valor está entre comillas, se almacena en el vector de campos del fichero ANNOVAR:
                        if ((annovar_filter[annovar_filter_index].startsWith("\"")) && (annovar_filter[annovar_filter_index].endsWith("\""))){
                            annovar_fields[annovar_fields_index] = annovar_filter[annovar_filter_index];
                            annovar_fields_index++;
                            annovar_filter_index++;
                        }
                        // Si es un campo cuyo valor no está entre comillas, se almacena en el vector de campos del fichero ANNOVAR:
                        else if (!(annovar_filter[annovar_filter_index].startsWith("\"")) && !(annovar_filter[annovar_filter_index].endsWith("\""))){
                            annovar_fields[annovar_fields_index] = annovar_filter[annovar_filter_index];
                            annovar_fields_index++;
                            annovar_filter_index++;
                        }
                        // Si es un valor que empieza por comillas pero no acaba con este símbolo, significa que el valor se ha filtrado de forma
                        // inadecuada y que faltan trozos correspndientes al valor de ese campo, por lo que se van concatenando los siguientes 
                        // valores del vector "annovar_filter" hasta encontrar uno que acabe con comillas:
                        else if ((annovar_filter[annovar_filter_index].startsWith("\"")) && !(annovar_filter[annovar_filter_index].endsWith("\""))){
                            // Los valores se van concatenando en un string para posteriormente almacenarla en el vector de campos de ANNOVAR:
                            String annovar_field_value = annovar_filter[annovar_filter_index];
                            annovar_filter_index++;
                            while (!(annovar_filter[annovar_filter_index].endsWith("\""))){
                                annovar_field_value += "," + annovar_filter[annovar_filter_index];
                                annovar_filter_index++;
                            }
                            annovar_field_value += "," + annovar_filter[annovar_filter_index];
                            annovar_fields[annovar_fields_index] = annovar_field_value;
                            annovar_fields_index++;
                            annovar_filter_index++;
                        }
                }
                
                // Comprobar que el cromosoma y la posición de .vcf y ANNOVAR sean los mismos:
                // El cromosoma y la posición en el fichero ANNOVAR los tenemos en los dos primeros campos (Chr y Start).
                // Caso 1: Si el cromosoma (CHROM) de ambos ficheros coincide, se comprobará la posición (POS) dentro del cromosoma.
                if (CombinatorVcf.posOfChrom(vcf_fields[0]) == CombinatorVcf.posOfChrom(annovar_fields[0])){
                    // Caso 1.1: Si la posición (POS) del fichero ANNOVAR es mayor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero .vcf (es decir, se lee la siguiente línea del fichero .vcf).
                    if ((Integer.parseInt(annovar_fields[1])) > (Integer.parseInt(vcf_fields[1]))){  
                        break;
                    }                  
                    // Caso 1.2: Si la posición (POS) del fichero ANNOVAR es menor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero ANNOVAR (es decir, se lee la siguiente línea del fichero ANNOVAR).
                    else if ((Integer.parseInt(annovar_fields[1])) < (Integer.parseInt(vcf_fields[1]))){                        
                        annovar_line = annovar_br.readLine();
                    }
                    // Caso 1.3: Si las posiciones (POS) coinciden, se combina la información de esas líneas (se añade al campo INFO del
                    // fichero .vcf de salida: el campo INFO del fichero .vcf de entrada y los campos de interés de ANNOVAR).
                    else{
                        // Se genera el map con los subcampos del campo INFO del fichero .vcf y los campos de interés del fichero ANNOVAR:
                        generateAnnovarVcfMap (vcf_fields, annovar_fields, annovar_headers);
                                                
                        // Se genera la línea que se escribirá en el fichero de salida:
                        String output_line = vcf_fields[0];
                        // Agregamos los campos principales del fichero .vcf (todos menos INFO):
                        for (int i = 1; i < 7; i++){
                            output_line += "\t" + vcf_fields[i];
                        }
                        // Agregamos a la línea el nuevo campo INFO que contiene los campos del .vcf y del ANNOVAR:
                        output_line += "\t" + CombinatorAnnotator.generateOutputInfoField ();
                        
                        // Se escribe la línea generada en el fichero de salida:
                        print_out.println(output_line);
                        
                        // Vaciamos el map para utilizarlo en la siguiente iteración: 
                        CombinatorAnnotator.info_fields_map.clear();
                        
                        // Se lee la siguiente línea en el fichero ANNOVAR (se avanza a la siguiente posición (POS)):
                        annovar_line = annovar_br.readLine();
                        break;
                    }
                }
                // Caso 2: Si el cromosoma (CHROM) es diferente en ambos ficheros.
                else{  
                    // Caso 2.1: Si el cromosoma (CHROM) del fichero .vcf es menor que el del fichero ANNOVAR, avanzamos en el fichero .vcf 
                    // (se lee la siguiente línea del fichero .vcf).
                    if (CombinatorVcf.posOfChrom(vcf_fields[0]) < CombinatorVcf.posOfChrom(annovar_fields[0])){
                        break;
                    }
                    // Caso 2.2: Si el cromosoma (CHROM) del fichero .vcf es mayor que el del fichero ANNOVAR, avanzamos en el fichero ANNOVAR 
                    // (se lee la siguiente línea del fichero ANNOVAR).
                    else if (CombinatorVcf.posOfChrom(vcf_fields[0]) > CombinatorVcf.posOfChrom(annovar_fields[0])){
                        annovar_line = annovar_br.readLine();
                    } 
                }
            }
            vcf_line = vcf_br.readLine();
        }
        
        // Se cierran los dos ficheros de entrada y el fichero de salida:
        vcf_br.close();
        annovar_br.close();
        output_wr.close();
          
    }
    
    /**
     * ----- Añadido el 18/12/2014 -----
     * Función que se utiliza para copiar en el fichero de salida las cabeceras del fichero .vcf y además se añaden las
     * cabeceras para los campos obtenidos del fichero ANNOVAR.
     * @param print_out
     * @throws IOException 
     */
    private void generateOutputHeader (PrintWriter print_out) throws IOException {
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
                        print_out.flush();
                        vcf_line = vcf_br.readLine();
                    }
                    // Añadimos debajo de las líneas de cabecera del campo INFO obtenidas del .vcf, las líneas de cabecera 
                    // del campo INFO para la información de ANNOVAR:
                    for (int i = 0; i < annovar_header_lines.length; i++){
                        print_out.println(annovar_header_lines[i]);
                        print_out.flush();
                    }
                }
                // Copiamos el resto de líneas de cabecera del fichero .vcf de entrada:
                else {
                    print_out.println(vcf_line);
                    print_out.flush();
                    vcf_line = vcf_br.readLine();
                }
            }
            break;
        }  
    }
    
    /**
     * ----- Añadido el 18/12/2014 -----
     * Función que nos indica la posición de un campo del fichero ANNOVAR en el vector de string que representa los campos que 
     * nos interesan de este tipo de ficheros.
     * @param annovar_field_name : Nombre del campo del fichero ANNOVAR que queremos comprobar si se encuentra en el listado
     *                             de campos definidos en el vector "annovar_filter_long_name".
     * @return : Devuelve la posición del vector en la que se encuentra el campo y si no lo ha encontrado devuelve -1.
     */
    private int indexOfAnnovarField (String annovar_field_name) {
        for (int i = 0; i < annovar_filter_long_name.length; i++) {
            if (annovar_filter_long_name[i].equals(annovar_field_name)) {
                return i;
            }
        }
        return -1;
    } 
    
    /**
     * ----- Añadido el 18/12/2014 -----
     * Función que se encargará de generar un map con todos los campos de interés obtenidos del fichero .vcf y del fichero 
     * ANNOVAR, donde la clave del map será el acrónimo o nombre corto que representa el campo y el valor será el valor del 
     * propio campo.
     * @param vcf_fields : Campos obtenidos del fichero .vcf de entrada.
     * @param annovar_filter : Campos obtenidos del fichero ANNOVAR.
     * @param annovar_headers : Nombre de las cabeceras del fichero ANNOVAR.
     */
    private void generateAnnovarVcfMap(String[] vcf_fields, String[] annovar_filter, String[] annovar_headers) {
        // Nos quedamos con los subcampos del campo INFO (campo 8) de los campos pasados por parámetro del fichero .vcf:
        String[] vcf_info = vcf_fields[7].split(";");
                        
        // Map en el que tendremos almacenados los campos que necesitemos de los ficheros .vcf y ANNOVAR (los campos se 
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
        
        // PASO 2: Almacenamos en el map los campos de interés del fichero ANNOVAR.
        for (int i = 0; i < annovar_headers.length; i++){
            // Si el campo se encuentra dentro de la lista de campos que nos interesan:
            if (indexOfAnnovarField(annovar_headers[i]) >= 0){
                // Comprobamos si existe un valor para ese campo y así evitamos insertar en el map campos que tengan 
                // valores perdidos (los valores perdidos en el fichero ANNOVAR se representan por el símbolo "-" o " "):
                if (!((annovar_filter[i].equals("")) || (annovar_filter[i].equals(".")) || (annovar_filter[i].equals("\"\"")))){
                    if (annovar_filter[i].startsWith("\"")){
                        String annovar_value = annovar_filter[i].substring(1, (annovar_filter[i].length() -1));
                        CombinatorAnnotator.info_fields_map.put(annovar_filter_short_name[indexOfAnnovarField(annovar_headers[i])], annovar_value);
                    }
                    else{
                        CombinatorAnnotator.info_fields_map.put(annovar_filter_short_name[indexOfAnnovarField(annovar_headers[i])], annovar_filter[i]);
                    }
                }
            } 
        }   
    }
    
}
