package edu.emory.mathcs.nlp.emorynlp.component.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Song on 10/30/2015.
 */
public class Ontology {

    public String parsePhrase(String url){
        String[] arr = url.split("/");
        String str = arr[arr.length - 1];
        return str.substring(0, str.length() - 1);
    }
    public void readOntology(String Filename) throws Exception{
        Set<String> set = new HashSet<>();
        FileInputStream fstream = new FileInputStream(Filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line;
        String[] arr;
        //Read File Line By Line
        while ((line = br.readLine()) != null)   {
            // Print the content on the console
            if(!line.contains("dbpedia.org/ontology"))  continue;
            arr = line.split(" ");
            set.add(parsePhrase(arr[0]));
//            System.out.println(parsePhrase(arr[0]) + "     " + parsePhrase(arr[2]));
        }
        System.out.println(set.size());
        //Close the input stream
        br.close();

    }

    public static void main(String[] args) throws Exception{
        Ontology ontology = new Ontology();
        ontology.readOntology("D://data//nlpdata//instance-types_en.nt");
    }
}
