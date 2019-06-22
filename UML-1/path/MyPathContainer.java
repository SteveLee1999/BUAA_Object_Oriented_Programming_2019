package path;

import com.oocourse.specs1.models.Path;
import com.oocourse.specs1.models.PathContainer;
import com.oocourse.specs1.models.PathIdNotFoundException;
import com.oocourse.specs1.models.PathNotFoundException;
import java.util.HashMap;

public class MyPathContainer implements PathContainer
{
    /* path为key，id为value */
    private HashMap<Path,Integer> pmap;
    private HashMap<Integer,Path> imap;
    private static HashMap<Integer,Integer> nodemap; // key是结点，node是个数
    private static int count;
    
    public MyPathContainer()
    {
        this.pmap = new HashMap<>();
        this.imap = new HashMap<>();
        nodemap = new HashMap<>();
        count = 0;
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
                
                pmap.put(path,++count);
                imap.put(count,path);
                return count;
            }
        }
        return 0;
    }
    
    public int removePath(Path path) throws PathNotFoundException
    {
        if (path == null || !path.isValid() || !containsPath(path))
        {
            throw new PathNotFoundException(path);
        }
        else
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
            
            int ret = pmap.remove(path);
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
    }
    
    public int getDistinctNodeCount()
    {
        return nodemap.size();
    }
}
