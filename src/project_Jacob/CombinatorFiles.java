package project_Jacob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ----- Añadido el 10/12/2014 -----
 * Clase en la que tendremos las funciones necesarias para que dados dos ficheros de entrada, uno en formato .vcf obtenido de *
 * CombinatorVcf y otro obtenido del anotador VEP, nos devuelva un fichero de salida en el que tendremos las líneas del       *
 * fichero .vcf pero en el campo INFO se habrán añadido los campos de interés del fichero VEP; También se añaden las líneas   *
 * de cabecera correspondientes a los campos que se encuentran en el fichero VEP.                                             *
 *                                                                                                                            *
 * @author Jacob Henríquez
 */
public class CombinatorFiles {
    //----- Añadido el 11/12/2014 -----
    // Vector de string que representa los distintos campos que nos podemos encontrar en el fichero obtenido de VEP:
    public final String [] vep_fields = {
        "Gene", "Feature", "Feature_type", "Consequence", "cDNA_position", "CDS_position", "Protein_position", "Amino_acids", "Codons", 
        "DISTANCE", "STRAND", "SYMBOL", "SYMBOL_SOURCE", "ENSP", "SWISSPROT", "TREMBL", "UNIPARC", "HGVSc", "HGVSp", "SIFT", "PolyPhen",
        "MOTIF_NAME", "MOTIF_POS", "HIGH_INF_POS", "MOTIF_SCORE_CHANGE", "CELL_TYPE", "CANONICAL", "CCDS", "INTRON", "EXON", "DOMAINS",
        "IND", "ZYG", "SV", "FREQS", "GMAF", "AFR_MAF", "AMR_MAF", "ASN_MAF", "EUR_MAF", "AA_MAF", "EA_MAF", "CLIN_SIG", "BIOTYPE", "TSL",
        "PUBMED", "SOMATIC"};
    
    // Vector que representa las siglas que se han establecido para los diferentes campos que podemos encontrarnos en el 
    // fichero obtenido de VEP:
    public final String [] vep_fields_tag = {
        "GENE", "FEAT", "TYPE", "CONS", "CDNA", "CDS", "PROT", "AMINO", "COD", "DIST", "STR", "GNAME", "SRC", "ENSP", "SWPR", "TRBL", "UNI",
        "HGVSc", "HGVSp", "SIFT", "POLY", "MTFN", "MTFP", "HIP", "MSC", "CLLS", "CANON", "CCDS", "INTR", "EXON", "DOM", "IND", "ZYG", "SV",
        "FRQ", "GMAF", "AFR_F", "AMR_F", "ASN_F", "EUR_F", "AA_F", "EA_F", "CLIN", "BIO", "TSL", "PUBM", "SOMA"};
    
    // Vector en el que tenemos las líneas de cabecera correspondientes a los campos del fichero obtenido de VEP y que se
    // incluirán como líneas de cabecera en el fichero de salida:
    public final String[] vep_info = {
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
    
    
    public CombinatorFiles(){
        
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
        addVep (vcf_file, vep_file, output_file);
        
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
    private void addVep(File vcf_file, File vep_file, File output_file) throws IOException, FileNotFoundException{
        // Se lee la primera línea del fichero de entrada .vcf obtenido de CombinatorVcf:
        vcf_br = new BufferedReader(new FileReader(vcf_file));
        vcf_line = vcf_br.readLine();
        
        // Se lee la primera línea del fichero de entrada obtenido de VEP (línea de cabecera):
        vep_br = new BufferedReader(new FileReader(vep_file));
        vep_line = vep_br.readLine();
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
        
        
        
        // FASE PRINCIPAL: Añadir la información de VEP al campo INFO del .vcf.
        
        
        

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
                    for (int i = 0; i < vep_info.length; i++){
                        print_out.println(vep_info[i]);
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
    
}
