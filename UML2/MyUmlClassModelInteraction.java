import com.oocourse.uml2.interact.common.AttributeClassInformation;
import com.oocourse.uml2.interact.common.AttributeQueryType;
import com.oocourse.uml2.interact.common.OperationQueryType;
import com.oocourse.uml2.interact.exceptions.user.AttributeDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.AttributeNotFoundException;
import com.oocourse.uml2.interact.exceptions.user.ClassDuplicatedException;
import com.oocourse.uml2.interact.exceptions.user.ClassNotFoundException;
import com.oocourse.uml2.interact.format.UmlClassModelInteraction;
import com.oocourse.uml2.models.common.Direction;
import com.oocourse.uml2.models.common.ElementType;
import com.oocourse.uml2.models.common.Visibility;
import com.oocourse.uml2.models.elements.UmlAssociation;
import com.oocourse.uml2.models.elements.UmlAssociationEnd;
import com.oocourse.uml2.models.elements.UmlAttribute;
import com.oocourse.uml2.models.elements.UmlElement;
import com.oocourse.uml2.models.elements.UmlGeneralization;
import com.oocourse.uml2.models.elements.UmlInterfaceRealization;
import com.oocourse.uml2.models.elements.UmlOperation;
import com.oocourse.uml2.models.elements.UmlParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MyUmlClassModelInteraction implements UmlClassModelInteraction {
    private HashMap<String, String> classid2name = new HashMap<>();
    private HashMap<String, String> classname2id = new HashMap<>();
    private HashMap<String, String> classid2parentid = new HashMap<>();
    private HashMap<String, String> classid2topid = new HashMap<>();
    private HashSet<String> nameSet = new HashSet<>(); // 已有的类名
    private HashSet<String> dupClassName = new HashSet<>(); // 重复的类名
    private HashMap<String, HashMap<OperationQueryType, Integer>>
            classOperationCount = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> cpubOp = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> cproOp = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> cpriOp = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> cpackOp = new HashMap<>();
    private HashMap<String, String> attributeid2name = new HashMap<>();
    private HashMap<String, HashSet<String>> classDupAttr = new HashMap<>();
    private HashMap<String, Integer> classSelfAttributeCount = new HashMap<>();
    private HashMap<String, Integer> classAllAttributeCount = new HashMap<>();
    private HashMap<String, HashMap<String,Visibility>>
            classAttribute = new HashMap<>();
    private HashMap<String, HashMap<String,String>>
            classNotHiddenAttribute = new HashMap<>();
    private HashMap<String, String> endid2classid = new HashMap<>();
    private HashMap<String, Integer> classAssociationCount = new HashMap<>();
    private HashMap<String, HashSet<String>>
            classAssociationSet = new HashMap<>();
    private HashMap<String, String> interfaceid2name = new HashMap<>();
    private HashMap<String, HashSet<String>> classInterface = new HashMap<>();
    private HashMap<String, HashSet<String>> interAncestor = new HashMap<>();
    
    public MyUmlClassModelInteraction(HashSet<UmlElement> elements) {
        HashSet<String> retOperation = new HashSet<>(); // 有返回值的操作id集合
        HashSet<String> paraOperation = new HashSet<>(); // 有参数的操作id集合
        for (UmlElement i:elements) {
            if (i.getElementType() == ElementType.UML_CLASS) {
                classid2name.put(i.getId(),i.getName());
                classname2id.put(i.getName(),i.getId());
                if (nameSet.contains(i.getName())) {
                    dupClassName.add(i.getName());
                }
                nameSet.add(i.getName());
                HashMap<OperationQueryType, Integer> temp1 = new HashMap<>();
                temp1.put(OperationQueryType.NON_RETURN,0);
                temp1.put(OperationQueryType.RETURN,0);
                temp1.put(OperationQueryType.NON_PARAM,0);
                temp1.put(OperationQueryType.PARAM,0);
                temp1.put(OperationQueryType.ALL,0);
                classOperationCount.put(i.getName(),temp1);
                cpubOp.put(i.getName(),new HashMap<>());
                cpriOp.put(i.getName(),new HashMap<>());
                cproOp.put(i.getName(),new HashMap<>());
                cpackOp.put(i.getName(),new HashMap<>());
                classAssociationSet.put(i.getName(),new HashSet<>());
                classAssociationCount.put(i.getName(),0);
                classDupAttr.put(i.getName(),new HashSet<>());
                classAttribute.put(i.getName(),new HashMap<>());
                classSelfAttributeCount.put(i.getName(),0);
                classNotHiddenAttribute.put(i.getName(),new HashMap<>());
                classInterface.put(i.getName(),new HashSet<>());
            }
            if (i.getElementType() == ElementType.UML_INTERFACE) {
                interfaceid2name.put(i.getId(),i.getName());
                interAncestor.put(i.getId(),new HashSet<>());
            }
            if (i.getElementType() == ElementType.UML_ATTRIBUTE) {
                attributeid2name.put(i.getId(),i.getName());
            }
        }
        for (UmlElement i:elements) {
            switch (i.getElementType()) {
                case UML_OPERATION:
                    UmlOperation o = (UmlOperation) i;
                    if (!classid2name.keySet().contains(i.getParentId())) {
                        break;
                    }
                    String classname = classid2name.get(i.getParentId());
                    HashMap<String,Integer> map1;
                    int num4;
                    switch (o.getVisibility())
                    {
                        case PUBLIC:
                            map1 = cpubOp.get(classname);
                            if (map1.containsKey(i.getName())) {
                                num4 = map1.get(i.getName()) + 1;
                            } else {
                                num4 = 1;
                            }
                            map1.put(i.getName(),num4);
                            cpubOp.put(classname,map1);
                            break;
                        case PRIVATE:
                            map1 = cpriOp.get(classname);
                            if (map1.containsKey(i.getName())) {
                                num4 = map1.get(i.getName()) + 1;
                            } else {
                                num4 = 1;
                            }
                            map1.put(i.getName(),num4);
                            cpriOp.put(classname,map1);
                            break;
                        case PROTECTED:
                            map1 = cproOp.get(classname);
                            if (map1.containsKey(i.getName())) {
                                num4 = map1.get(i.getName()) + 1;
                            } else {
                                num4 = 1;
                            }
                            map1.put(i.getName(),num4);
                            cproOp.put(classname,map1);
                            break;
                        case PACKAGE:
                            map1 = cpackOp.get(classname);
                            if (map1.containsKey(i.getName())) {
                                num4 = map1.get(i.getName()) + 1;
                            } else {
                                num4 = 1;
                            }
                            map1.put(i.getName(),num4);
                            cpackOp.put(classname,map1);
                            break;
                        default:
                    }
                    break;
                case UML_PARAMETER:
                    UmlParameter p = (UmlParameter) i;
                    if (p.getDirection() == Direction.RETURN) {
                        retOperation.add(i.getParentId());
                    } else if (p.getDirection() == Direction.IN ||
                            p.getDirection() == Direction.OUT ||
                            p.getDirection() == Direction.INOUT) {
                        paraOperation.add(i.getParentId());
                    }
                    break;
                case UML_ATTRIBUTE:
                    if (!classid2name.keySet().contains(i.getParentId())) {
                        break;
                    }
                    String c = classid2name.get(i.getParentId());
                    int num3 = classSelfAttributeCount.get(c) + 1;
                    classSelfAttributeCount.put(c,num3);
                    HashMap<String,Visibility> map2 = classAttribute.get(c);
                    if (map2.keySet().contains(i.getName())) {
                        HashSet<String> set3 = classDupAttr.get(c);
                        set3.add(i.getName());
                        classDupAttr.put(c,set3);
                    }
                    UmlAttribute a = (UmlAttribute) i;
                    map2.put(i.getName(),a.getVisibility());
                    classAttribute.put(c,map2);
                    if (a.getVisibility() != Visibility.PRIVATE) {
                        HashMap<String,String> map3 =
                                classNotHiddenAttribute.get(c);
                        map3.put(i.getId(),c);
                        classNotHiddenAttribute.put(c,map3);
                    } break;
                case UML_ASSOCIATION_END:
                    String id = ((UmlAssociationEnd) i).getReference();
                    if (classid2name.keySet().contains(id)) {
                        endid2classid.put(i.getId(),id);
                    } break;
                case UML_INTERFACE_REALIZATION:
                    String sonid = ((UmlInterfaceRealization) i).getSource();
                    HashSet<String> set1 =
                            classInterface.get(classid2name.get(sonid));
                    set1.add(((UmlInterfaceRealization) i).getTarget());
                    classInterface.put(classid2name.get(sonid),set1);
                    break;
                case UML_GENERALIZATION:
                    UmlGeneralization g = (UmlGeneralization) i;
                    if (classid2name.containsKey(g.getSource())) {
                        classid2parentid.put(g.getSource(),g.getTarget());
                    } else {
                        HashSet<String> set;
                        set = interAncestor.get(g.getSource());
                        set.add(g.getTarget());
                        interAncestor.put(g.getSource(),set);
                    }
                    break;
                default:
            }
        }
        for (UmlElement i:elements) {
            switch (i.getElementType()) {
                case UML_OPERATION:
                    if (!classid2name.keySet().contains(i.getParentId())) {
                        break;
                    }
                    addClassOperationCount(i,OperationQueryType.ALL);
                    if (retOperation.contains(i.getId())) {
                        addClassOperationCount(i,OperationQueryType.RETURN);
                    } else {
                        addClassOperationCount(i,OperationQueryType.NON_RETURN);
                    }
                    if (paraOperation.contains(i.getId())) {
                        addClassOperationCount(i,OperationQueryType.PARAM);
                    } else {
                        addClassOperationCount(i,OperationQueryType.NON_PARAM);
                    }
                    break;
                case UML_ASSOCIATION:
                    UmlAssociation a = (UmlAssociation)i;
                    String id1 = null;
                    String name1 = null;
                    String id2 = null;
                    String name2 = null;
                    if (endid2classid.keySet().contains(a.getEnd1())) {
                        id1 = endid2classid.get(a.getEnd1());
                        name1 = classid2name.get(id1);
                        int num1 = classAssociationCount.get(name1) + 1;
                        classAssociationCount.put(name1,num1);
                    }
                    if (endid2classid.keySet().contains(a.getEnd2())) {
                        id2 = endid2classid.get(a.getEnd2());
                        name2 = classid2name.get(id2);
                        int num2 = classAssociationCount.get(name2) + 1;
                        classAssociationCount.put(name2,num2);
                    }
                    if (endid2classid.keySet().contains(a.getEnd1()) &&
                            endid2classid.keySet().contains(a.getEnd2())) {
                        HashSet<String> s1 = classAssociationSet.get(name1);
                        s1.add(id2);
                        classAssociationSet.put(name1,s1);
                        HashSet<String> s2 = classAssociationSet.get(name2);
                        s2.add(id1);
                        classAssociationSet.put(name2,s2);
                    }
                    break;
                default:
            }
        }
        buildAncestorMap();
    }
    
    private void addClassOperationCount(UmlElement i, OperationQueryType type) {
        HashMap<OperationQueryType, Integer> temp
                = classOperationCount.get(classid2name.get(i.getParentId()));
        int num = temp.get(type) + 1;
        temp.put(type,num);
        classOperationCount.put(classid2name.get(i.getParentId()),temp);
    }
    
    private HashSet<String> visited = new HashSet<>();
    
    private void buildAncestorMap() {
        visited.clear();
        for (String i:interAncestor.keySet()) {
            if (!visited.contains(i)) {
                interface_visit(i);
            }
        }
        visited.clear();
        for (String i:classid2name.keySet()) {
            if (!visited.contains(i)) {
                class_visit(i);
            }
        }
    }
    
    private void interface_visit(String i) {
        visited.add(i);
        if (interAncestor.get(i).isEmpty()) {
            return;
        }
        HashSet<String> set1 = interAncestor.get(i);
        HashSet<String> temp = new HashSet<>();
        for (String parentid:set1) {
            if (!visited.contains(parentid)) {
                interface_visit(parentid);
            }
            HashSet<String> set2 = interAncestor.get(parentid);
            temp.addAll(set2);
        }
        set1.addAll(temp);
        interAncestor.put(i,set1);
    }
    
    private void class_visit(String i) {
        visited.add(i);
        String me = classid2name.get(i); // name of this class
        HashSet<String> set5 = classInterface.get(me);
        HashSet<String> temp = new HashSet<>(set5);
        for (String interfaceid:set5) {
            temp.addAll(interAncestor.get(interfaceid));
        }
        set5.addAll(temp);
        classInterface.put(me,set5);
        if (!classid2parentid.containsKey(i)) {
            classid2topid.put(i,i);
            
            int num = classSelfAttributeCount.get(me);
            classAllAttributeCount.put(me,num);
            return;
        }
        if (!visited.contains(classid2parentid.get(i))) {
            class_visit(classid2parentid.get(i));
        }
        String parent = classid2name.get(classid2parentid.get(i));
        HashSet<String> set6 = classInterface.get(parent);
        temp.addAll(set6);
        for (String interfaceid:set6) {
            temp.addAll(interAncestor.get(interfaceid));
        }
        set5.addAll(temp);
        classInterface.put(me,set5);
        String topid = classid2topid.get(classid2parentid.get(i));
        classid2topid.put(i,topid);
        int num1 = classSelfAttributeCount.get(me);
        int num2 = classAllAttributeCount.getOrDefault(parent,0);
        classAllAttributeCount.put(me,num1 + num2);
        HashMap<String,Visibility> map1 = classAttribute.get(me);
        HashMap<String,Visibility> map2 = classAttribute.get(parent);
        HashSet<String> set0 = classDupAttr.get(me);
        for (String s:map2.keySet()) {
            if (map1.keySet().contains(s)) {
                set0.add(s);
            }
        }
        set0.addAll(classDupAttr.get(parent));
        classDupAttr.put(me,set0);
        map1.putAll(map2);
        classAttribute.put(me,map1);
        HashMap<String,String> map3 = classNotHiddenAttribute.get(me);
        HashMap<String,String> map4 = classNotHiddenAttribute.get(parent);
        map3.putAll(map4);
        classNotHiddenAttribute.put(me,map3);
        num1 = classAssociationCount.get(me);
        num2 = classAssociationCount.get(parent);
        classAssociationCount.put(me,num1 + num2);
        HashSet<String> set1 = classAssociationSet.get(me);
        HashSet<String> set2 = classAssociationSet.get(parent);
        set1.addAll(set2);
        classAssociationSet.put(me,set1);
    }
    
    public int getClassCount() {
        return classid2name.size();
    }
    
    public int getClassOperationCount(
            String className, OperationQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            return classOperationCount.get(className).get(queryType);
        }
    }
    
    public int getClassAttributeCount(
            String className, AttributeQueryType queryType)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else if (queryType == AttributeQueryType.SELF_ONLY) {
            return classSelfAttributeCount.get(className);
        } else {
            return classAllAttributeCount.get(className);
        }
    }
    
    public int getClassAssociationCount(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            return classAssociationCount.get(className);
        }
    }
    
    public List<String> getClassAssociatedClassList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            ArrayList<String> ret = new ArrayList<>();
            for (String s:classAssociationSet.get(className)) {
                ret.add(classid2name.get(s));
            }
            return ret;
        }
    }
    
    public Map<Visibility, Integer> getClassOperationVisibility(
            String className, String operationName)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            HashMap<Visibility,Integer> ret = new HashMap<>();
            HashMap<String,Integer> map = cpubOp.get(className);
            ret.put(Visibility.PUBLIC,map.getOrDefault(operationName,0));
            map = cproOp.get(className);
            ret.put(Visibility.PROTECTED,map.getOrDefault(operationName,0));
            map = cpriOp.get(className);
            ret.put(Visibility.PRIVATE,map.getOrDefault(operationName,0));
            map = cpackOp.get(className);
            ret.put(Visibility.PACKAGE,map.getOrDefault(operationName,0));
            return ret;
        }
    }
    
    public Visibility getClassAttributeVisibility(
            String className, String attri)
            throws ClassNotFoundException, ClassDuplicatedException,
            AttributeNotFoundException, AttributeDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else if (!classAttribute.get(className).keySet().contains(attri)) {
            throw new AttributeNotFoundException(className,attri);
        } else if (classDupAttr.get(className).contains(attri)) {
            throw new AttributeDuplicatedException(className,attri);
        } else {
            return classAttribute.get(className).get(attri);
        }
    }
    
    public String getTopParentClass(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className))
        {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            String topid = classid2topid.get(classname2id.get(className));
            return classid2name.get(topid);
        }
    }
    
    public List<String> getImplementInterfaceList(String className)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(className)) {
            throw new ClassNotFoundException(className);
        } else if (dupClassName.contains(className)) {
            throw new ClassDuplicatedException(className);
        } else {
            ArrayList<String> ret = new ArrayList<>();
            for (String id:classInterface.get(className)) {
                String name = interfaceid2name.get(id);
                ret.add(name);
            }
            return ret;
        }
    }
    
    public List<AttributeClassInformation> getInformationNotHidden(String cname)
            throws ClassNotFoundException, ClassDuplicatedException {
        if (!nameSet.contains(cname)) {
            throw new ClassNotFoundException(cname);
        } else if (dupClassName.contains(cname)) {
            throw new ClassDuplicatedException(cname);
        } else {
            ArrayList<AttributeClassInformation> ret = new ArrayList<>();
            for (String id:classNotHiddenAttribute.get(cname).keySet()) {
                String name = attributeid2name.get(id);
                String c = classNotHiddenAttribute.get(cname).get(id);
                ret.add(new AttributeClassInformation(name,c));
            }
            return ret;
        }
    }
}