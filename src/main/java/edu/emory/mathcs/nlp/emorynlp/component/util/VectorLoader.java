package edu.emory.mathcs.nlp.emorynlp.component.util;

import edu.emory.mathcs.nlp.common.util.XMLUtils;
import edu.emory.mathcs.nlp.emorynlp.component.config.ConfigXML;
import org.w3c.dom.Element;
import smile.clustering.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Song on 10/31/2015.
 */
public class VectorLoader implements ConfigXML{
    public static VectorLoader instance;
    private static String train_path;
    private String clustering_algorithm = "GMEANS";
    private Map<String, Integer> word_index;
    private Map<Integer, String> index_word;
    private Map<String, Integer> word_cluster;
    private List<double[]> vectors;

    public VectorLoader(){
        vectors = new ArrayList<>();
        word_index = new HashMap<>();
        index_word = new HashMap<>();
        word_cluster = new HashMap<>();
    }

    static public void initClustering(Element doc)
    {
        Element eGlobal = XMLUtils.getFirstElementByTagName(doc, "global");
        if(eGlobal == null) return;
        String path  = XMLUtils.getTextContentFromFirstElementByTagName(eGlobal, "word2vec");
        if(path == null)  return;
        train_path = path;
        VectorLoader.getInstance().read();
        boolean word2vec_clustering = XMLUtils.getBooleanTextContentFromFirstElementByTagName(eGlobal, "word2vec_clustering");
        if(!word2vec_clustering)   return;
        String  algorithm  = XMLUtils.getTextContentFromFirstElementByTagName(eGlobal, "clustering_algorithm");
        if(algorithm == null) return;
        int numOfClusters  = XMLUtils.getIntegerTextContentFromFirstElementByTagName(eGlobal, "num_of_clusters");
        if(numOfClusters == 0)  numOfClusters = 25; //default number of clusters
        VectorLoader.getInstance().clustering(algorithm, numOfClusters);
    }

    public void read(){
        if(train_path == null)  return;
        System.out.println("Loading word embedding vector...");
        try (BufferedReader br = new BufferedReader(new FileReader(train_path))) {
            String line;
            String[] arr;
            double[] vector;
            while ((line = br.readLine()) != null) {
                // process the line.
                arr = line.split(" ");
                if(arr.length <= 2) continue;
                vector = new double[arr.length - 1];
                for(int i = 1; i < arr.length; i++)
                    vector[i - 1] = Double.parseDouble(arr[i]);
                word_index.put(arr[0], vectors.size());
                index_word.put(vectors.size(), arr[0]);
                vectors.add(vector);
            }
        } catch (Exception e){
            System.err.println("Load word embedding failed");
        }
    }

    public static VectorLoader getInstance(){
        if(instance == null){
            instance = new VectorLoader();
        }
        return instance;
    }

    public double[] getVector(String word){
        if(word_index.containsKey(word)){
            return vectors.get(word_index.get(word));
        }
        return null;
    }

    public double[][] getVectors(){
        return vectors.toArray(new double[vectors.size()][]);
    }

    private void clustering(int numOfClusters){
        System.out.println("Clustering word embedding vectors with " + numOfClusters + " clusters ...");
        Clustering<double[]> algorithm;
        double[][] data = getVectors();
        switch (clustering_algorithm){
            case DETERMINISTIC_ANNEALING: algorithm = new DeterministicAnnealing(data, numOfClusters);break;
            case GMEANS: algorithm = new GMeans(data, numOfClusters); break;
            case NEURAL_GAS: algorithm = new NeuralGas(data, numOfClusters); break;
            case KMEANS: algorithm = new KMeans(data, numOfClusters); break;
            default: System.err.println("unknown clustering algorithms."); return;
        }
        int centroid;
        for(int i = 0; i < vectors.size(); i++){
            centroid = algorithm.predict(vectors.get(i));
            word_cluster.put(index_word.get(i), centroid);
        }
    }

    public String getCluster(String word){
        return word_cluster.get(word).toString();
    }

    public void clustering(String algorithm, int numOfClusters){
        clustering_algorithm = algorithm;
        clustering(numOfClusters);
    }

    public static void main(String[] args) throws IOException{
        System.out.println(VectorLoader.getInstance().getVectors().length);
    }

}

