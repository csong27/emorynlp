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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.config.NLPConfig;
import edu.emory.mathcs.nlp.emorynlp.component.eval.Eval;
import edu.emory.mathcs.nlp.emorynlp.component.eval.F1Eval;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.train.TrainInfo;
import edu.emory.mathcs.nlp.machine_learning.instance.SparseInstance;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.machine_learning.prediction.StringPrediction;
import edu.emory.mathcs.nlp.machine_learning.util.MLUtils;
import edu.emory.mathcs.nlp.machine_learning.vector.SparseVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTagger<N extends NLPNode> extends NLPOnlineComponent<N,NERState<N>>
{
	private static final long serialVersionUID = 87807440372806016L;

	public NERTagger() {}
	
	public NERTagger(InputStream configuration)
	{
		super(configuration);
	}
	
//	============================== LEXICONS ==============================

	@Override
	protected void readLexicons(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		
	}

	@Override
	protected void writeLexicons(ObjectOutputStream out) throws IOException
	{
		
	}
	
//	============================== ABSTRACT ==============================
	
	@Override
	@SuppressWarnings("unchecked")
	public void setConfiguration(InputStream in)
	{
		setConfiguration((NLPConfig<N>)new NERConfig(in));
	}
	
	@Override
	public Eval createEvaluator()
	{
		return new F1Eval();
	}
	
	@Override
	protected NERState<N> initState(N[] nodes)
	{
		return new NERState<>(nodes);
	}

	@Override
	public void process(N[] nodes)
	{
		process(initState(nodes));
	}

	public void process(NERState<N> state)
	{
		feature_template.setState(state);
		if (!isDecode()) state.saveOracle();

		OnlineOptimizer optimizer;
		SparseInstance inst;
		StringModel model;
		TrainInfo info;
		SparseVector x;
		float[] scores;
		int ydot, yhat;
		int modelID;
		int[] Z;

		while (!state.isTerminate())
		{
			modelID = getModelID(state);
			model = models[modelID];
			x = extractFeatures(state, model);

			if (isTrain())
			{
				optimizer = optimizers[modelID];
				info = train_info[modelID];
				Z = getZeroCostLabels(state, model);
				optimizer.expand(model.getLabelSize(), model.getFeatureSize());
				scores = model.scores(x);
				inst = new SparseInstance(Z, x, scores);
				ydot = inst.getGoldLabel();
				yhat = optimizer.setPredictedLabel(inst);
				optimizer.train(inst);
				if (info.chooseGold() || yhat == -1) yhat = ydot;

			}
			else
			{
				scores = model.scores(x);
				yhat = MLUtils.argmax(scores, model.getLabelSize());
			}
			state.next(new StringPrediction(model.getLabel(yhat), scores[yhat]));
		}

		if (isEvaluate()) state.evaluate(eval);
	}
}
