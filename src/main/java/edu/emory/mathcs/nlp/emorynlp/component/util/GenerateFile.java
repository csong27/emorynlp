package edu.emory.mathcs.nlp.emorynlp.component.util;

import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.emorynlp.component.node.NLPNode;
import edu.emory.mathcs.nlp.emorynlp.component.reader.TSVReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Song on 10/31/2015.
 */
public class GenerateFile {

    private void read() throws IOException {
        String train_path = "ner_data/eng.trn.bilou";
        String ext = "*";
        List<String> trainFiles = FileUtils.getFileList(train_path, ext);
        TSVReader reader = new TSVReader();
        reader.form = 0;
        reader.lemma = 1;
        reader.pos = 2;
        reader.feats = 3;
        reader.nament = 4;
        reader.deprel = -1;
        reader.dhead = -1;
        iterate(reader, trainFiles, "train.txt");
    }

    private void iterate(TSVReader reader, List<String> filenames, String outputFile) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        NLPNode[] nodes;
        for (String filename : filenames)
        {
            reader.open(IOUtils.createFileInputStream(filename));
            int i;
            while ((nodes = reader.next(NLPNode::new)) != null){
                if(nodes[1].getWordForm().equals("-DOCSTART-")) continue;
                StringJoiner join = new StringJoiner(" ");
                for(i = 1; i < nodes.length; i++)
                    join.add(nodes[i].getWordForm());
                writer.write(join.toString());
                writer.newLine();
            }
            reader.close();
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException{
        GenerateFile gf = new GenerateFile();
        gf.read();
    }
}

