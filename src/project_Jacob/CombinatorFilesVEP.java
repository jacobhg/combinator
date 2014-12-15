package project_Jacob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * ----- Añadido el 10/12/2014 -----
 * Clase en la que tendremos las funciones necesarias para que dados dos ficheros de entrada, uno en formato .vcf obtenido de *
 * CombinatorVcf y otro obtenido del anotador VEP, nos devuelva un fichero de salida en el que tendremos las líneas del       *
 * fichero .vcf pero en el campo INFO se habrán añadido los campos de interés del fichero VEP; También se añaden las líneas   *
 * de cabecera correspondientes a los campos que se encuentran en el fichero VEP.                                             *
 *                                                                                                                            *
 * @author Jacob Henríquez
 */
public class CombinatorFilesVEP {
    //----- Añadido el 11/12/2014 -----
    // Vector de string que representa los distintos campos que nos podemos encontrar en el fichero obtenido de VEP:
    public final String [] vep_fields_long_name = {
        "Gene", "Feature", "Feature_type", "Consequence", "cDNA_position", "CDS_position", "Protein_position", "Amino_acids", "Codons", 
        "DISTANCE", "STRAND", "SYMBOL", "SYMBOL_SOURCE", "ENSP", "SWISSPROT", "TREMBL", "UNIPARC", "HGVSc", "HGVSp", "SIFT", "PolyPhen",
        "MOTIF_NAME", "MOTIF_POS", "HIGH_INF_POS", "MOTIF_SCORE_CHANGE", "CELL_TYPE", "CANONICAL", "CCDS", "INTRON", "EXON", "DOMAINS",
        "IND", "ZYG", "SV", "FREQS", "GMAF", "AFR_MAF", "AMR_MAF", "ASN_MAF", "EUR_MAF", "AA_MAF", "EA_MAF", "CLIN_SIG", "BIOTYPE", "TSL",
        "PUBMED", "SOMATIC"};
    
    // Vector que representa las siglas que se han establecido para los diferentes campos que podemos encontrarnos en el 
    // fichero obtenido de VEP:
    public final String [] vep_fields_short_name = {
        "GENE", "FEAT", "TYPE", "CONS", "CDNA", "CDS", "PROT", "AMINO", "COD", "DIST", "STR", "GNAME", "SRC", "ENSP", "SWPR", "TRBL", "UNI",
        "HGVSc", "HGVSp", "SIFT", "POLY", "MTFN", "MTFP", "HIP", "MSC", "CLLS", "CANON", "CCDS", "INTR", "EXON", "DOM", "IND", "ZYG", "SV",
        "FRQ", "GMAF", "AFR_F", "AMR_F", "ASN_F", "EUR_F", "AA_F", "EA_F", "CLIN", "BIO", "TSL", "PUBM", "SOMA"};
    
    // Vector en el que tenemos las líneas de cabecera correspondientes a los campos del fichero obtenido de VEP y que se
    // incluirán como líneas de cabecera en el fichero de salida:
    public final String[] vep_info_lines = {
        "##INFO=<ID=GENE,Number=1,Type=String,Description=\"Ensemble gene ID\">",
        "##INFO=<ID=FEAT,Number=1,Type=String,Description=\"Ensemble feature ID\">",
        "##INFO=<ID=TYPE,Number=1,Type=String,Description=\"Type of feature (Transcript, RegulatoryFeature, MotifFeature)\">",
        "##INFO=<ID=CONS,Number=1,Type=String,Description=\"Consequence type\">",
        "##INFO=<ID=CDNA,Number=1,Type=Integer,Description=\"Relative position of base pair in cDNA sequence\">",
        "##INFO=<ID=CDS,Number=1,Type=Integer,Description=\"Relative position of base pair in coding sequence\">",
        "##INFO=<ID=PROT,Number=1,Type=Integer,Description=\"Relative position of amino acid in protein\">",
        "##INFO=<ID=AMINO,Number=1,Type=String,Description=\"Amino acid change. Only given if the variation affects the protein-coding sequence\">",
        "##INFO=<ID=COD,Number=1,Type=String,Description=\"The alternative codons with the variant base in upper case\">",
        "##INFO=<ID=DIST,Number=1,Type=String,Description=\"Shortest distance from variant to transcript\">",
        "##INFO=<ID=STR,Number=1,Type=String,Description=\"The DNA strand (1 or -1) on which the transcript/feature lies\">",
        "##INFO=<ID=GNAME,Number=1,Type=String,Description=\"Gene symbol or name\">",
        "##INFO=<ID=SRC,Number=1,Type=String,Description=\"The source of the gene symbol\">",
        "##INFO=<ID=ENSP,Number=1,Type=String,Description=\"Ensembl protein identifier of the affected transcript\">",
        "##INFO=<ID=SWPR,Number=1,Type=String,Description=\"UniProtKB/Swiss-Prot identifier of protein product\">",
        "##INFO=<ID=TRBL,Number=1,Type=String,Description=\"UniProtKB/TrEMBL identifier of protein product\">",
        "##INFO=<ID=UNI,Number=1,Type=String,Description=\"UniParc identifier of protein product\">",
        "##INFO=<ID=HGVSc,Number=1,Type=String,Description=\"HGVS coding sequence name\">",
        "##INFO=<ID=HGVSp,Number=1,Type=String,Description=\"HGVS protein sequence name\">",
        "##INFO=<ID=SIFT,Number=1,Type=String,Description=\"SIFT prediction and/or score, with both given as prediction(score)\">",
        "##INFO=<ID=POLY,Number=1,Type=String,Description=\"PolyPhen prediction and/or score\">",
        "##INFO=<ID=MTFN,Number=1,Type=String,Description=\"source and identifier of a transcription factor binding profile aligned at this position\">",
        "##INFO=<ID=MTFP,Number=1,Type=String,Description=\"relative position of the variation in the aligned TFBP\">",
        "##INFO=<ID=HIP,Number=0,Type=Flag,Description=\"a flag indicating if the variant falls in a high information position of a transcription factor binding profile (TFBP)\">",
        "##INFO=<ID=MSC,Number=1,Type=String,Description=\"difference in motif score of the reference and variant sequences for the TFBP\">",
        "##INFO=<ID=CLLS,Number=1,Type=String,Description=\"List of cell types and classifications for regulatory feature\">",
        "##INFO=<ID=CANON,Number=0,Type=Flag,Description=\"Transcript is denoted as the canonical transcript for this gene\">",
        "##INFO=<ID=CCDS,Number=1,Type=String,Description=\"CCDS identifer for this transcript, where applicable\">",
        "##INFO=<ID=INTR,Number=1,Type=String,Description=\"Intron number (out of total number)\">",
        "##INFO=<ID=EXON,Number=1,Type=String,Description=\"Exon number (out of total number)\">",
        "##INFO=<ID=DOM,Number=1,Type=String,Description=\"the source and identifer of any overlapping protein domains\">",
        "##INFO=<ID=IND,Number=1,Type=String,Description=\"Individual name\">",
        "##INFO=<ID=ZYG,Number=1,Type=String,Description=\"Zygosity of individual genotype at this locus\">",
        "##INFO=<ID=SV,Number=1,Type=String,Description=\"IDs of overlapping structural variants\">",
        "##INFO=<ID=FRQ,Number=1,Type=String,Description=\"Frequencies of overlapping variants used in filtering\">",
        "##INFO=<ID=GMAF,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1\">",
        "##INFO=<ID=AFR_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined African population\">",
        "##INFO=<ID=AMR_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined American population\">",
        "##INFO=<ID=ASN_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined Asian population\">",
        "##INFO=<ID=EUR_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variation in 1000 Genomes Phase 1 combined European population\">",
        "##INFO=<ID=AA_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variant in NHLBI-ESP African American population\">",
        "##INFO=<ID=EA_F,Number=1,Type=String,Description=\"Minor allele and frequency of existing variant in NHLBI-ESP European American population\">",
        "##INFO=<ID=CLIN,Number=1,Type=String,Description=\"Clinical significance of variant from dbSNP\">",
        "##INFO=<ID=BIO,Number=1,Type=String,Description=\"Biotype of transcript or regulatory feature\">",
        "##INFO=<ID=TSL,Number=1,Type=String,Description=\"Transcript support level\">",
        "##INFO=<ID=PUBM,Number=1,Type=String,Description=\"Pubmed ID(s) of publications that cite existing variant\">",
        "##INFO=<ID=SOMA,Number=1,Type=String,Description=\"Somatic status of existing variation(s)\">" };
    
    // Buffered Reader para el fichero de entrada (en formato .vcf) obtenido de CombinatorVcf:
    public static BufferedReader vcf_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero .vcf:
    public static String vcf_line;
    
    // Buffered Reader para el fichero de entrada obtenido de VEP:
    public static BufferedReader vep_br;
    // Variable string que contendrá la línea que se vaya leyendo del fichero VEP:
    public static String vep_line;
    
    //----- Añadido el 12/12/2014 -----
    // Map en el que se guardarán todos los subcampos que se van a añadir al campo INFO del fichero .vcf de salida:
    public static Map<String, String> info_fields_map;
    
    public CombinatorFilesVEP(){
        
    }
    
    void start() throws FileNotFoundException, IOException{
        // Fichero de entrada obtenido de CombinatorVcf (en formato .vcf):
        File vcf_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084.vcf");
        // Fichero de salida del anotador VEP en formato .txt:
        File vep_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084_VEP.txt");

        // Fichero de salida (en formato .vcf) en el que tendremos las líneas del fichero obtenido de CombinatorVcf con 
        // la adición de la información de interés del anotador VEP como subcampos del campo INFO. El fichero también 
        // tendrá sus correspondientes líneas de cabecera:
        String vcf_name = vcf_file.getName();
        File output_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/add_VEP_" + vcf_name);
        
        // Función que añade la información obtenida de VEP al fichero .vcf obtenido de CombinatorVcf:
        addVepInfoToVcf (vcf_file, vep_file, output_file);
        
    }
    
    /**
     * ----- Añadido el 11/12/2014 -----
     * Función que se utiliza generar un fichero de salida en el que se ha añadido la información de interés obtenida
     * del anotador VEP al fichero .vcf que tenemos como resultado de CombinatorVcf.
     * @param vcf_file : Fichero de entrada en formato .vcf obtenido de CombinatorVcf.
     * @param vep_file : Fichero de entrada con la información del anotador VEP. 
     * @param output_file : Fichero de salida en el que aparecerá combinada la información de los ficheros de entrada.
     * @throws IOException
     * @throws FileNotFoundException 
     */
    private void addVepInfoToVcf(File vcf_file, File vep_file, File output_file) throws IOException, FileNotFoundException{
        // Se lee la primera línea del fichero de entrada .vcf obtenido de CombinatorVcf:
        vcf_br = new BufferedReader(new FileReader(vcf_file));
        vcf_line = vcf_br.readLine();
        
        // Se lee la primera línea del fichero de entrada obtenido de VEP (línea de cabecera):
        vep_br = new BufferedReader(new FileReader(vep_file));
        vep_line = vep_br.readLine();
        //----- Añadido el 12/12/2014 -----
        // Vector en el que tendremos todas las cabeceras de los campos de VEP:
        String[] vep_headers = vep_line.split("\t");
        // Se lee la primera línea con datos del fichero de VEP:
        vep_line = vep_br.readLine();
        
        // Fichero de salida en el que se añadirá al campo INFO del fichero .vcf obtenido de CombinatorVcf la información 
        // deseada del fichero obtenido de VEP:
        FileWriter output_wr = new FileWriter(output_file);
        PrintWriter print_out = new PrintWriter(output_wr);
        
        // Se copian en el fichero de salida las líneas de cabecera del fichero obtenido de CombinatorVcf y se añaden las 
        // líneas de cabecera correspondientes a los campos de VEP (al acabar esta función nos encontraremos situados en 
        // la primera línea con datos del fichero .vcf obtenido de CombinatorVcf):
        addVepHeaderToVcf(print_out);
        
        // Recorrido del fichero de entrada .vcf:
        while (vcf_line != null){
            // En un vector almacenamos los campos del fichero .vcf
            String[] vcf_fields = vcf_line.split("\t");
            // Recorrido del fichero de entrada VEP:
            while (vep_line != null){
                // Almacenamos en un vector los campos del fichero VEP:
                String[] vep_fields = vep_line.split("\t");
                // Del campo "Location" (campo 2) del fichero VEP, obtenemos el cromosoma y la posición para poder comparar
                // con el cromosoma y la posición del .vcf:
                String[] vep_chrom_pos = vep_fields[1].split(":");
                // En algunos casos la posición viene indicada como un intervalo de posiciones del tipo "1394663-1394664", por
                // lo que tendremos que dividir esa línea para quedarnos con la posición que nos interesa.
                String[] vep_pos = vep_chrom_pos[1].split("-");
                
                // Comprobar que el cromosoma y la posición de .vcf y VEP sean la misma:
                // Caso 1: Si el cromosoma (CHROM) en ambos ficheros coincide, se comprobará la posición (POS) dentro del cromosoma.
                if (CombinatorVcf.posOfChrom(vcf_fields[0]) == CombinatorVcf.posOfChrom(vep_chrom_pos[0])){ 
                    // Caso 1.1: Si la posición (POS) del fichero VEP es mayor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero .vcf (es decir, se lee la siguiente línea del fichero .vcf).
                    if ((Integer.parseInt(vep_pos[0])) > (Integer.parseInt(vcf_fields[1]))){
                        break;
                    }                  
                    // Caso 1.2: Si la posición (POS) del fichero VEP es menor que la del fichero .vcf, avanzamos una posición en el 
                    // fichero VEP (es decir, se lee la siguiente línea del fichero VEP).
                    else if ((Integer.parseInt(vep_pos[0])) < (Integer.parseInt(vcf_fields[1]))){
                        vep_line = vep_br.readLine();
                    }                     
                    // Caso 1.3: Si las posiciones (POS) coinciden, se combina la información de esas líneas (se añade al campo INFO del
                    // fichero .vcf de salida: el campo INFO del fichero .vcf de entrada y los campos de interés de VEP).
                    else{
                        // Se genera el map con los subcampos del campo INFO del fichero .vcf y los campos de interés del fichero VEP:
                        generateVepVcfMap (vcf_fields, vep_fields, vep_headers);
                                                
                        // Se genera la línea que se escribirá en el fichero de salida:
                        String output_line = vcf_fields[0];
                        // Agregamos los campos principales del fichero .vcf (todos menos INFO):
                        for (int i = 1; i < 7; i++){
                            output_line += "\t" + vcf_fields[i];
                        }
                        // Agregamos a la línea el nuevo campo INFO que contiene los campos del .vcf y del VEP:
                        output_line += "\t" + addToInfoOutput ();
                        
                        // Se escribe la línea generada en el fichero de salida:
                        print_out.println(output_line);
                        
                        // Vaciamos el map para utilizarlo en la siguiente iteración: 
                        info_fields_map.clear();
                        
                        // Se lee la siguiente línea en el fichero VEP (se avanza a la siguiente posición (POS)):
                        vep_line = vep_br.readLine();
                        break;
                    }
                }
                // Caso 2: Si el cromosoma (CHROM) es diferente en ambos ficheros.
                else{  
                    // Caso 2.1: Si el cromosoma (CHROM) del fichero .vcf es menor que el del fichero VEP, avanzamos en el fichero .vcf 
                    // (se lee la siguiente línea del fichero .vcf).
                    if (CombinatorVcf.posOfChrom(vcf_fields[0]) < CombinatorVcf.posOfChrom(vep_chrom_pos[0])){
                        break;
                    }
                    // Caso 2.2: Si el cromosoma (CHROM) del fichero .vcf es mayor que el del fichero VEP, avanzamos en el fichero a comparar 
                    // (se lee la siguiente línea del fichero VEP).
                    else if (CombinatorVcf.posOfChrom(vcf_fields[0]) > CombinatorVcf.posOfChrom(vep_chrom_pos[0])){
                        vep_line = vep_br.readLine();
                    } 
                }
            }
            vcf_line = vcf_br.readLine();
        }
        
        // Se cierran los dos ficheros de entrada y el fichero de salida:
        vcf_br.close();
        vep_br.close();
        output_wr.close();
        
    }
    
    /**
     * ----- Añadido el 11/12/2014 -----
     * Función que se utiliza para copiar en el fichero de salida las cabeceras del fichero .vcf y además se añaden las
     * cabeceras para los campos obtenidos del fichero VEP.
     * @param print_out
     * @throws IOException 
     */
    private void addVepHeaderToVcf (PrintWriter print_out) throws IOException{
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
                    // Añadimos debajo de las líneas del campo INFO obtenidas del .vcf, las líneas del cabecera del
                    // campo INFO para la información de VEP:
                    for (int i = 0; i < vep_info_lines.length; i++){
                        print_out.println(vep_info_lines[i]);
                    }
                }
                // Copiamos el resto de líneas de cabecera del fichero .vcf:
                else {
                    print_out.println(vcf_line);
                    vcf_line = vcf_br.readLine();
                }
            }
            break;
        }

    }    
    
    /**
     * ----- Añadido el 12/12/2014 -----
     * Función que nos indica la posición de un campo del fichero VEP en el vector de string que representa los campos que 
     * nos interesan de este tipo de ficheros.
     * @param vep_field_name : Nombre del campo del fichero VEP que queremos comprobar si se encuentra en el listado
     *                         de campos definidos en el vector "vep_fields_long_name".
     * @return : Devuelve la posición del vector en la que se encuentra el campo y si no lo ha encontrado devuelve -1.
     */
    public int indexOfVepField (String vep_field_name) {
        for (int i = 0; i < vep_fields_long_name.length; i++) {
            if (vep_fields_long_name[i].equals(vep_field_name)) {
                return i;
            }
        }
        return -1;
    } 
    
    /**
     * ----- Añadido el 12/12/2014 -----
     * Función que se encargará de generar un map con todos los campos de interés obtenidos del fichero .vcf y del fichero 
     * VEP, donde la clave del map será el acrónimo que representa el campo y el valor será el valor del propio campo.
     * @param vcf_fields : Campos obtenidos del fichero .vcf.
     * @param vep_fields : Capos obtenidos del fichero VEP.
     * @param vep_headers : Nombre de las cabeceras del fichero VEP.
     */
    public void generateVepVcfMap (String[] vcf_fields, String[] vep_fields, String[] vep_headers){
        // Nos quedamos con los subcampos del campo INFO (campo 8) de los campos pasados por parámetro del fichero .vcf:
        String[] vcf_info = vcf_fields[7].split(";");
                        
        // Map en el que tendremos almacenados los campos que necesitemos de los ficheros .vcf y VEP:
        info_fields_map = new TreeMap();
        
        // PASO 1: Almacenamos en el map los subcampos del campo INFO del fichero .vcf.
        for (int i = 0; i < vcf_info.length; i++){
            // Controlamos que se añadan correctamente las etiquetas MistZone:
            if (vcf_info[i].startsWith("MistZone")){
                info_fields_map.put(vcf_info[i], vcf_info[i]);
            }
            // Para el resto de los campos:
            else{
                String[] vcf_sub_info = vcf_info[i].split("=");                
                info_fields_map.put(vcf_sub_info[0], vcf_sub_info[1]);
            }
        }
        
        // PASO 2: Almacenamos en el map los campos de interés del fichero VEP.
        for (int i = 0; i < vep_fields.length; i++){
            // Si el campo se encuentra dentro de la lista de campos que nos interesan:
            if (indexOfVepField(vep_headers[i]) >= 0){
                // Comprobamos si existe un valor para ese campo y así evitamos insertar en el map campos que tengan 
                // valores perdidos (los valores perdidos en el fichero VEP se representan por el símbolo "-"):
                if (!(vep_fields[i].equals("-"))){
                    // Comprobamos si es un campo correspondiente a la frecuencia en la población:
                    if (vep_headers[i].contains("MAF")){
                        String[] freq = vep_fields[i].split(":");
                        info_fields_map.put(vep_fields_short_name[indexOfVepField(vep_headers[i])], freq[1]);
                    }
                    else {
                        info_fields_map.put(vep_fields_short_name[indexOfVepField(vep_headers[i])], vep_fields[i]);
                    }    
                }
            } 
        }
    }
    
    /**
     * ----- Añadido el 15/12/2014 -----
     * Función que se utiliza para generar la línea INFO de salida con todos los campos que nos interesan de los ficheros 
     * .vcf y VEP. 
     * @return : Devuelve la línea generada.
     */
    public String addToInfoOutput (){
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
