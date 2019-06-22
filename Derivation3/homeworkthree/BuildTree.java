package homeworkthree;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildTree
{
    static BasicTerm BuildBody(String s)
    {
        String str = new String(s);
        int top;
        int i;
        String rnum = "[+-]?\\d+$";
        Pattern pnum = Pattern.compile(rnum);
        Matcher mnum;
        BigInteger degree;
        if (s.charAt(0) == '+')
        {
            str = s.substring(1);
        }
        while (str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')')
        {
            str = str.substring(1,str.length() - 1);
        }
        for (top = 0,i = 0;i < str.length();i++)
        {
            if (top == 0 && str.charAt(i) == '+' &&
                str.charAt(i - 1) != '*' && str.charAt(i - 1) != '^')
            {
                return new Sum(str);
            }
            else if (str.charAt(i) == '(')
            {
                top++;
            }
            else if (str.charAt(i) == ')')
            {
                top--;
            }
        }
        for (top = 0,i = 0;i < str.length();i++)
        {
            if (top == 0 && str.charAt(i) == '*')
            {
                return new Product(str);
            }
            else if (str.charAt(i) == '(')
            {
                top++;
            }
            else if (str.charAt(i) == ')')
            {
                top--;
            }
        }
        if (str.charAt(0) == 's')
        {
            mnum = pnum.matcher(str);
            if (mnum.find())
            {
                for (top = 0,i = 0;i < str.length();i++)
                {
                    if (str.charAt(i) == '(')
                    {
                        top++;
                    }
                    else if (str.charAt(i) == ')')
                    {
                        top--;
                        if (top == 0)
                        {
                            break;
                        }
                    }
                }
                return new Sinnest(str.substring(4,i),new BigInteger(mnum.group()));
            }
            else
            {
                return new Sinnest(str.substring(4,str.length() - 1),BigInteger.ONE);
            }
        }
        else if (str.charAt(0) == 'c')
        {
            mnum = pnum.matcher(str);
            if (mnum.find())
            {
                for (top = 0,i = 0;i < str.length();i++)
                {
                    if (str.charAt(i) == '(')
                    {
                        top++;
                    }
                    else if (str.charAt(i) == ')')
                    {
                        top--;
                        if (top == 0)
                        {
                            break;
                        }
                    }
                }
                return new Cosnest(str.substring(4,i),new BigInteger(mnum.group()));
            }
            else
            {
                return new Cosnest(str.substring(4,str.length() - 1),BigInteger.ONE);
            }
        }
        else if (str.charAt(0) == 'x')
        {
            return new Power(str);
        }
        else //常数
        {
            return new Constant(str);
        }
    }
}
