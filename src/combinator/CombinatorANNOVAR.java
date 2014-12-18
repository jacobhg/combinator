package combinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    public final String [] annovar_fields_long_name = {
        "Func.refgene", "Gene.refgene", "GeneDetail.refgene", "ExonicFunc.refgene", "AAChange.refgene", "phastConsElements46way",
        "esp6500si_all", "1000g2014oct_all", "snp138", "SIFT_score", "SIFT_pred", "Polyphen2_HDIV_score", "Polyphen2_HDIV_pred",
        "Polyphen2_HVAR_score", "Polyphen2_HVAR_pred", "LRT_score", "LRT_pred", "MutationTaster_score", "MutationTaster_pred",
        "MutationAssessor_score", "MutationAssessor_pred", "FATHMM_score", "FATHMM_pred", "RadialSVM_score", "RadialSVM_pred",
        "LR_score", "LR_pred", "VEST3_score", "CACADD_raw", "CADD_phred", "GERP++_RS", "phyloP46way_placental", 
        "phyloP100way_vertebrate", "SiPhy_29way_logOdds", "genomicSuperDups", "clinvar_20140929", "gwasCatalog" };
    
    // Vector que representa las siglas o el nombre corto que se ha establecido para los diferentes campos que podemos encontrarnos 
    // en el fichero obtenido de ANNOVAR:
    public final String [] annovar_fields_short_name = {
        
        };
    
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
        File sift_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/combinator_ref_niv_084_ANNOVAR.csv");
        
        // Fichero de salida (en formato .vcf) en el que tendremos las líneas del fichero obtenido de CombinatorVcf con 
        // la adición de la información de interés del anotador SIFT como subcampos del campo INFO. El fichero también 
        // tendrá sus correspondientes líneas de cabecera:
        String vcf_name = vcf_file.getName();
        File output_file = new File("/home/uai02/Investigacion_Jacob/ficheros_vcf_mist/add_SIFT_" + vcf_name);
        
        // Función que añade la información obtenida de ANNOVAR al fichero .vcf obtenido de CombinatorVcf:
        //addAnnovarToVcf (vcf_file, sift_file, output_file);
        
    }
    
    
    
    
}
