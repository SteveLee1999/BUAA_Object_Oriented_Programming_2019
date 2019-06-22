package system;

import com.oocourse.specs3.models.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class MyPath implements Path
{
    private int[] nodearray;
    private HashSet<Integer> nodeset = new HashSet<>(); // 优化查找
    private ArrayList<Integer> nodelist = new ArrayList<>(); // 迭代器
    
    public MyPath(int... nodeList) // 变长数组
    {
        this.nodearray = nodeList;
        int i;
        for (i = 0;i < nodearray.length;i++)
        {
            this.nodeset.add(nodeList[i]);
            this.nodelist.add(nodeList[i]);
        }
    }
    
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(nodearray);
    }
    
    public Iterator<Integer> iterator() // 迭代器
    {
        return nodelist.iterator();
    }
    
    public int compareTo(Path o) // this > o:1
    {
        int len;
        int ret;
        if (this.size() > o.size())
        {
            len = o.size();
            ret = 1;
        }
        else if (this.size() < o.size())
        {
            len = this.size();
            ret = -1;
        }
        else
        {
            len = o.size();
            ret = 0;
        }
        int i;
        for (i = 0;i < len;i++)
        {
            if (this.nodearray[i] < o.getNode(i))
            {
                return -1;
            }
            else if (this.nodearray[i] > o.getNode(i))
            {
                return 1;
            }
        }
        return ret;
    }
    
    public int size()
    {
        return nodearray.length;
    }
    
    public int getNode(int index)
    {
        if (index < 0 || index >= size())
        {
            System.out.println("index is out of bound in getNode!");
            throw new ArrayIndexOutOfBoundsException(); // 抛出异常
        }
        return nodearray[index];
    }
    
    public boolean containsNode(int node)
    {
        return (nodeset.contains(node));
    }
    
    public int getDistinctNodeCount()
    {
        return nodeset.size();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Path)
        {
            return (compareTo((Path )obj) == 0);
        }
        else
        {
            return false;
        }
    }
    
    public boolean isValid()
    {
        return (nodearray.length >= 2);
    }
    
    public int getUnpleasantValue(int nodeId)
    {
        if (containsNode(nodeId))
        {
            return (int)Math.pow(4,(nodeId % 5 + 5) % 5);
        }
        else
        {
            return 0;
        }
    }
}