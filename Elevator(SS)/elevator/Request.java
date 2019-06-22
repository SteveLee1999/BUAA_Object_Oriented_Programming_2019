package elevator;

public class Request
{
    private final int from;
    private final int to;
    private final int id;
    private final char transfer; // 中转之后电梯号，若此次不中转则为'D'
   
    public Request(int from,int to,int id,char transfer)
    {
        this.from = from;
        this.to = to;
        this.id = id;
        this.transfer = transfer;
    }
    
    public int getFrom()
    {
        return from;
    }
    
    public int getTo()
    {
        return to;
    }
    
    public int getId()
    {
        return id;
    }
    
    public char getTransfer()
    {
        return transfer;
    }
}
