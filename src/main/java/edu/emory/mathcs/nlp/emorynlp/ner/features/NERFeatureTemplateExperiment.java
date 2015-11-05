package edu.emory.mathcs.nlp.emorynlp.ner.features;

/**
 * Created by Song on 10/31/2015.
 */

import edu.emory.mathcs.nlp.emorynlp.component.feature.FeatureItem;
import edu.emory.mathcs.nlp.emorynlp.component.feature.Field;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;

public class NERFeatureTemplateExperiment<N extends NLPNode> extends NERFeatureTemplate<N>
{
    private static final long serialVersionUID = -3867869616627234917L;

    @Override
    protected void init()
    {
        // 1-gram features
        add(new FeatureItem<>( 0, Field.simplified_word_form));
        add(new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>( 0, Field.word_shape, 2));
        add(new FeatureItem<>( 0, Field.lemma));
        add(new FeatureItem<>( 0, Field.part_of_speech_tag));
        add(new FeatureItem<>( 0, Field.ambiguity_class));

        add(new FeatureItem<>(-1, Field.simplified_word_form));
        add(new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>(-1, Field.word_shape, 2));
        add(new FeatureItem<>(-1, Field.part_of_speech_tag));
        add(new FeatureItem<>(-1, Field.ambiguity_class));
        add(new FeatureItem<>(-1, Field.named_entity_tag));

        add(new FeatureItem<>( 1, Field.simplified_word_form));
        add(new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>( 1, Field.word_shape, 2));
        add(new FeatureItem<>( 1, Field.part_of_speech_tag));
        add(new FeatureItem<>( 1, Field.ambiguity_class));

        add(new FeatureItem<>( 2, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>( 2, Field.part_of_speech_tag));

        add(new FeatureItem<>(-2, Field.part_of_speech_tag));
        add(new FeatureItem<>(-2, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>(-2, Field.named_entity_tag));

        //feats
        add(new FeatureItem<>( 0, Field.feats, "p2"));
//        add(new FeatureItem<>( 1, Field.feats, "p2"));
//        add(new FeatureItem<>(-1, Field.feats, "p2"));

        //prediction history
        add(new FeatureItem<>( 0, Field.prediction_history));

        //word2vec cluster
//        add(new FeatureItem<>( 0, Field.word2vec_clusters));

        //my n-gram
        add(new FeatureItem<>(-2, Field.named_entity_tag), new FeatureItem<>(-1, Field.named_entity_tag));
//
//        add(new FeatureItem<>(-1, Field.part_of_speech_tag), new FeatureItem<>( 0, Field.part_of_speech_tag));
//        add(new FeatureItem<>( 0, Field.part_of_speech_tag), new FeatureItem<>( 1, Field.part_of_speech_tag));


        // 2-gram features
        add(new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 2, Field.uncapitalized_simplified_word_form));

        add(new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 0, Field.part_of_speech_tag));
        add(new FeatureItem<>( 1, Field.uncapitalized_simplified_word_form), new FeatureItem<>( 0, Field.part_of_speech_tag));

        add(new FeatureItem<>(-2, Field.part_of_speech_tag), new FeatureItem<>(-1, Field.part_of_speech_tag));
        add(new FeatureItem<>( 1, Field.ambiguity_class)   , new FeatureItem<>( 2, Field.ambiguity_class));

        // 3-gram features
        add(new FeatureItem<>(-3, Field.named_entity_tag), new FeatureItem<>(-2, Field.named_entity_tag), new FeatureItem<>( 1, Field.ambiguity_class));
        add(new FeatureItem<>( 1, Field.ambiguity_class) , new FeatureItem<>( 2, Field.ambiguity_class) , new FeatureItem<>( 3, Field.ambiguity_class));

        add(new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form), new FeatureItem<>(-1, Field.named_entity_tag), new FeatureItem<>( 0, Field.part_of_speech_tag));
        add(new FeatureItem<>(-1, Field.uncapitalized_simplified_word_form), new FeatureItem<>(-1, Field.named_entity_tag), new FeatureItem<>(-1, Field.part_of_speech_tag));

        // affix features
        add(new FeatureItem<>( 0, Field.suffix, 1));
        //from Lexicon Infused Phrase Embeddings for Named Entity Resolution
        add(new FeatureItem<>( 0, Field.suffix, 4));
        add(new FeatureItem<>( 0, Field.prefix, 4));
//        add(new FeatureItem<>( 0, Field.prefix, 6));
//        add(new FeatureItem<>( 0, Field.prefix, 10));

        add(new FeatureItem<>( 0, Field.suffix, 4), new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form));
        add(new FeatureItem<>(-1, Field.suffix, 4), new FeatureItem<>( 0, Field.uncapitalized_simplified_word_form));

        // orthographic features
        addSet(new FeatureItem<>(-1, Field.orthographic));

        addSet(new FeatureItem<>( 0, Field.orthographic));
        addSet(new FeatureItem<>( 1, Field.orthographic));

        // distributional semantics
        addSet(new FeatureItem<>( 0, Field.clusters, 0));
        addSet(new FeatureItem<>( 1, Field.clusters, 0));
        addSet(new FeatureItem<>( 2, Field.clusters, 0));

//        addSet(new FeatureItem<>(-2, Field.binary));
//        addSet(new FeatureItem<>(-1, Field.binary));
        addSet(new FeatureItem<>(0, Field.binary));

        //vector norm
//        add(new FeatureItem<>(-1, Field.word2vec_norm));
//        add(new FeatureItem<>(0, Field.word2vec_norm));
        //cosine similartiy
//        add(new FeatureItem<>(-1, Field.word2vec_cos));
//        add(new FeatureItem<>( 0, Field.word2vec_cos));
        addDense(new FeatureItem<>( 0, Field.word2vec_dense));
        addDense(new FeatureItem<>(-1, Field.word2vec_dense));

        //title
        add(new FeatureItem<>(-1, Field.title), new FeatureItem<>( 0, Field.title));


    }
}