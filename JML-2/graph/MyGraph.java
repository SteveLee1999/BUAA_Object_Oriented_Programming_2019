package graph;

import com.oocourse.specs2.models.Graph;
import com.oocourse.specs2.models.NodeIdNotFoundException;
import com.oocourse.specs2.models.NodeNotConnectedException;
import com.oocourse.specs2.models.Path;
import com.oocourse.specs2.models.PathIdNotFoundException;
import com.oocourse.specs2.models.PathNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyGraph implements Graph
{
    private HashMap<Path,Integer> pmap;
    private HashMap<Integer,Path> imap;
    private HashMap<Integer,Integer> nodemap;
    private HashMap<Integer,HashSet<Integer>> edgemap;
    private HashMap<TwoPoint,Integer> edgenum; // 边的条数
    private HashMap<TwoPoint,ArrayList<Integer>> roadmap; // 最短路径
    private int count;
    
    public MyGraph()
    {
        this.pmap = new HashMap<>();
        this.imap = new HashMap<>();
        nodemap = new HashMap<>();
        edgemap = new HashMap<>();
        edgenum = new HashMap<>();
        roadmap = new HashMap<>();
        count = 0;
    }
    
    private void showGraph()
    {
        for (int i:nodemap.keySet())
        {
            System.out.println(String.format("%d有%d个",i,nodemap.get(i)));
        }
        for (int i:edgemap.keySet())
        {
            System.out.print(String.format("%d的相邻点：",i));
            for (int j:edgemap.get(i))
            {
                System.out.print(String.format("%d ",j));
            }
            System.out.println();
        }
        for (TwoPoint i:edgenum.keySet())
        {
            System.out.println(String.format(
                    "%d-%d有%d条",
                    i.getnode(0),i.getnode(1),edgenum.get(i)));
        }
        for (TwoPoint i:roadmap.keySet())
        {
            System.out.print(String.format(
                    "%d - %d最短路径:", i.getnode(0),i.getnode(1)));
            for (int j = 0;j < roadmap.get(i).size();j++)
            {
                System.out.print(String.format("%d ",roadmap.get(i).get(j)));
            }
            System.out.println();
        }
    }
    
    public int size()
    {
        return pmap.size();
    }
    
    public boolean containsPath(Path path)
    {
        if (path == null)
        {
            System.out.println("null in containsPath!");
            throw new NullPointerException();
        }
        else
        {
            return (pmap.containsKey(path));
        }
    }
    
    public boolean containsPathId(int pathId)
    {
        return (imap.containsKey(pathId));
    }
    
    public Path getPathById(int pathId) throws PathIdNotFoundException
    {
        if (!containsPathId(pathId))
        {
            throw new PathIdNotFoundException(pathId);
        }
        else
        {
            return imap.get(pathId);
        }
    }
    
    public int getPathId(Path path) throws PathNotFoundException
    {
        if (path == null || !path.isValid() || !containsPath(path))
        {
            throw new PathNotFoundException(path);
        }
        else
        {
            return pmap.get(path);
        }
    }
    
    public int addPath(Path path)
    {
        if (path != null && path.isValid())
        {
            if (containsPath(path))
            {
                try
                {
                    return getPathId(path);
                }
                catch (PathNotFoundException e)
                {
                    System.out.println("Error in addPath!");
                    System.exit(0);
                }
            }
            else
            {
                addNodeMap(path);
                addEdgeMap(path);
                updateRoadMap();
                //showGraph();
                
                pmap.put(path,++count);
                imap.put(count,path);
                return count;
            }
        }
        return 0;
    }
    
    private void addNodeMap(Path path)
    {
        int i;
        int node;
        int times;
        for (i = 0;i < path.size();i++)
        {
            node = path.getNode(i);
            if (nodemap.containsKey(node))
            {
                times = nodemap.get(node) + 1;
                nodemap.remove(node);
                nodemap.put(node,times);
            }
            else
            {
                nodemap.put(node,1);
            }
        }
    }
    
    private void addEdgeMap(Path path)
    {
        int i;
        int node1;
        int node2;
        HashSet<Integer> temp;
        for (i = 0;i < path.size() - 1;i++)
        {
            node1 = path.getNode(i);
            node2 = path.getNode(i + 1);
            TwoPoint t = new TwoPoint(node1,node2);
            
            if (edgenum.containsKey(t))
            {
                int times = edgenum.get(t) + 1;
                edgenum.put(t,times);
            }
            else
            {
                edgenum.put(t,1);
                if (edgemap.containsKey(node1))
                {
                    temp = edgemap.get(node1);
                }
                else
                {
                    temp = new HashSet<>();
                }
                temp.add(node2);
                edgemap.put(node1,temp); // key相同则会覆盖
                if (edgemap.containsKey(node2))
                {
                    temp = edgemap.get(node2);
                }
                else
                {
                    temp = new HashSet<>();
                }
                temp.add(node1);
                edgemap.put(node2,temp);
            }
        }
    }
    
    private void updateRoadMap()
    {
        roadmap.clear();
        for (int node:nodemap.keySet()) // 遍历点集
        {
            dijkstra(node); // dijkstra算法：更新node的所有最短路径
        }
    }
    
    private void dijkstra(int node)
    {
        HashMap<Integer,Boolean> s = new HashMap<>(); // 最短路径是否找到
        HashMap<Integer,Integer> dist = new HashMap<>(); // 最短路径长度
        HashMap<Integer,ArrayList<Integer>> path = new HashMap<>(); // 最短路径
        for (int i:nodemap.keySet())
        {
            s.put(i,false);
            dist.put(i,-1); // -1表示暂未找到
            ArrayList<Integer> tmp = new ArrayList<>();
            tmp.add(node);
            path.put(i,tmp);
        }
        s.put(node,true);
        dist.put(node,0);
        for (int i:edgemap.get(node))
        {
            dist.put(i,1);
        }
        while (true)
        {
            int minlen = -1;
            int u = 0;
            for (int i:nodemap.keySet())
            {
                if (!s.get(i) && dist.get(i) != -1 &&
                        (minlen == -1 || dist.get(i) < minlen))
                {
                    minlen = dist.get(i);
                    u = i;
                }
            }
            if (minlen == -1)
            {
                break;
            }
            s.put(u,true);
            ArrayList<Integer> temp1 = new ArrayList<>(path.get(u));
            temp1.add(u);
            path.put(u,temp1);
            for (int i:edgemap.get(u))
            {
                if (!s.get(i) &&
                        (dist.get(i) == -1 || minlen + 1 < dist.get(i)))
                {
                    dist.put(i,minlen + 1);
                    path.put(i,new ArrayList<>(path.get(u)));
                    /* 注意这里在不确定一定是最短路径时候，先不把终点加入 */
                }
            }
        }
        for (int i:nodemap.keySet())
        {
            if (s.get(i))
            {
                roadmap.put(new TwoPoint(node,i),path.get(i));
            }
        }
    }
    
    public int removePath(Path path) throws PathNotFoundException
    {
        if (path == null || !path.isValid() || !containsPath(path))
        {
            throw new PathNotFoundException(path);
        }
        else
        {
            removeNodeMap(path);
            removeEdgeMap(path);
            updateRoadMap();
            //showGraph();
            
            int ret = pmap.remove(path); // 取得id
            imap.remove(ret);
            return ret;
        }
    }
    
    public void removePathById(int pathId) throws PathIdNotFoundException
    {
        if (!containsPathId(pathId))
        {
            throw new PathIdNotFoundException(pathId);
        }
        else
        {
            Path path = imap.remove(pathId);
            pmap.remove(path);
            
            removeNodeMap(path);
            removeEdgeMap(path);
            updateRoadMap();
            //showGraph();
        }
    }
    
    private void removeNodeMap(Path path)
    {
        int i;
        int node;
        int times;
        for (i = 0;i < path.size();i++)
        {
            node = path.getNode(i);
            times = nodemap.get(node) - 1;
            nodemap.remove(node);
            if (times >= 1)
            {
                nodemap.put(node,times);
            }
        }
    }
    
    private void removeEdgeMap(Path path)
    {
        int i;
        int node1;
        int node2;
        for (i = 0;i < path.size() - 1;i++)
        {
            node1 = path.getNode(i);
            node2 = path.getNode(i + 1);
            TwoPoint t = new TwoPoint(node1,node2);
            if (edgenum.get(t) == 1)
            {
                edgenum.remove(t);
                HashSet<Integer> h = edgemap.get(node1);
                if (h.size() == 1)
                {
                    edgemap.remove(node1);
                }
                else
                {
                    h.remove(node2);
                    edgemap.put(node1,h);
                }
                if (node1 != node2)
                {
                    h = edgemap.get(node2);
                    if (h.size() == 1)
                    {
                        edgemap.remove(node2);
                    }
                    else
                    {
                        h.remove(node1);
                        edgemap.put(node2,h);
                    }
                }
            }
            else
            {
                int times = edgenum.get(t) - 1;
                edgenum.put(t,times);
            }
        }
    }
    
    public int getDistinctNodeCount()
    {
        return nodemap.size();
    }
    
    public boolean containsNode(int nodeId)
    {
        return (nodemap.containsKey(nodeId));
    }
    
    public boolean containsEdge(int fromNodeId, int toNodeId)
    {
        return (edgenum.containsKey(new TwoPoint(fromNodeId,toNodeId)));
    }
    
    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException
    {
        if (!nodemap.containsKey(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!nodemap.containsKey(toNodeId))
        {
            throw new NodeIdNotFoundException(toNodeId);
        }
        else if (fromNodeId == toNodeId)
        {
            return true;
        }
        else
        {
            return (roadmap.containsKey(new TwoPoint(fromNodeId,toNodeId)));
        }
    }
    
    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException,NodeNotConnectedException
    {
        if (!nodemap.containsKey(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!nodemap.containsKey(toNodeId))
        {
            throw new NodeIdNotFoundException(toNodeId);
        }
        else if (!isConnected(fromNodeId,toNodeId))
        {
            throw new NodeNotConnectedException(fromNodeId,toNodeId);
        }
        else if (fromNodeId == toNodeId)
        {
            return 0;
        }
        else
        {
            return (roadmap.get(new TwoPoint(fromNodeId,toNodeId)).size() - 1);
        }
    }
}
