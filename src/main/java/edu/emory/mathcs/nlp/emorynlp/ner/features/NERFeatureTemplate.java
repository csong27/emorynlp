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

import javax.naming.ldap.PagedResultsControl;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NERFeatureTemplate<N extends NLPNode> extends FeatureTemplate<N,NERState<N>>
{
	private static final long serialVersionUID = 2750773840515707758L;

	public NERFeatureTemplate()	
	{
		super();
	}
	
//	========================= FEATURE EXTRACTORS =========================

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

	private double euclideanDistance(double[] vectorA, double[] vectorB) {
		double sum = 0.0;
		for(int i = 0; i < vectorA.length; i++)
			sum = sum + Math.pow((vectorA[i] - vectorB[i]), 2.0);
		return Math.sqrt(sum);
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
			case ambiguity_class: return null; // To be filled.
			case prediction_history: return getPredictionHistory(node);
			case word2vec_clusters: return getWord2VecCluster(node);
			case word2vec_norm: return getVectorNorm(node);
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
