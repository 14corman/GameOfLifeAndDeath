package main;

import gnu.trove.map.hash.THashMap;
import static main.AIA.NODE_LIST;
 
/**
 * created by: Kunuk Nykjaer &nbsp;&nbsp;&nbsp; website:
 * <a href="https://kunuk.wordpress.com/2010/09/24/q-learning/">Q-learning example with Java</a>
 * <br>
 * edited by: Cory Edwards <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Made all of the variables work with the algorithm <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Added applyPolicy() <br>
 *&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Got rid of main and debugging methods. <br>
 * <br>
 * references: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://en.wikipedia.org/wiki/Q-learning">wikipedia Q-learning</a> <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://people.revoledu.com/kardi/tutorial/ReinforcementLearning/Q-Learning.htm">Q-learning tutorial</a>
 */
public class QLearning {
 
    // path finding
    private final double alpha = 0.1;
    private final double gamma = 0.9;
    
    //Rewards will come from the parent score - child score. Those scores are calculated when a Node is initialized.
    //If a Node is not a valid child of of the parent then that value is set to 0.
    private THashMap<Long, THashMap<Long, Integer>> R;
    
    //Map of every child Node's Q value coming from every parent.
    private THashMap<Long, THashMap<Long, Double>> Q;
    
    //Children Ids for each Node.
    private THashMap<Long, long[]> actions;
 
    /**
     * 
     */
    public QLearning() 
    {
        actions = new THashMap(NODE_LIST.numberOfNodes(), .9f);
        R = new THashMap(NODE_LIST.numberOfNodes(), .9f);
        Q = new THashMap(NODE_LIST.numberOfNodes(), .9f);
        
        for(Node node : NODE_LIST.values())
        {
            if(node != null)
            {
                //Put all possible children for the Node in actions for use later.
                actions.put(node.getNodeId(), node.children == null ? new long[0] : node.children);

                //Temporary maps.
                THashMap<Long, Integer> tempR = new THashMap(NODE_LIST.numberOfNodes(), .9f);
                THashMap<Long, Double> tempQ = new THashMap(NODE_LIST.numberOfNodes(), .9f);

                //Now go through and reset all of the possible children to an actual value.
                for(long childId : node.children)
                {
                    tempR.put(childId, node.getScore() - NODE_LIST.getNode(childId).getScore());
                    tempQ.put(childId, 0.0);
                }

                //Add the temp maps for the Node.
                R.put(node.getNodeId(), tempR);
                Q.put(node.getNodeId(), tempQ);
            }
        }
    }
    
    /**
     * Start the learning process.
     */
    public void learn() 
    {
        // train episodes
        for (int i = 0; i < 10000; i++)
        { 
            //Go over each Node.
            for(long id : actions.keySet())
            {
                //If the Node has children.
                if(actions.get(id).length != 0)
                {
                    //Go over all of the children for the Node.
                    for(long childId : actions.get(id))
                    {
                        //Get all of the variables needed.
                        double q = Q(id, childId);
                        double maxQ = maxQ(childId);
                        int r = R(id, childId);

                        // Q(s,a)= Q(s,a) + alpha * (R(s,a) + gamma * Max(next id, all actions) - Q(s,a))
                        double value = q + alpha * (r + gamma * maxQ - q);
                        setQ(id, childId, value);
                    }
                }
            }
        }
    }
 
    //s is a Node. You will get and iterate over all its children to find the one
    //with the highest value.
    private double maxQ(long s) 
    {
        long[] actionsFromState = actions.get(s);
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < actionsFromState.length; i++) 
        {
            long nextState = actionsFromState[i];
            double value = Q.get(s).get(nextState);
 
            if (value > maxValue)
                maxValue = value;
        }
        return maxValue;
    }
    
    //s = parent Id
    //a = child Id
    private double Q(long s, long a)
    {
        return Q.get(s).get(a);
    }
 
    //s = parent Id
    //a = child Id
    //value = setting value
    private void setQ(long s, long a, double value)
    {
        Q.get(s).put(a, value);
    }
 
    //s = parent Id
    //a = child Id
    private int R(long s, long a) 
    {
        return R.get(s).get(a);
    }
    
    /**
     * Give each Node its new policy.
     */
    public void applyPolicies()
    {
        for(long id : NODE_LIST.keySet())
            NODE_LIST.getNode(id).givePolicy(new THashMap(Q.get(id)));
    }
}