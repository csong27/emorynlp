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
package edu.emory.mathcs.nlp.bin;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.emory.mathcs.nlp.common.random.XORShiftRandom;
import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.emorynlp.component.NLPOnlineComponent;
import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;
import edu.emory.mathcs.nlp.emorynlp.component.train.NLPOnlineTrain;
import edu.emory.mathcs.nlp.emorynlp.component.train.TrainInfo;
import edu.emory.mathcs.nlp.emorynlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.emorynlp.component.util.PredictionHistory;
import edu.emory.mathcs.nlp.emorynlp.ner.NERState;
import edu.emory.mathcs.nlp.emorynlp.ner.NERTagger;
import edu.emory.mathcs.nlp.emorynlp.ner.features.NERFeatureTemplate0;
import edu.emory.mathcs.nlp.emorynlp.ner.features.NERFeatureTemplateExperiment;
import edu.emory.mathcs.nlp.machine_learning.model.StringModel;
import edu.emory.mathcs.nlp.machine_learning.optimization.OnlineOptimizer;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERTrain extends NLPOnlineTrain<NLPNode,NERState<NLPNode>>
{
	public NERTrain(String[] args)
	{
		super(args);
	}

	@Override
	protected NLPOnlineComponent<NLPNode,NERState<NLPNode>> createComponent(InputStream config)
	{
		return new NERTagger<>(config);
	}

	@Override
	protected void initComponent(NLPOnlineComponent<NLPNode,NERState<NLPNode>> component, List<String> inputFiles)
	{
		initComponentSingleModel(component, inputFiles);
	}

	@Override
	protected FeatureTemplate<NLPNode,NERState<NLPNode>> createFeatureTemplate()
	{

		switch (feature_template)
		{
			case  0: return new NERFeatureTemplate0<>();
			case  1: return new NERFeatureTemplateExperiment<>();
			default: throw new IllegalArgumentException("Unknown feature template: "+feature_template);
		}
	}

	@Override
	protected double train(List<String> trainFiles, List<String> developFiles, NLPOnlineComponent<NLPNode,?> component, TSVReader reader, OnlineOptimizer optimizer, StringModel model, TrainInfo info) {
		int bestEpoch = -1, bestNZW = -1, nzw, epoch;
		Random rand = new XORShiftRandom(9);
		double bestScore = 0, score;
		byte[] bestModel = null;

		BinUtils.LOG.info("\n"+optimizer.toString()+", bias = "+model.getBias()+", batch = "+info.getBatchSize()+"\n");

		for (epoch=1; epoch<=info.getMaxEpochs(); epoch++)
		{
			component.setFlag(NLPFlag.TRAIN);
			PredictionHistory.getInstance().resetCount();
			Collections.shuffle(trainFiles, rand);
			iterate(reader, trainFiles, component::process, optimizer, info);
			//reset prediction history
			PredictionHistory.getInstance().resetCount();
			score = evaluate(developFiles, component, reader);
			nzw = optimizer.getWeightVector().countNonZeroWeights();

			BinUtils.LOG.info(String.format("%5d: %s, RollIn = %5.4f, NZW = %d\n", epoch, component.getEval().toString(), info.getRollInProbability(), nzw));
			info.updateRollInProbability();

			if (bestScore < score || (bestScore == score && nzw < bestNZW))
			{
				bestNZW   = nzw;
				bestEpoch = epoch;
				bestScore = score;
				bestModel = model.toByteArray();
			}
		}

		BinUtils.LOG.info(String.format(" Best: %5.2f, epoch: %d, nzw: %d\n", bestScore, bestEpoch, bestNZW));
		model.fromByteArray(bestModel);
		return bestScore;
	}


	@Override
	protected NLPNode createNode()
	{
		return new NLPNode();
	}

	static public void main(String[] args)
	{
		new NERTrain(args).train();
	}
}