package function;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DealString
{
    DealString(StringBuffer sb)
    //判断输入是否合法，如合法则返回处理后的字符串
    {
        String s = sb.toString();
        StringBuilder rs = new StringBuilder();
        String r0 = "(([+-]?[ \\t]*sin[ \\t]*\\([ \\t]*x[ \\t]*\\)[ \\t]*"
                + "(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([+-]?[ \\t]*cos[ \\t]*\\([ \\t]*x[ \\t]*\\)[ \\t]*"
                + "(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([+-]?[ \\t]*x[ \\t]*(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([+-]?[ \\t]*[+-]?\\d+))"
                + "(([ \\t]*\\*[ \\t]*sin[ \\t]*\\([ \\t]*x[ \\t]*\\)[ \\t]*"
                + "(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([ \\t]*\\*[ \\t]*cos[ \\t]*\\([ \\t]*x[ \\t]*\\)[ \\t]*"
                + "(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([ \\t]*\\*[ \\t]*x[ \\t]*(\\^[ \\t]*[+-]?\\d+)?)|"
                + "([ \\t]*\\*[ \\t]*[+-]?\\d+))*";
        //r1形如(()|()|()|())((*)|(*)|(*)|(*))*
        String r2;
        String space = "^([ \\t]*)$";
        Pattern pspace = Pattern.compile(space);
        Matcher mspace;
        StringBuffer beforefind = new StringBuffer();
        if (s.equals("")) {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        else { //先判断首项
            String r1 = "[+-]?[ \\t]*" + r0;
            Pattern p1 = Pattern.compile(r1);
            Matcher m1 = p1.matcher(sb);
            if (m1.find()) {
                rs.append(sb.substring(m1.start(),m1.end()));
                beforefind.append(sb.substring(0, m1.start()));
                mspace = pspace.matcher(beforefind);
                if (!mspace.find()) {
                    System.out.print("WRONG FORMAT!");
                    System.exit(0);
                }
                sb.delete(m1.start(), m1.end());
                r2 = "[+-][ \\t]*" + r0;
                Pattern p2 = Pattern.compile(r2);
                Matcher m2 = p2.matcher(sb);
                //注意！！这里匹配的是sb，若匹配s则可能m2.start值发生变化！
                while (m2.find()) {
                    rs.append(sb.substring(m2.start(), m2.end()));
                    beforefind.append(sb.substring(0, m2.start()));
                    mspace = pspace.matcher(beforefind);
                    if (!mspace.find()) {
                        System.out.print("WRONG FORMAT!");
                        System.exit(0);
                    }
                    sb.delete(m2.start(), m2.end());
                    m2 = p2.matcher(sb);
                }
            }
            mspace = pspace.matcher(sb);
            if (rs.toString().equals("")) {
                System.out.print("WRONG FORMAT!");
                System.exit(0);
            }
            else if (!sb.toString().equals("") && !mspace.find()) {
                System.out.print("WRONG FORMAT!");
                System.exit(0);
            }
        }
        sb.append(rs);
    }
    
    static String OptimizeString(String s)
    {
        String os = s;
        os = os.replace(" ", "");
        os = os.replace("\t", "");
        os = os.replace("++", "+");
        os = os.replace("+-", "-");
        os = os.replace("-+", "-");
        os = os.replace("--", "+");
        os = os.replace("++", "+");
        os = os.replace("+-", "-");
        os = os.replace("-+", "-");
        os = os.replace("--", "+");
        return os;
    }
    
    static ArrayList<DealTerm> StringtoTerm(String s)
    //将处理好的string依项将系数存入arraylist
    {
        ArrayList<DealTerm> functArray = new ArrayList<>();
        String r = "(([+-]?sin\\(x\\)"
                + "(\\^[+-]?\\d+)?)|"
                + "([+-]?cos\\(x\\)"
                + "(\\^[+-]?\\d+)?)|"
                + "([+-]?x(\\^[+-]?\\d+)?)|"
                + "([+-]?[+-]?\\d+))"
                + "((\\*sin\\(x\\)"
                + "(\\^[+-]?\\d+)?)|"
                + "(\\*cos\\(x\\)"
                + "(\\^[+-]?\\d+)?)|"
                + "(\\*x(\\^[+-]?\\d+)?)|"
                + "(\\*[+-]?\\d+))*";
        Pattern p = Pattern.compile(r);
        Matcher m = p.matcher(s);
        while (m.find())
        {
            functArray.add(new DealTerm(m.group()));
        }
        return functArray;
    }
}