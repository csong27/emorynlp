/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.emorynlp.ner.features;

import edu.emory.mathcs.nlp.common.util.CharUtils;
import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.util.PredictionHistory;
import edu.emory.mathcs.nlp.emorynlp.component.util.VectorLoader;
import edu.emory.mathcs.nlp.emorynlp.ner.NERState;
import edu.emory.mathcs.nlp.machine_learning.vector.StringVector;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NERFeatureTemplate<N extends NLPNode> extends FeatureTemplate<N,NERState<N>>
{
	private static final long serialVersionUID = 2750773840515707758L;

	public void addDense(FeatureItem<?> items)
	{
		feature_dense.add(items);
	}

	public NERFeatureTemplate()	
	{
		super();
	}

	@Override
	public StringVector extractFeatures()
	{
		StringVector x = new StringVector();
		int i, j, type = 0;
		String[] t;
		double[] v;
		String f;

		for (i=0; i<feature_list.size(); i++,type++)
		{
			f = getFeature(feature_list.get(i));
			if (f != null) x.add(type, f);
		}

		for (i=0; i<feature_set.size(); i++,type++)
		{
			t = getFeatures(feature_set.get(i));
			if (t != null) for (String s : t) x.add(type, s);
		}

		for (i=0; i<feature_dense.size(); i++,type++)
		{
			v = getDenseFeature(feature_dense.get(i));
			if (v != null) {
				for (j = 0; j < v.length; j++) {
					x.add(type, "" + j, (float) v[j]);
				}
			}
		}
		return x;
	}


	
//	========================= FEATURE EXTRACTORS =========================

	private double[] getDenseFeature(FeatureItem<?> item){
		N node = state.getNode(item);
		if(node == null)	return null;
		return getVector(node);
	}

	private String getPredictionHistory(N node){
		return PredictionHistory.getInstance().getFeature(StringUtils.toLowerCase(node.getSimplifiedWordForm()));
	}

	private String getWord2VecCluster(N node){
		try{
			return VectorLoader.getInstance().getCluster(StringUtils.toLowerCase(node.getWordForm()));
		} catch (Exception e){
			return null;
		}
	}
	private String getVectorNorm(N node) {
		double[] v1 = VectorLoader.getInstance().getVector(StringUtils.toLowerCase(node.getWordForm()));
		if(v1 == null) return null;
		else {
			double norm = euclideanDistance(v1, new double[v1.length]);
			return String.format("%5.1f", norm);
		}
	}

	private String getVectorSimilarity(N node){
		if(node.getID() <= 1)
			return null;
		else{
			N prevNode = state.getNode(node.getID(), -1);
			double[] v1 = VectorLoader.getInstance().getVector(StringUtils.toLowerCase(node.getWordForm()));
			double[] v2 = VectorLoader.getInstance().getVector(StringUtils.toLowerCase(prevNode.getWordForm()));
			if(v1 != null && v2 != null)
				return String.format("%5.1f", cosineSimilarity(v1, v2));
			else
				return null;
		}
	}

	private double euclideanDistance(double[] vectorA, double[] vectorB) {
		double sum = 0.0;
		for(int i = 0; i < vectorA.length; i++)
			sum = sum + Math.pow((vectorA[i] - vectorB[i]), 2.0);
		return Math.sqrt(sum);
	}

	private double[] getVector(N node){
		return VectorLoader.getInstance().getVector(StringUtils.toLowerCase(node.getWordForm()));
	}

	private double cosineSimilarity(double[] vectorA, double[] vectorB) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		for (int i = 0; i < vectorA.length; i++) {
			dotProduct += vectorA[i] * vectorB[i];
			normA += Math.pow(vectorA[i], 2);
			normB += Math.pow(vectorB[i], 2);
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private String getTitleFeature(N node){
		char first = node.getWordForm().charAt(0);
		return CharUtils.isUpperCase(first) ? "1" : "0";
	}

	@Override
	protected String getFeature(FeatureItem<?> item)
	{
		N node = state.getNode(item);
		if (node == null) return null;
		
		switch (item.field)
		{
			case ambiguity_class: return state.getAmbiguityClass(node);
			case prediction_history: return getPredictionHistory(node);
			case word2vec_clusters: return getWord2VecCluster(node);
			case word2vec_norm: return getVectorNorm(node);
			case word2vec_cos: return getVectorSimilarity(node);
			case title: return getTitleFeature(node);
			default: return getFeature(item, node);
		}
	}
	
	@Override
	protected String[] getFeatures(FeatureItem<?> item)
	{
		N node = state.getNode(item);
		return (node == null) ? null : getFeatures(item, node);
	}
}
