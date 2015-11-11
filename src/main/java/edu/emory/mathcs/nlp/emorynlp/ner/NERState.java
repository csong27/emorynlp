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
package edu.emory.mathcs.nlp.emorynlp.ner;

import java.util.*;
import java.util.Map.Entry;

import edu.emory.mathcs.nlp.common.util.StringUtils;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.eval.F1Eval;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.state.L2RState;
import edu.emory.mathcs.nlp.emorynlp.component.util.BILOU;
import edu.emory.mathcs.nlp.emorynlp.component.util.Counter;
import edu.emory.mathcs.nlp.emorynlp.component.util.Dictionary;
import edu.emory.mathcs.nlp.emorynlp.component.util.PredictionHistory;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.intellij.lang.regexp.psi.RegExpQuantifier;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERState<N extends NLPNode> extends L2RState<N>
{
	String[] ambiguity_classes;

	public NERState(N[] nodes)
	{
		super(nodes);
		ambiguity_classes = new String[nodes.length];
		getAmbiguityClasses();
	}

	private void getAmbiguityClasses()
	{
		ambiguity_classes[0] = null;
		String tag, nextTag, entity, classes;
		int i, j, k;
		for(i = 1; i < nodes.length; i++){
			tag = getLabel(nodes[i]).substring(0, 1);
			if(tag.equals("O"))
				ambiguity_classes[i] = null;
			if(tag.equals("U"))
				ambiguity_classes[i] = Dictionary.getInstance().getAmbiguityClass(nodes[i].getSimplifiedWordForm());
			if(tag.equals("B")){
				entity = nodes[i].getSimplifiedWordForm();
				j = i + 1;
				while(j < nodes.length){
					nextTag = getLabel(nodes[j]).substring(0, 1);
					entity += " " + nodes[j].getSimplifiedWordForm();
					if(nextTag.equals("L")) break;
					j++;
				}
				classes = Dictionary.getInstance().getAmbiguityClass(entity);
				for(k = i; k <= j; k++){
					nextTag = getLabel(nodes[j]).substring(0 , 1);
					if(classes != null)
						ambiguity_classes[k] = nextTag + " " + classes;
					else
						ambiguity_classes[k] = null;
				}
			}
		}
	}

	public String getAmbiguityClass(N node){
		return ambiguity_classes[node.getID()];
	}

	@Override
	protected String getLabel(N node)
	{
		return node.getNamedEntityTag();
	}
	
	@Override
	protected String setLabel(N node, String label)
	{
		String s = node.getNamedEntityTag();
		node.setNamedEntityTag(label);
		return s;
	}

	@Override
	public void evaluate(Eval eval)
	{
		Int2ObjectMap<String> gMap = BILOU.collectNamedEntityMap(oracle, String::toString, 1, nodes.length);
		Int2ObjectMap<String> sMap = BILOU.collectNamedEntityMap(nodes, this::getLabel, 1, nodes.length);
		((F1Eval)eval).add(countCorrect(sMap, gMap), sMap.size(), gMap.size());
	}
	
	private int countCorrect(Int2ObjectMap<String> map1, Int2ObjectMap<String> map2)
	{
		int count = 0;
		String s2;
		
		for (Entry<Integer,String> p1 : map1.entrySet())
		{
			s2 = map2.get(p1.getKey());
			if (s2 != null && s2.equals(p1.getValue())) count++; 
		}
		
		return count;
	}

	@Override
	public void next(StringPrediction prediction)
	{
		String word = StringUtils.toLowerCase(nodes[input].getSimplifiedWordForm());
		String label = prediction.getLabel();
		PredictionHistory.getInstance().addLabelCount(word, label);
		setLabel(nodes[input++], label);
	}

	public void printErrorLabel(){
		String label, prevLabel;
		BILOU curr, prev;
		for(int i = 2; i < nodes.length; i++){
			label = nodes[i].getNamedEntityTag();
			prevLabel = nodes[i - 1].getNamedEntityTag();
			curr = BILOU.toBILOU(label);
			prev = BILOU.toBILOU(prevLabel);
			if(curr == BILOU.I || curr == BILOU.L)
				if(prev == BILOU.B || prev == BILOU.I)
					if(!BILOU.toTag(label).equals(BILOU.toTag(prevLabel)))
						System.out.println(label + " & " + prevLabel + " & " + oracle[i]);
		}
	}

	public void updateErrorLabel(Map<String, Counter> errorCounter){
		if(errorCounter == null)
			errorCounter = new HashMap<>();
		String label, key;
		for(int i = 1; i < nodes.length; i++){
			label = getLabel(nodes[i]);
			if(!label.equals(oracle[i])) {
				key = nodes[i].getWordForm() + " & " + label + " & " + oracle[i];
				errorCounter.computeIfAbsent(key, v -> new Counter()).increment();
			}
		}
	}
}
