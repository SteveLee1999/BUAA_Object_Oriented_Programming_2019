package graph;

import java.util.Arrays;

public class TwoPoint
{
    private int[] node = new int[2];
    
    TwoPoint(int a,int b) // 要求node[0] <= node[1]
    {
        if (a <= b)
        {
            node[0] = a;
            node[1] = b;
        }
        else
        {
            node[0] = b;
            node[1] = a;
        }
    }
    
    public int getnode(int index)
    {
        return node[index];
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof TwoPoint &&
                ((TwoPoint)obj).getnode(0) == this.node[0] &&
                ((TwoPoint)obj).getnode(1) == this.node[1]);
    }
    
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(node);
    }
}
