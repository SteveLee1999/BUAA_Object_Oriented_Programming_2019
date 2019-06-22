import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.AttributeQueryType;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.InteractionDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.InteractionNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.LifelineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.LifelineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.StateMachineNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.StateNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.interact.format.UmlGeneralInteraction;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlElement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyUmlGeneralInteraction implements UmlGeneralInteraction
{
    private MyUmlStandardPreCheck precheck;
    private MyUmlClassModelInteraction classmodel;
    private MyUmlCollaborationInteraction interaction;
    private MyUmlStateChartInteraction statemachine;
    
    public MyUmlGeneralInteraction(UmlElement... elements)
            throws UmlRule008Exception,
            UmlRule009Exception,
            UmlRule002Exception
    {
        HashSet<UmlElement> classModeElements = new HashSet<>();
        HashSet<UmlElement> collaborationElements = new HashSet<>();
        HashSet<UmlElement> stateChartElements = new HashSet<>();
        HashSet<UmlElement> preCheckElements = new HashSet<>();
        
        for (UmlElement e:elements)
        {
            if (e.getElementType() == ElementType.UML_ASSOCIATION ||
                    e.getElementType() == ElementType.UML_ASSOCIATION_END ||
                    e.getElementType() == ElementType.UML_GENERALIZATION ||
                    e.getElementType() == ElementType.UML_INTERFACE ||
                    e.getElementType() == ElementType.UML_INTERFACE_REALIZATION)
            {
                classModeElements.add(e);
                preCheckElements.add(e);
                continue;
            }
            if (e.getElementType() == ElementType.UML_CLASS ||
                e.getElementType() == ElementType.UML_OPERATION ||
                e.getElementType() == ElementType.UML_PARAMETER ||
                e.getElementType() == ElementType.UML_ATTRIBUTE)
            {
                classModeElements.add(e);
                preCheckElements.add(e);
                continue;
            }
            if (e.getElementType() == ElementType.UML_ASSOCIATION ||
                e.getElementType() == ElementType.UML_ASSOCIATION_END ||
                e.getElementType() == ElementType.UML_GENERALIZATION ||
                e.getElementType() == ElementType.UML_INTERFACE ||
                e.getElementType() == ElementType.UML_INTERFACE_REALIZATION)
            {
                classModeElements.add(e);
                continue;
            }
            if (e.getElementType() == ElementType.UML_INTERACTION ||
                e.getElementType() == ElementType.UML_LIFELINE ||
                e.getElementType() == ElementType.UML_MESSAGE)
            {
                collaborationElements.add(e);
                continue;
            }
            if (e.getElementType() == ElementType.UML_STATE_MACHINE ||
                e.getElementType() == ElementType.UML_PSEUDOSTATE ||
                e.getElementType() == ElementType.UML_FINAL_STATE ||
                e.getElementType() == ElementType.UML_STATE ||
                e.getElementType() == ElementType.UML_TRANSITION ||
                e.getElementType() == ElementType.UML_REGION)
            {
                stateChartElements.add(e);
            }
        }
        
        precheck = new MyUmlStandardPreCheck(preCheckElements);
        classmodel = new MyUmlClassModelInteraction(classModeElements);
        interaction = new MyUmlCollaborationInteraction(collaborationElements);
        statemachine = new MyUmlStateChartInteraction(stateChartElements);
    }
    
    public int getClassCount()
    {
        return classmodel.getClassCount();
    }
    
    public int getClassOperationCount(
            String className, OperationQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getClassOperationCount(className,queryType);
    }
    
    public int getClassAttributeCount(
            String className, AttributeQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getClassAttributeCount(className,queryType);
    }
    
    public int getClassAssociationCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getClassAssociationCount(className);
    }
    
    public List<String> getClassAssociatedClassList(String className)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getClassAssociatedClassList(className);
    }
    
    public Map<Visibility, Integer> getClassOperationVisibility(
            String className, String operationName)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getClassOperationVisibility(className,operationName);
    }
    
    public Visibility getClassAttributeVisibility(
            String className, String attributeName)
            throws ClassNotFoundException, ClassDuplicatedException,
            AttributeNotFoundException, AttributeDuplicatedException
    {
        return classmodel.getClassAttributeVisibility(className,attributeName);
    }
    
    public String getTopParentClass(String className)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getTopParentClass(className);
    }
    
    public List<String> getImplementInterfaceList(String className)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getImplementInterfaceList(className);
    }
    
    public List<AttributeClassInformation> getInformationNotHidden(
            String className)
            throws ClassNotFoundException, ClassDuplicatedException
    {
        return classmodel.getInformationNotHidden(className);
    }
    
    public int getParticipantCount(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException
    {
        return interaction.getParticipantCount(interactionName);
    }
    
    public int getMessageCount(String interactionName)
            throws InteractionNotFoundException, InteractionDuplicatedException
    {
        return interaction.getMessageCount(interactionName);
    }
    
    public int getIncomingMessageCount(
            String interactionName, String lifelineName)
            throws
            InteractionNotFoundException,
            InteractionDuplicatedException,
            LifelineNotFoundException,
            LifelineDuplicatedException
    {
        return interaction.getIncomingMessageCount(
                interactionName,lifelineName);
    }
    
    public void checkForUml002() throws UmlRule002Exception
    {
        precheck.checkForUml002();
    }
    
    public void checkForUml008() throws UmlRule008Exception
    {
        precheck.checkForUml008();
    }
    
    public void checkForUml009() throws UmlRule009Exception
    {
        precheck.checkForUml009();
    }
    
    public int getStateCount(String stateMachineName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException
    {
        return statemachine.getStateCount(stateMachineName);
    }
    
    public int getTransitionCount(String stateMachineName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException
    {
        return statemachine.getTransitionCount(stateMachineName);
    }
    
    public int getSubsequentStateCount(
            String stateMachineName, String stateName)
            throws StateMachineNotFoundException,
            StateMachineDuplicatedException,
            StateNotFoundException,
            StateDuplicatedException
    {
        return statemachine.getSubsequentStateCount(stateMachineName,stateName);
    }
}
