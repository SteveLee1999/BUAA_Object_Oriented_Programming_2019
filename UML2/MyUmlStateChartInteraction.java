import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.format.UmlStateChartInteraction;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlTransition;
import java.util.HashMap;
import java.util.HashSet;

public class MyUmlStateChartInteraction implements UmlStateChartInteraction
{
    private HashSet<String> dupnamesmname = new HashSet<>();
    private HashSet<String> stateidset = new HashSet<>();
    
    /* <statemachine name, statemachine id> */
    private HashMap<String, String> smname2id = new HashMap<>();
    
    /* <regionid, <state name, state id>> */
    private HashMap<String, HashMap<String, String>>
            statename2id = new HashMap<>();
    
    /* <statemachine id, region id> */
    private HashMap<String, String> sm2region = new HashMap<>();
    
    /* <region id, state count> */
    private HashMap<String, Integer> statecount = new HashMap<>();
    
    /* <region id, transition count> */
    private HashMap<String, Integer> transcount = new HashMap<>();
    
    /* <region id, set of state name> */
    private HashMap<String, HashSet<String>> statenameset = new HashMap<>();
    
    /* <region id, set of duplicate state name> */
    private HashMap<String, HashSet<String>> dupstatename = new HashMap<>();
    
    /* state : <srcid, set of dstid that srcid can arrive at> */
    private HashMap<String, HashSet<String>> dirarriveset = new HashMap<>();
    
    public MyUmlStateChartInteraction(HashSet<UmlElement> elements)
    {
        HashSet<String> smidset = new HashSet<>();
        HashSet<String> haspseudo = new HashSet<>();
        HashSet<String> hasfinal = new HashSet<>();
        HashSet<String> finalstateid = new HashSet<>();
        int n;
        for (UmlElement e:elements)
        {
            switch (e.getElementType())
            {
                case UML_STATE_MACHINE:
                    smidset.add(e.getId());
                    if (smname2id.containsKey(e.getName()))
                    {
                        dupnamesmname.add(e.getName());
                    }
                    smname2id.put(e.getName(),e.getId());
                    break;
                case UML_FINAL_STATE:
                    finalstateid.add(e.getId());
                    hasfinal.add(e.getParentId());
                    break;
                case UML_PSEUDOSTATE:
                    haspseudo.add(e.getParentId());
                    break;
                case UML_STATE:
                    stateidset.add(e.getId());
                    
                    HashMap<String,String> temp2 = statename2id.getOrDefault(
                            e.getParentId(),new HashMap<>());
                    temp2.put(e.getName(),e.getId());
                    statename2id.put(e.getParentId(),temp2);
                    
                    n = statecount.getOrDefault(e.getParentId(),0);
                    statecount.put(e.getParentId(),n + 1);
                    
                    HashSet<String> temp1 = statenameset.getOrDefault(
                            e.getParentId(),new HashSet<>());
                    if (temp1.contains(e.getName()))
                    {
                        HashSet<String> temp3 = dupstatename.getOrDefault(
                                e.getParentId(),new HashSet<>());
                        temp3.add(e.getName());
                        dupstatename.put(e.getParentId(),temp3);
                    }
                    temp1.add(e.getName());
                    statenameset.put(e.getParentId(),temp1);
                    break;
                default:
            }
        }
        for (UmlElement e:elements)
        {
            switch (e.getElementType())
            {
                case UML_TRANSITION:
                    if (finalstateid.contains(((UmlTransition)e).getTarget()))
                    {
                        HashSet<String> temp = dirarriveset.getOrDefault(
                                ((UmlTransition)e).getSource(),new HashSet<>());
                        temp.add("final");
                        dirarriveset.put(((UmlTransition)e).getSource(),temp);
                    }
                    else
                    {
                        HashSet<String> temp = dirarriveset.getOrDefault(
                                ((UmlTransition)e).getSource(),new HashSet<>());
                        temp.add(((UmlTransition) e).getTarget());
                        dirarriveset.put(((UmlTransition)e).getSource(),temp);
                    }
        
                    n = transcount.getOrDefault(e.getParentId(),0);
                    transcount.put(e.getParentId(),n + 1);
                    break;
                case UML_STATE:
                    if (!dirarriveset.keySet().contains(e.getId()))
                    {
                        dirarriveset.put(e.getId(),new HashSet<>());
                    }
                    break;
                case UML_REGION:
                    if (smidset.contains(e.getParentId()))
                    {
                        sm2region.put(e.getParentId(),e.getId());
                    }
                    break;
                default:
            }
        }
        for (String s:haspseudo)
        {
            n = statecount.getOrDefault(s,0);
            statecount.put(s,n + 1);
        }
        for (String s:hasfinal)
        {
            n = statecount.getOrDefault(s,0);
            statecount.put(s,n + 1);
        }
        buildArriveGraph();
    }
    
    private HashSet<String> visited = new HashSet<>();
    private HashSet<String> completed = new HashSet<>();
    private HashMap<String, HashSet<String>> arriveset = new HashMap<>();
    
    private void visit(String state)
    {
        /*if (visited.contains(state)) {
            return;
        } else if (dirarriveset.containsKey(state)) {
            visited.add(state);
            System.out.println(String.format("%s",state));
            for (String next : dirarriveset.get(state)) {
                visit(next);
            }
        }
        arriveset.put(state, new HashSet<>(visited));*/
        
        visited.add(state);
        if (dirarriveset.keySet().contains(state))
        {
            HashSet<String> addset = new HashSet<>(dirarriveset.get(state));
            for (String next:dirarriveset.get(state))
            {
                if (!visited.contains(next) && !completed.contains(next))
                {
                    visit(next);
                }
                if (arriveset.containsKey(next))
                {
                    addset.addAll(arriveset.get(next));
                }
            }
            arriveset.get(state).addAll(addset);
        }
    }
    
    private void buildArriveGraph()
    {
        for (String state:stateidset)
        {
            arriveset.put(state,new HashSet<>());
        }
        for (String state:stateidset) // stateid不包含初始状态与末状态
        {
            visited.clear();
            visit(state);
            completed.add(state);
        }
    }
    
    public int getStateCount(String stateMachineName)
            throws StateMachineNotFoundException,
                   StateMachineDuplicatedException
    {
        if (!smname2id.containsKey(stateMachineName))
        {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (dupnamesmname.contains(stateMachineName))
        {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        else
        {
            String regionid = sm2region.get(smname2id.get(stateMachineName));
            return statecount.getOrDefault(regionid,0);
        }
    }
    
    public int getTransitionCount(String stateMachineName)
            throws StateMachineNotFoundException,
                   StateMachineDuplicatedException
    {
        if (!smname2id.containsKey(stateMachineName))
        {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (dupnamesmname.contains(stateMachineName))
        {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        else
        {
            String smid = smname2id.get(stateMachineName);
            String regionid = sm2region.get(smid);
            return transcount.getOrDefault(regionid,0);
        }
    }
    
    public int getSubsequentStateCount(
            String stateMachineName, String stateName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException,
            StateNotFoundException, StateDuplicatedException
    {
        String regionid = sm2region.get(smname2id.get(stateMachineName));
        
        if (!smname2id.containsKey(stateMachineName))
        {
            throw new StateMachineNotFoundException(stateMachineName);
        }
        else if (dupnamesmname.contains(stateMachineName))
        {
            throw new StateMachineDuplicatedException(stateMachineName);
        }
        else if (!statenameset.containsKey(regionid) ||
                !statenameset.get(regionid).contains(stateName))
        {
            throw new StateNotFoundException(stateMachineName,stateName);
        }
        else if (dupstatename.containsKey(regionid) &&
                dupstatename.get(regionid).contains(stateName))
        {
            throw new StateDuplicatedException(stateMachineName,stateName);
        }
        else if (statename2id.containsKey(regionid) &&
                 statename2id.get(regionid).containsKey(stateName))
        {
            return arriveset.get(
                    statename2id.get(regionid).get(stateName)).size();
        }
        else
        {
            return 0;
        }
    }
}

