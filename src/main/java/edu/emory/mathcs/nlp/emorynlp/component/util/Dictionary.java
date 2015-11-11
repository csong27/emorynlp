package edu.emory.mathcs.nlp.emorynlp.component.util;

import edu.emory.mathcs.nlp.common.util.XMLUtils;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
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
    private static String dict_path;

    public static Dictionary instance;


    public static Dictionary getInstance(){
        if(instance == null){
            instance = new Dictionary();
        }
        return instance;
    }

    public Dictionary(){
        entity_classes = new HashMap<>();
    }

    public void read(){
        if(dict_path == null)   return;
        try {
            System.out.println("Loading gazetteer dictionary...");
            FileInputStream fstream = new FileInputStream(dict_path);
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
        } catch (IOException e){
            System.err.println("Load gazetteer failed...");
        }

    }

    public String getAmbiguityClass(String word){
        return entity_classes.containsKey(word) ? entity_classes.get(word).toString() : null;
    }

    static public void initClustering(Element doc)
    {
        Element eGlobal = XMLUtils.getFirstElementByTagName(doc, "global");
        if(eGlobal == null) return;
        String path  = XMLUtils.getTextContentFromFirstElementByTagName(eGlobal, "dictionary");
        if(path == null)  return;
        dict_path = path;
        Dictionary.getInstance().read();
    }

}
