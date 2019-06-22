package system;

import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.RailwaySystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MyRailwaySystem extends MyGraph implements RailwaySystem
{
    private HashMap<TwoPoint,Integer> edgemap;
    private HashMap<TwoPoint,Integer> distmap;
    private HashMap<TwoPoint,Integer> pricemap; // offset = 2
    private HashMap<TwoPoint,Integer> transfermap; // offset = 1
    private HashMap<TwoPoint,Integer> unpleasemap; // offset = 32
    private HashMap<Path,HashMap<TwoPoint,Integer>> pathpricemap;
    private HashMap<Path,HashMap<TwoPoint,Integer>> pathtransfermap;
    private HashMap<Path,HashMap<TwoPoint,Integer>> pathunpleasemap;
    private ArrayList<HashSet<Integer>> blocklist;
    
    public MyRailwaySystem()
    {
        edgemap = new HashMap<>();
        distmap = new HashMap<>();
        pricemap = new HashMap<>();
        unpleasemap = new HashMap<>();
        transfermap = new HashMap<>();
        pathpricemap = new HashMap<>();
        pathtransfermap = new HashMap<>();
        pathunpleasemap = new HashMap<>();
        blocklist = new ArrayList<>();
    }
    
    private HashMap<TwoPoint,Integer> Floyd(
            HashSet<Integer> nodeset,HashMap<TwoPoint,Integer> edgeset) {
        HashMap<TwoPoint,Integer> ret = new HashMap<>(edgeset);
        for (int k:nodeset) {
            for (int i:nodeset) {
                TwoPoint eik = new TwoPoint(i,k);
                if (!ret.containsKey(eik)) {
                    continue;
                }
                for (int j:nodeset) {
                    TwoPoint eij = new TwoPoint(i,j);
                    TwoPoint ejk = new TwoPoint(j,k);
                    if (ret.containsKey(ejk)) {
                        if (!ret.containsKey(eij) ||
                                ret.get(eij) > ret.get(eik) + ret.get(ejk)) {
                            ret.put(eij,ret.get(eik) + ret.get(ejk));
                        }
                    }
                }
            }
        }
        return ret;
    }
    
    @Override
    public int addPath(Path path) {
        if (path != null && path.isValid()) {
            if (containsPath(path)) {
                try {
                    return getPathId(path);
                }
                catch (PathNotFoundException e) {
                    System.out.println("Error in addPath!");
                    System.exit(0);
                }
            }
            else {
                addBlock(path);
                addNode(path);
                addEdgeMap(path);
                addPriceMap(path);
                addUnpleaseMap(path);
                addTransferMap(path);
                updateDistMap();
                
                return addThePath(path);
            }
        }
        return 0;
    }
    
    private void addBlock(Path path) {
        boolean isconnect = false;
        int include = -1;
        for (int i = 0;i < path.size();i++) {
            if (containsNode(path.getNode(i))) {
                isconnect = true;
                include = path.getNode(i);
            }
        }
        if (isconnect) {
            for (int i = 0;i < blocklist.size();i++) {
                if (blocklist.get(i).contains(include)) {
                    HashSet<Integer> sum = blocklist.get(i);
                    for (int j = 0;j < path.size();j++) {
                        sum.add(path.getNode(j));
                    }
                    for (int j = 0;j < blocklist.size();j++) {
                        if (blocklist.get(j) == sum) {
                            continue;
                        }
                        for (int k = 0;k < path.size();k++) {
                            if (blocklist.get(j).contains(path.getNode(k))) {
                                sum.addAll(blocklist.get(j));
                                blocklist.remove(j);
                                j--;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        else {
            HashSet<Integer> temp = new HashSet<>();
            for (int i = 0;i < path.size();i++) {
                temp.add(path.getNode(i));
            }
            blocklist.add(temp);
        }
    }
    
    private void addEdgeMap(Path path) {
        for (int i = 0;i < path.size() - 1;i++) {
            TwoPoint t = new TwoPoint(path.getNode(i),path.getNode(i + 1));
            if (edgemap.containsKey(t)) {
                int times = edgemap.get(t) + 1;
                edgemap.put(t,times);
            }
            else {
                edgemap.put(t,1);
            }
        }
    }
    
    private void addPriceMap(Path path) {
        HashMap<TwoPoint,Integer> newedge = new HashMap<>();
        HashSet<Integer> newnode = new HashSet<>();
        for (int i = 0;i < path.size() - 1;i++) {
            newedge.put(new TwoPoint(path.getNode(i),path.getNode(i + 1)),1);
        }
        for (int i = 0;i < path.size();i++) {
            newedge.put(new TwoPoint(path.getNode(i),path.getNode(i)),0);
            newnode.add(path.getNode(i));
        }
        newedge = Floyd(newnode,newedge);
        for (int i = 0;i < path.size();i++) {
            for (int j = 0;j < path.size();j++) {
                TwoPoint e = new TwoPoint(path.getNode(i),path.getNode(j));
                int ep = newedge.get(e) + 2;
                newedge.put(e,ep);
                if (!pricemap.containsKey(e) || pricemap.get(e) > ep) {
                    pricemap.put(e,ep);
                }
            }
        }
        pathpricemap.put(path,newedge);
        pricemap = Floyd(getNodemap(),pricemap);
    }
    
    private void addUnpleaseMap(Path path)
    {
        HashMap<TwoPoint,Integer> newedge = new HashMap<>();
        HashSet<Integer> newnode = new HashSet<>();
        for (int i = 0;i < path.size() - 1;i++)
        {
            int node1 = path.getNode(i);
            int node2 = path.getNode(i + 1);
            int temp = Math.max((node1 % 5 + 5) % 5,(node2 % 5 + 5) % 5);
            newedge.put(new TwoPoint(node1,node2),(int)Math.pow(4,temp));
        }
        for (int i = 0;i < path.size();i++)
        {
            int node = path.getNode(i);
            newedge.put(new TwoPoint(node,node),0);
            newnode.add(node);
        }
        newedge = Floyd(newnode,newedge);
        for (int i = 0;i < path.size();i++)
        {
            for (int j = 0;j < path.size();j++)
            {
                TwoPoint e = new TwoPoint(path.getNode(i),path.getNode(j));
                int ep = newedge.get(e) + 32;
                newedge.put(e,ep);
                if (!unpleasemap.containsKey(e) || unpleasemap.get(e) > ep)
                {
                    unpleasemap.put(e,ep);
                }
            }
        }
        pathunpleasemap.put(path,newedge);
        unpleasemap = Floyd(getNodemap(),unpleasemap);
    }
    
    private void addTransferMap(Path path)
    {
        HashMap<TwoPoint,Integer> newedge = new HashMap<>();
        for (int i = 0;i < path.size();i++)
        {
            newedge.put(new TwoPoint(path.getNode(i),path.getNode(i)),0);
            for (int j = i + 1;j < path.size();j++)
            {
                newedge.put(new TwoPoint(path.getNode(i),path.getNode(j)),1);
            }
        }
        pathtransfermap.put(path,newedge);
        transfermap.putAll(newedge);
        transfermap = Floyd(getNodemap(),transfermap);
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException
    {
        if (path == null || !path.isValid() || !containsPath(path))
        {
            throw new PathNotFoundException(path);
        }
        else
        {
            final int ret = remPath(path);
            pathpricemap.remove(path);
            pathtransfermap.remove(path);
            pathunpleasemap.remove(path);
            removeEdge(path);
            removeNode(path);
            updateBlockList();
            updateDistMap();
            updateValueMap();
            
            return ret;
        }
    }
    
    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException
    {
        if (!containsPathId(pathId))
        {
            throw new PathIdNotFoundException(pathId);
        }
        else
        {
            Path path = remId(pathId);
            pathpricemap.remove(path);
            pathtransfermap.remove(path);
            pathunpleasemap.remove(path);
            removeEdge(path);
            removeNode(path);
            updateDistMap();
            updateBlockList();
            updateValueMap();
        }
    }
    
    private void removeEdge(Path path)
    {
        for (int i = 0;i < path.size() - 1;i++)
        {
            TwoPoint t = new TwoPoint(path.getNode(i),path.getNode(i + 1));
            if (edgemap.get(t) == 1)
            {
                edgemap.remove(t);
            }
            else
            {
                int times = edgemap.get(t) - 1;
                edgemap.put(t,times);
            }
        }
    }
    
    private void updateBlockList()
    {
        blocklist.clear();
        HashSet<Integer> nodeset = getNodemap();
        while (!nodeset.isEmpty())
        {
            int start = nodeset.iterator().next();
            HashSet<Integer> newset = new HashSet<>();
            newset.add(start);
            HashSet<Integer> visit = new HashSet<>();
            visit.add(start);
            ArrayList<Integer> queue = new ArrayList<>();
            queue.add(start);
            while (!queue.isEmpty())
            {
                int front = queue.remove(0);
                visit.add(front);
                newset.add(front);
                for (Path p:getPmap())
                {
                    if (p.containsNode(front))
                    {
                        for (int i = 0;i < p.size();i++)
                        {
                            newset.add(p.getNode(i));
                            if (!visit.contains(p.getNode(i)))
                            {
                                queue.add(p.getNode(i));
                            }
                        }
                    }
                }
            }
            blocklist.add(newset);
            nodeset.removeAll(newset);
        }
    }
    
    private void updateDistMap()
    {
        distmap.clear();
        HashMap<TwoPoint,Integer> edge = new HashMap<>();
        for (TwoPoint t:edgemap.keySet())
        {
            edge.put(t,1);
        }
        distmap = Floyd(getNodemap(),edge);
    }
    
    private void updateValueMap() {
        pricemap.clear();
        unpleasemap.clear();
        transfermap.clear();
        int times = 0;
        TwoPoint e;
        for (Path path:getPmap()) {
            times++;
            if (times == 1) {
                pricemap.putAll(pathpricemap.get(path));
                unpleasemap.putAll(pathunpleasemap.get(path));
                transfermap.putAll(pathtransfermap.get(path));
            }
            else {
                for (int i = 0;i < path.size();i++) {
                    for (int j = 0;j < path.size();j++) {
                        e = new TwoPoint(path.getNode(i),path.getNode(j));
                        int ep = pathpricemap.get(path).get(e);
                        if (!pricemap.containsKey(e) || pricemap.get(e) > ep) {
                            pricemap.put(e,ep);
                        }
                        int eu = pathunpleasemap.get(path).get(e);
                        if (!unpleasemap.containsKey(e) || unpleasemap.get(e) > eu) {
                            unpleasemap.put(e,eu);
                        }
                        int et = pathtransfermap.get(path).get(e);
                        if (!transfermap.containsKey(e) || transfermap.get(e) > et) {
                            transfermap.put(e,et);
                        }
                    }
                }
                pricemap = Floyd(getNodemap(),pricemap);
                unpleasemap = Floyd(getNodemap(),unpleasemap);
                transfermap = Floyd(getNodemap(),transfermap);
            }
        }
    }
    
    @Override
    public boolean containsEdge(int fromNodeId, int toNodeId)
    {
        return (edgemap.containsKey(new TwoPoint(fromNodeId,toNodeId)));
    }
    
    @Override
    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException
    {
        if (!containsNode(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId))
        {
            throw new NodeIdNotFoundException(toNodeId);
        }
        else if (fromNodeId == toNodeId)
        {
            return true;
        }
        else
        {
            return (distmap.containsKey(new TwoPoint(fromNodeId,toNodeId)));
        }
    }
    
    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException,NodeNotConnectedException
    {
        if (!containsNode(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId))
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
            return distmap.get(new TwoPoint(fromNodeId,toNodeId));
        }
    }
    
    public int getLeastTicketPrice(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException
    {
        if (!containsNode(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId))
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
            return (pricemap.get(new TwoPoint(fromNodeId,toNodeId)) - 2);
        }
    }
    
    public int getLeastTransferCount(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException
    {
        if (!containsNode(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId))
        {
            throw new NodeIdNotFoundException(toNodeId);
        }
        else if (!isConnected(fromNodeId, toNodeId))
        {
            throw new NodeNotConnectedException(fromNodeId,toNodeId);
        }
        else if (fromNodeId == toNodeId)
        {
            return 0;
        }
        else
        {
            return (transfermap.get(new TwoPoint(fromNodeId,toNodeId)) - 1);
        }
    }
    
    public int getLeastUnpleasantValue(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException
    {
        if (!containsNode(fromNodeId))
        {
            throw new NodeIdNotFoundException(fromNodeId);
        }
        else if (!containsNode(toNodeId))
        {
            throw new NodeIdNotFoundException(toNodeId);
        }
        else if (!isConnected(fromNodeId, toNodeId))
        {
            throw new NodeNotConnectedException(fromNodeId,toNodeId);
        }
        else if (fromNodeId == toNodeId)
        {
            return 0;
        }
        else
        {
            return (unpleasemap.get(new TwoPoint(fromNodeId,toNodeId)) - 32);
        }
    }
    
    public int getConnectedBlockCount()
    {
        return blocklist.size();
    }
    
    public boolean containsPathSequence(Path[] pseq) {
        return true;
    }
    
    public boolean isConnectedInPathSequence(
            Path[] pseq, int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, PathNotFoundException {
        return true;
    }
    
    public int getTicketPrice(Path[] pseq, int fromNodeId, int toNodeId) {
        return 0;
    }
    
    public int getUnpleasantValue(Path path, int fromIndex, int toIndex) {
        return 0;
    }
    
    public int getUnpleasantValue(Path[] pseq, int fromNodeId, int toNodeId) {
        return 0;
    }
}
