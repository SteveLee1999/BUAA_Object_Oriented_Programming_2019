package homeworkthree;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckInput
{
    private String input;
    
    void setInput(String input)
    {
        this.input = input;
    }
    
    String getInput()
    {
        return input;
    }
    
    void CheckAll()
    {
        CheckChar();
        CheckBlank();
        CheckBracket();
        CheckOperator();
        RecursiveCheck(input);
        Optimize();
    }
    
    private void RecursiveCheck(String s)
    {
        StringBuilder sb = new StringBuilder(s);
        int top;//栈顶
        int begin = -1;
        int istri;//是否是三角函数的括号
        int i;
        if (sb.length() == 0)
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        for (i = 0,top = 0,istri = 0;i < sb.length();i++)
        {
            if ((sb.charAt(i) == 's' || sb.charAt(i) == 'c') && (top == 0))
            {
                istri = 1;
            }
            
            if (sb.charAt(i) == '(')
            {
                top++;
                if (top == 1)
                {
                    begin = i;
                }
            }
            else if (sb.charAt(i) == ')')
            {
                top--;
                if (top == 0 && istri == 0)
                {
                    RecursiveCheck(sb.substring(begin + 1,i));
                    sb.replace(begin,i + 1,"y");
                    i = begin + 1;
                }
                else if (top == 0 && istri == 1)
                {
                    istri = 0;
                    if (sb.substring(begin + 1,i).compareTo("x") == 0)
                    {
                        continue;
                    }
                    else if (sb.substring(begin + 1,i).compareTo("sin(x)") == 0 ||
                            sb.substring(begin + 1,i).compareTo("cos(x)") == 0)
                    {
                        sb.replace(begin + 1,i,"y");
                        i = begin + 7;
                        continue;
                    }
                    else if (sb.charAt(begin + 1) != '(' || sb.charAt(i - 1) != ')')
                    {
                        System.out.print("WRONG FORMAT!");
                        System.exit(0);
                    }
                    RecursiveCheck(sb.substring(begin + 2,i - 1));
                    sb.replace(begin + 1,i,"y");//有可能是i!!!!
                    i = begin + 3;
                }//如此一来，sin(y)与y之类均为合法
            }
        }
        CheckLine(sb.toString());
    }
    
    private void CheckLine(String s)
    {
        String r0 = "(([+-]?sin\\([xy]\\)(\\^[+-]?\\d+)?)|"
                + "([+-]?cos\\([xy]\\)(\\^[+-]?\\d+)?)|"
                + "([+-]?x(\\^[+-]?\\d+)?)|([+-]?y)|([+-]?[+-]?\\d+))"
                + "((\\*sin\\([xy]\\)(\\^[+-]?\\d+)?)|"
                + "(\\*cos\\([xy]\\)(\\^[+-]?\\d+)?)|"
                + "(\\*x(\\^[+-]?\\d+)?)|(\\*y)|(\\*[+-]?\\d+))*";
        StringBuilder remain = new StringBuilder(s);
        StringBuilder rs = new StringBuilder();
        //先判断首项
        String r1 = "^[+-]?" + r0;
        Pattern p1 = Pattern.compile(r1);
        Matcher m1 = p1.matcher(remain);
        if (m1.find())
        {
            rs.append(remain.substring(m1.start(),m1.end()));
            remain.delete(m1.start(), m1.end());
            String r2 = "^[+-]" + r0;
            Pattern p2 = Pattern.compile(r2);
            Matcher m2 = p2.matcher(remain);
            while (m2.find())
            {
                rs.append(remain.substring(m2.start(), m2.end()));
                remain.delete(m2.start(), m2.end());
                m2 = p2.matcher(remain);
            }
        }
        if (rs.toString().equals("") || !remain.toString().equals(""))
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
    }
    
    private void CheckChar()
    {
        String rchar = "(([^\\dsincox+\\- \\t*()^])|(^[ \\t]*$))";//非法字符或者空输入
        Pattern pchar = Pattern.compile(rchar);
        Matcher mchar = pchar.matcher(input);
        if (mchar.find())
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
    }
    
    private void CheckBlank()
    {
        String rblank = "((s[ \\t]+in)|(si[ \\t]+n)|"
                + "(c[ \\t]+os)|(co[ \\t]+s)|"
                + "(\\d+[ \\t]+\\d+)|"
                + "([+-][ \\t]*[+-][ \\t]*[+-][ \\t]+\\d+)|"
                //+ + 4可以吗？
                + "(\\^[ \\t]*[+-][ \\t]+\\d+)|"
                + "(\\*[ \\t]*[+-][ \\t]+\\d+))";
        Pattern pblank = Pattern.compile(rblank);
        Matcher mblank = pblank.matcher(input);
        if (mblank.find())
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        input = input.replaceAll("\\s", "");
    }
    
    private void CheckBracket()
    {
        int i;
        int flag;
        int top = 0;
        for (i = 0,flag = 0;i < input.length();i++)
        {
            if (input.charAt(i) == '(')
            {
                top++;
            }
            else if (input.charAt(i) == ')')
            {
                top--;
            }
            if (top < 0)
            {
                flag = 1;
                break;
            }
        }
        if (top != 0)
        {
            flag = 1;
        }
        
        if (flag == 1)
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
    }
    
    private void CheckOperator()
    {
        String r1 = "(([+-][+-][+-][+-])|([+-][+-][+-][^\\d+]))";//对吗？？？？？
        Pattern p1 = Pattern.compile(r1);
        Matcher m1 = p1.matcher(input);
        if (m1.find())
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
    }
    
    private void Optimize()
    {
        input = input.replace("++", "+");
        input = input.replace("+-", "-");
        input = input.replace("-+", "-");
        input = input.replace("--", "+");
        input = input.replace("++", "+");
        input = input.replace("+-", "-");
        input = input.replace("-+", "-");
        input = input.replace("--", "+");
        int i;
        StringBuilder sbinput = new StringBuilder(input);
        for (i = 0;i < sbinput.length() - 1;i++)
        {
            if (sbinput.charAt(i) != '^' &&
                sbinput.charAt(i) != '*' &&
                sbinput.charAt(i) != '+' &&
                sbinput.charAt(i + 1) == '-')
            {
                sbinput.replace(i + 1,i + 2,"+-1*");
            }
            i += 4;
        }
    }
}

