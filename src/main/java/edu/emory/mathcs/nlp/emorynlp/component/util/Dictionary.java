package edu.emory.mathcs.nlp.emorynlp.component.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Song on 11/4/2015.
 */
public class Dictionary {
    private Map<String, Set<String>> entity_classes;
    public static Dictionary instance;


    public static Dictionary getInstance(){
        if(instance == null){
            instance = new Dictionary();
        }
        return instance;
    }

    public Dictionary(){
        entity_classes = new HashMap<>();
        try{
            System.out.println("Initialize dictionary...");
            readDictionary();
        } catch (Exception e){
            System.err.println("read dictionary failed...");
        }
    }

    public void readDictionary() throws Exception{
        String Filename = "D://data//nlpdata//prefixtree_dbpedia_undigitalized.txt";
        FileInputStream fstream = new FileInputStream(Filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line, entity, tag;
        String[] arr;
        //Read File Line By Line
        while ((line = br.readLine()) != null)   {
            // Print the content on the console
            arr = line.split("\t");
            tag = arr[arr.length - 1];
            entity = line.replace(tag, "").trim();
            entity_classes.computeIfAbsent(entity, v -> new HashSet<>()).add(tag);
        }
        //Close the input stream
        br.close();
    }

    public String getAmbiguityClass(String word){
        return entity_classes.containsKey(word) ? entity_classes.get(word).toString() : null;
    }

    public static void main(String[] args) throws Exception{
        Dictionary dictionary = new Dictionary();
        dictionary.readDictionary();
    }
}
