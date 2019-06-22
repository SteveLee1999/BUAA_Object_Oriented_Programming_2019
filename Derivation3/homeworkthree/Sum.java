package homeworkthree;

import java.math.BigInteger;
import java.util.ArrayList;

public class Sum extends BasicTerm
{
    private ArrayList<BasicTerm> termlist = new ArrayList<>();
    
    public Sum(String s)
    {
        int top = 0;//栈顶
        int begin = 0;
        int i;
        for (i = 0; i < s.length(); i++)
        {
            if (top == 0 && s.charAt(i) == '+' &&
                    s.charAt(i - 1) != '*' && s.charAt(i - 1) != '^')
            {
                termlist.add(BuildTree.BuildBody(s.substring(begin,i)));
                begin = i + 1;
            }
            else if (s.charAt(i) == '(')
            {
                top++;
            }
            else if (s.charAt(i) == ')')
            {
                top--;
            }
        }
        termlist.add(BuildTree.BuildBody(s.substring(begin)));
    }
    
    public Sum(ArrayList<BasicTerm> al)
    {
        this.termlist.clear();
        int i;
        for (i = 0;i < al.size();i++)
        {
            this.termlist.add(al.get(i));
        }
    }
    
    @Override
    public BasicTerm clone()
    {
        return new Sum(this.termlist);
    }
    
    @Override
    public String toString()
    {
        StringBuilder termb = new StringBuilder("");
        int i;
        int j;
        simplification();
        if (termlist.size() == 0)
        {
            termb.append("0");
        }
        else
        {
            for (i = 0;i < termlist.size();i++)
            {
                termb.append(termlist.get(i).toString());
                if (i != termlist.size() - 1)
                {
                    termb.append("+");
                }
            }
        }
        return termb.toString();
    }
    
    @Override
    public BasicTerm derivation()
    {
        ArrayList<BasicTerm> derivedlist = new ArrayList<>();
        int i;
        for (i = 0;i < termlist.size();i++)
        {
            derivedlist.add(termlist.get(i).derivation());
        }
        return new Sum(derivedlist);
    }
    
    public boolean isOverstep()
    {
        int i;
        boolean result = true;
        for (i = 0;i < termlist.size();i++)
        {
            if (!termlist.get(i).isOverstep())
            {
                result = false;
                break;
            }
        }
        return result;
    }
    
    private void simplification()
    {
        int i;
        if (termlist.size() > 1)
        {
            for (i = 0;i < termlist.size();i++)
            {
                if (termlist.get(i).toString().equals("0"))
                {
                    termlist.remove(i);
                    i--;
                }
            }
        }
    }
}
