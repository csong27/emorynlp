package edu.emory.mathcs.nlp.emorynlp.component.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Song on 10/31/2015.
 */
public class PredictionHistory {
    public Map<String, Map<String, Counter>> label_counter_map;

    public static PredictionHistory instance;

    public PredictionHistory(){
        label_counter_map = new HashMap<>();
    }

    public static PredictionHistory getInstance(){
        if(instance == null){
            System.out.println("Initialize Prediction History...");
            instance = new PredictionHistory();
        }
        return instance;
    }

    public void addLabelCount(String word, String label){
        if(label_counter_map.containsKey(word)){
            Map<String, Counter> labelCounter = label_counter_map.get(word);
            if(labelCounter.containsKey(label))
                labelCounter.get(label).increment();
            else
                labelCounter.put(label, new Counter());
        } else{
            Map<String, Counter> labelCounter = new HashMap<>();
            labelCounter.put(label, new Counter());
            label_counter_map.put(word, labelCounter);
        }
    }

    public void resetCount(){
        for(String key: label_counter_map.keySet())
            label_counter_map.get(key).forEach((k,v) -> v.resetCount());
    }


    public String getCountInfo(Map<String, Counter> labelCounter){
        String info = "";
        int sum = 0;
        for(Counter c: labelCounter.values()) sum += c.tally();
        for(String key: labelCounter.keySet()){
            info += key + ":" + labelCounter.get(key).tally() + "/" + sum + "_";
        }
        return info.substring(0, info.length() - 1);
    }


    public String getFeature(String word){
        if(label_counter_map.containsKey(word))
            return getCountInfo(label_counter_map.get(word));
        else
            return null;
    }


    public static void main(String[] args){
        PredictionHistory ph = new PredictionHistory();
        ph.addLabelCount("a", "b");
        ph.addLabelCount("a", "c");
        System.out.println(ph.getFeature("a"));
        ph.resetCount();
        System.out.println(ph.getFeature("a"));
    }
}
