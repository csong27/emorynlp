package edu.emory.mathcs.nlp.emorynlp.component.node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Song on 11/1/2015.
 */
public class Quiz6 {
    public List<NLPNode> getArgumentCandidateList(NLPNode[] nodes, int predicateID)
    {
        NLPNode head, curr = nodes[predicateID];
        List<NLPNode> list = new ArrayList<>();
        //child and descendant
        list.addAll(curr.getSubNodeList());
        //getSubNodeList is inclusive, remove curr
        list.remove(curr);
        //ancestor's dependent
        while (true){
            head = curr.getDependencyHead();
            if (head == null) break;   //break if hit root
            for (NLPNode node : head.getDependentList()) {  //dependents of head
                if(node.getID() != predicateID)
                    list.add(node);
            }
            curr = head;
        }

        return list;
    }
}
