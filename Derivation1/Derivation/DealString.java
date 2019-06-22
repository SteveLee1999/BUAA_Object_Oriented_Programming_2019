package Derivation;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DealString
{
    static String InitString(String s)
    {
        StringBuffer sb = new StringBuffer(s);
        StringBuilder rs = new StringBuilder();
        int format;
        if (s.equals(""))
        {
            format = 0;
        }
        else
        {
            String r1 = "([+-]?[ \\t]*[+-]?(\\d+[ \\t]*\\*)?[ \\t]*"
                      + "x([ \\t]*\\^[ \\t]*[+-]?\\d+)?)"
                      + "|([+-]?[ \\t]*[+-]?\\d+)";
            Pattern p1 = Pattern.compile(r1);
            Matcher m1 = p1.matcher(sb);
            
            if (m1.find())
            {
                rs.append(sb.substring(m1.start(), m1.end()));
                sb.delete(m1.start(), m1.end());
                String r2 = "([+-][ \\t]*[+-]?(\\d+[ \\t]*\\*)?[ \\t]*"
                          + "x([ \\t]*\\^[ \\t]*[+-]?\\d+)?)"
                          + "|([+-][ \\t]*[+-]?\\d+)";
                Pattern p2 = Pattern.compile(r2);
                Matcher m2 = p2.matcher(sb);
                while (m2.find())
                {
                    rs.append(sb.substring(m2.start(), m2.end()));
                    sb.delete(m2.start(), m2.end());
                    m2 = p2.matcher(sb);
                }
            }
            
            String r3 = "^([ \\t]*)$";
            Pattern p3 = Pattern.compile(r3);
            Matcher m3 = p3.matcher(sb.toString());
            if (rs.toString().equals(""))
            {
                format = 0;
            }
            else if (sb.toString().equals("") || m3.find())
            {
                format = 1;
            }
            else
            {
                format = 0;
            }
        }
        if (format == 0)
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        String result = rs.toString().replace(" ", "");
        result = result.replace("\t", "");
        result = result.replace("++", "+");
        result = result.replace("+-", "-");
        result = result.replace("-+", "-");
        result = result.replace("--", "+");
        return result;
    }
    
    static ArrayList<DealTerm> StringtoTerm(String s)
    {
        ArrayList<DealTerm> polyarray = new ArrayList<>();
        String r = "([+-]?(\\d+\\*)?x(\\^[+-]?\\d+)?)|([+-]?\\d+)";
        Pattern p = Pattern.compile(r);
        Matcher m = p.matcher(s);
        while (m.find())
        {
            polyarray.add(new DealTerm(m.group()));
        }
        return polyarray;
    }
}