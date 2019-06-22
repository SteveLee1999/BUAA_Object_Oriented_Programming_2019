package homeworkthree;

import java.util.ArrayList;

public class Product extends BasicTerm
{
    private ArrayList<BasicTerm> termlist = new ArrayList<>();
    
    Product(String s)
    {
        int top = 0;//栈顶
        int begin = 0;
        int i;
        for (i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '*' && top == 0)
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
    
    Product(ArrayList<BasicTerm> al)
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
        return new Product(this.termlist);
    }
    
    @Override
    public String toString()
    {
        String sterm = "";
        int i;
        int j;
        int flag;
        int top;
        int iszero = 0;
        simplification();
        if (termlist.size() == 0)
        {
            return "1";
        }
        else
        {
            for (i = 0;i < termlist.size();i++)
            {
                String s = termlist.get(i).toString();
                if (s.compareTo("0") == 0)
                {
                    iszero = 1;
                    break;
                }
            
                for (j = 0,flag = 0,top = 0;j < s.length();j++)
                {
                    if (s.charAt(j) == '(')
                    {
                        top++;
                    }
                    else if (s.charAt(j) == ')')
                    {
                        top--;
                    }
                    else if (top == 0 && s.charAt(j) == '+' &&
                             s.charAt(j - 1) != '*' && s.charAt(j - 1) != '^')
                    {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1)
                {
                    sterm += "(" + s + ")";
                }
                else
                {
                    sterm += s;
                }
            
                if (i != termlist.size() - 1)
                {
                    sterm += "*";
                }
            }
            if (iszero == 1)
            {
                return "0";
            }
            else
            {
                return sterm;
            }
        }
    }
    
    @Override
    public BasicTerm derivation()
    {
        ArrayList<BasicTerm> derivedlist = new ArrayList<>();
        ArrayList<BasicTerm> derivedproduct = new ArrayList<>();
        int i;
        int j;
        for (i = 0;i < termlist.size();i++)
        {
            for (j = 0;j < termlist.size();j++)
            {
                if (j == i)
                {
                    derivedproduct.add(termlist.get(j).derivation());
                }
                else
                {
                    derivedproduct.add(termlist.get(j));
                }
            }
            derivedlist.add(new Product(derivedproduct));
            derivedproduct.clear();
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
                if (termlist.get(i).toString().equals("1"))
                {
                    termlist.remove(i);
                    i--;
                }
            }
        }
    }
}
