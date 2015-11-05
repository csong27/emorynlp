package edu.emory.mathcs.nlp.emorynlp.component.util;

import edu.emory.mathcs.nlp.common.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Song on 10/30/2015.
 */
public class Ontology {

    private Map<String, Set<String>> entity_classes;
    public static Ontology instance;

    public Ontology(){
        entity_classes = new HashMap<>();
    }

    public static Ontology getInstance(){
        if(instance == null){
            instance = new Ontology();
        }
        return instance;
    }

    public String parseClass(String url){
        String[] arr = url.split("/");
        String str = arr[arr.length - 1];
        return str.substring(0, str.length() - 1);
    }

    public String parseEntity(String url){
        String[] arr = url.split("/");
        String last, str = arr[arr.length - 1];
        str = str.replace("_", " ");
        if(!str.startsWith("(") && str.contains("("))
            str = str.substring(0, str.indexOf('(') - 1);
        if(!str.startsWith(">") && str.contains(">"))
            str = str.substring(0, str.indexOf('>') - 1);
        str = str.trim();
        arr = str.split("  ");
        last = arr[arr.length - 1];
        if(StringUtils.containsDigitOnly(last) && last.length() < 2)
            return null;
        else
            return str;
    }

    public void readOntology(String Filename) throws Exception{
        FileInputStream fstream = new FileInputStream(Filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line, entity, classTag;
        String[] arr;
        //Read File Line By Line
        while ((line = br.readLine()) != null)   {
            // Print the content on the console
            if(!line.contains("dbpedia.org/ontology"))  continue;
            arr = line.split(" ");
            entity = parseEntity(arr[0]);
            if(entity != null) {
                classTag = parseClass(arr[2]);
                entity_classes.computeIfAbsent(entity, v -> new HashSet<>()).add(classTag);
            }
        }
        for(String key: entity_classes.keySet()){
            System.out.println(key + " : " + entity_classes.get(key));
        }
        //Close the input stream
        br.close();

    }

    public String getClasses(String entity){
        return entity_classes.containsKey(entity) ? entity_classes.get(entity).toString(): null;
    }

    public static void main(String[] args) throws Exception{
        Ontology.getInstance().readOntology("D://data//nlpdata//instance-types_en.nt");
    }
}
