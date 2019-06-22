import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.format.UmlCollaborationInteraction;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlMessage;
import java.util.HashMap;
import java.util.HashSet;

public class MyUmlCollaborationInteraction
        implements UmlCollaborationInteraction
{
    private HashMap<String, String> intername2id = new HashMap<>();
    private HashSet<String> dupintername = new HashSet<>();
    
    /* <interaction id ...> */
    private HashMap<String, HashMap<String, String>>
            lifelinename2id = new HashMap<>();
    private HashMap<String, HashSet<String>> duplifelinename = new HashMap<>();
    
    /* <Interaction id, <lifeline or end id, number of incomming message>> */
    private HashMap<String, HashMap<String, Integer>>
            incomingcount = new HashMap<>();
    
    /* <Interaction id, number of message or lifeline> */
    private HashMap<String, Integer> messagecount = new HashMap<>();
    private HashMap<String, Integer> lifelinecount = new HashMap<>();
    
    public MyUmlCollaborationInteraction(HashSet<UmlElement> elements)
    {
        int n;
        for (UmlElement e:elements)
        {
            switch (e.getElementType())
            {
                case UML_INTERACTION:
                    if (intername2id.containsKey(e.getName()))
                    {
                        dupintername.add(e.getName());
                    }
                    intername2id.put(e.getName(),e.getId());
                    break;
                case UML_MESSAGE: // UmlMessage的parent_id是interaction的id
                    n = messagecount.getOrDefault(e.getParentId(),0);
                    messagecount.put(e.getParentId(),n + 1);
    
                    UmlMessage m = (UmlMessage)e;
                    HashMap<String, Integer> temp = incomingcount.getOrDefault(
                            e.getParentId(),new HashMap<>());
                    n = temp.getOrDefault(m.getTarget(),0);
                    temp.put(m.getTarget(),n + 1);
                    incomingcount.put(e.getParentId(),temp);
                    break;
                case UML_LIFELINE:
                    n = lifelinecount.getOrDefault(e.getParentId(),0);
                    lifelinecount.put(e.getParentId(),n + 1);
                    
                    HashMap<String, String> tem = lifelinename2id.getOrDefault(
                            e.getParentId(),new HashMap<>());
                    if (tem.containsKey(e.getName()))
                    {
                        HashSet<String> set = duplifelinename.getOrDefault(
                                e.getParentId(),new HashSet<>());
                        set.add(e.getName());
                        duplifelinename.put(e.getParentId(),set);
                    }
                    tem.put(e.getName(),e.getId());
                    lifelinename2id.put(e.getParentId(),tem);
                    break;
                default:
            }
        }
    }

    public int getParticipantCount(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException
    {
        if (!intername2id.keySet().contains(interactionName))
        {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (dupintername.contains(interactionName))
        {
            throw new InteractionDuplicatedException(interactionName);
        }
        else
        {
            String id = intername2id.get(interactionName);
            return lifelinecount.getOrDefault(id,0);
        }
    }
    
    public int getMessageCount(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException
    {
        if (!intername2id.keySet().contains(interactionName))
        {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (dupintername.contains(interactionName))
        {
            throw new InteractionDuplicatedException(interactionName);
        }
        else
        {
            String id = intername2id.get(interactionName);
            return messagecount.getOrDefault(id,0);
        }
    }
    
    public int getIncomingMessageCount(
            String interactionName, String lifelineName)
            throws
            InteractionNotFoundException,
            InteractionDuplicatedException,
            LifelineNotFoundException,
            LifelineDuplicatedException
    {
        String interid = intername2id.get(interactionName);
        if (!intername2id.keySet().contains(interactionName))
        {
            throw new InteractionNotFoundException(interactionName);
        }
        else if (dupintername.contains(interactionName))
        {
            throw new InteractionDuplicatedException(interactionName);
        }
        else if (!lifelinename2id.containsKey(interid) ||
                 !lifelinename2id.get(interid).containsKey(lifelineName))
        {
            throw new LifelineNotFoundException(interactionName,lifelineName);
        }
        else if (duplifelinename.containsKey(interid) &&
                 duplifelinename.get(interid).contains(lifelineName))
        {
            throw new LifelineDuplicatedException(interactionName,lifelineName);
        }
        else
        {
            String lifelineid = lifelinename2id.get(interid).get(lifelineName);
            if (incomingcount.containsKey(interid) &&
                incomingcount.get(interid).containsKey(lifelineid))
            {
                return incomingcount.get(interid).get(lifelineid);
            }
            else
            {
                return 0;
            }
        }
    }
}
