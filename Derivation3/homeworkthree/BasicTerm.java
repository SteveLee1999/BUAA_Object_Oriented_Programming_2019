package homeworkthree;

public abstract class BasicTerm
{
    public int classmark;
    
    @Override
    public abstract BasicTerm clone();
    
    @Override
    public abstract String toString();
    
    public abstract BasicTerm derivation();
    
    public abstract boolean isOverstep();//指数是否超过10000
}
