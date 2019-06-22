import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.exceptions.user.UmlRule002Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule008Exception;
import com.oocourse.uml2.interact.exceptions.user.UmlRule009Exception;
import com.oocourse.uml2.interact.format.UmlStandardPreCheck;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlClassOrInterface;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyUmlStandardPreCheck implements UmlStandardPreCheck
{
    private HashMap<String, String> endid2classid = new HashMap<>();
    private HashMap<String, String> endid2name = new HashMap<>();
    private HashMap<String, String> classid2name = new HashMap<>();
    private HashMap<String, String> interfaceid2name = new HashMap<>();
    private HashMap<String, UmlClassOrInterface>
            classorinterid2uml = new HashMap<>();
    
    /* <class id, name set> */
    private HashMap<String, HashSet<String>> class2nameset = new HashMap<>();
    private HashMap<String, HashSet<String>> class2dupname = new HashMap<>();
    
    /* 继承关系的有向边 (class or interface id) */
    private HashMap<String, HashSet<String>> general = new HashMap<>();
    
    /* 继承关系或者实现关系的有向边 (class or interface id) */
    private HashMap<String, HashSet<String>> edgeset = new HashMap<>();
    
    /* 存放环路继承或重复实现 (class or interface id) */
    private HashSet<String> circlegeneralset = new HashSet<>();
    private HashSet<String> dupgeneralset = new HashSet<>();
    
    MyUmlStandardPreCheck(HashSet<UmlElement> elements)
    {
        for (UmlElement e:elements)
        {
            HashSet<String> set;
            switch (e.getElementType())
            {
                case UML_CLASS:
                    classid2name.put(e.getId(),e.getName());
                    classorinterid2uml.put(e.getId(),(UmlClassOrInterface)e);
                    break;
                case UML_INTERFACE:
                    interfaceid2name.put(e.getId(),e.getName());
                    classorinterid2uml.put(e.getId(),(UmlClassOrInterface)e);
                    break;
                case UML_ASSOCIATION_END:
                    UmlAssociationEnd end = (UmlAssociationEnd)e;
                    endid2classid.put(e.getId(),end.getReference());
                    endid2name.put(e.getId(),e.getName());
                    break;
                case UML_ATTRIBUTE:
                    if (e.getName() != null)
                    {
                        set = class2nameset.getOrDefault(
                                e.getParentId(),new HashSet<>());
                        if (set.contains(e.getName())) // duplicate name
                        {
                            HashSet<String> set1 = class2dupname.getOrDefault(
                                    e.getParentId(),new HashSet<>());
                            set1.add(e.getName());
                            class2dupname.put(e.getParentId(),set1);
                        }
                        set.add(e.getName());
                        class2nameset.put(e.getParentId(),set);
                    }
                    break;
                case UML_GENERALIZATION:
                    UmlGeneralization g = (UmlGeneralization) e;
                    set = general.getOrDefault(g.getSource(),new HashSet<>());
                    set.add(g.getTarget());
                    general.put(g.getSource(),set);
    
                    set = edgeset.getOrDefault(g.getSource(),new HashSet<>());
                    if (set.contains(g.getTarget()))
                    {
                        dupgeneralset.add(g.getSource());
                    }
                    set.add(g.getTarget());
                    edgeset.put(g.getSource(),set);
                    break;
                case UML_INTERFACE_REALIZATION:
                    UmlInterfaceRealization ir = (UmlInterfaceRealization) e;
                    set = edgeset.getOrDefault(ir.getSource(),new HashSet<>());
                    if (set.contains(ir.getTarget()))
                    {
                        dupgeneralset.add(ir.getSource());
                    }
                    set.add(ir.getTarget());
                    edgeset.put(ir.getSource(),set);
                    break;
                default:
            }
        }
        for (UmlElement e:elements)
        {
            if (e.getElementType() == ElementType.UML_ASSOCIATION)
            {
                UmlAssociation a = (UmlAssociation)e;
                HashSet<String> set;
                HashSet<String> set1;
                String classid;
    
                String endname = endid2name.get(a.getEnd1());
                if (endname != null)
                {
                    classid = endid2classid.get(a.getEnd2());
                    set = class2nameset.getOrDefault(classid,new HashSet<>());
                    if (set.contains(endname)) // duplicate name
                    {
                        set1 = class2dupname.getOrDefault(
                                classid,new HashSet<>());
                        set1.add(endname);
                        class2dupname.put(classid,set1);
                    }
                    set.add(endname);
                    class2nameset.put(classid,set);
                }
    
                endname = endid2name.get(a.getEnd2());
                if (endname != null)
                {
                    classid = endid2classid.get(a.getEnd1());
                    set = class2nameset.getOrDefault(classid,new HashSet<>());
                    if (set.contains(endname))
                    {
                        set1 = class2dupname.getOrDefault(
                                classid,new HashSet<>());
                        set1.add(endname);
                        class2dupname.put(classid,set1);
                    }
                    set.add(endname);
                    class2nameset.put(classid,set);
                }
            }
        }
    }
    
    public void checkForUml002() throws UmlRule002Exception
    {
        HashSet<AttributeClassInformation> ret = new HashSet<>();
        for (String classid:class2dupname.keySet())
        {
            String classname = classid2name.get(classid);
            for (String name:class2dupname.get(classid))
            {
                ret.add(new AttributeClassInformation(name,classname));
            }
        }
        if (!ret.isEmpty())
        {
            throw new UmlRule002Exception(ret);
        }
    }
    
    private HashSet<String> visited = new HashSet<>();
    private ArrayList<String> maincircle = new ArrayList<>();
    
    private void visit008(String mainid,String id)
    {
        if (general.containsKey(id))
        {
            for (String s:general.get(id))
            {
                maincircle.add(s);
                if (s.compareTo(mainid) == 0)
                {
                    circlegeneralset.addAll(maincircle);
                }
                if (!visited.contains(s))
                {
                    visited.add(s);
                    visit008(mainid,s);
                }
                maincircle.remove(s);
            }
        }
    }
    
    public void checkForUml008() throws UmlRule008Exception
    {
        HashSet<String> idset = new HashSet<>();
        idset.addAll(classid2name.keySet());
        idset.addAll(interfaceid2name.keySet());
        
        for (String id:idset)
        {
            visited.clear();
            visited.add(id);
            maincircle.clear();
            maincircle.add(id);
            visit008(id,id);
        }
        
        if (!circlegeneralset.isEmpty())
        {
            HashSet<UmlClassOrInterface> ret = new HashSet<>();
            for (String s:circlegeneralset)
            {
                ret.add(classorinterid2uml.get(s));
            }
            throw new UmlRule008Exception(ret);
        }
    }
    
    private HashMap<String, HashSet<String>> arriveset = new HashMap<>();
    
    private void visit009(String mainid, String id)
    {
        HashSet<String> temp = new HashSet<>(
                edgeset.getOrDefault(id,new HashSet<>()));
        HashSet<String> set = new HashSet<>(
                edgeset.getOrDefault(id,new HashSet<>()));
        for (String parent:temp) // 继承或者实现
        {
            if (visited.contains(parent))
            {
                continue;
            }
            visited.add(parent);
            visit009(mainid,parent);
            if (dupgeneralset.contains(parent))
            {
                dupgeneralset.add(mainid);
            }
            for (String s:arriveset.get(parent))
            {
                if (set.contains(s))
                {
                    dupgeneralset.add(mainid);
                }
            }
            if (arriveset.containsKey(parent))
            {
                set.addAll(arriveset.get(parent));
            }
        }
        arriveset.put(id,set);
    }
    
    public void checkForUml009() throws UmlRule009Exception
    {
        HashSet<String> idset = new HashSet<>();
        idset.addAll(classid2name.keySet());
        idset.addAll(interfaceid2name.keySet());
        
        for (String id:idset)
        {
            visited.clear();
            visited.add(id);
            visit009(id,id);
        }
    
        if (!dupgeneralset.isEmpty())
        {
            HashSet<UmlClassOrInterface> ret = new HashSet<>();
            for (String s:dupgeneralset)
            {
                ret.add(classorinterid2uml.get(s));
            }
            throw new UmlRule009Exception(ret);
        }
    }
}
