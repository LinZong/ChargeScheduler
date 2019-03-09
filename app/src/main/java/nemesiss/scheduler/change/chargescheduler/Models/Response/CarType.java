package nemesiss.scheduler.change.chargescheduler.Models.Response;

public class CarType
{
    private int Id;
    private String CarName;

    public String getName()
    {
        return CarName;
    }

    public int getId()
    {
        return Id;
    }

    public void setId(int id)
    {
        Id = id;
    }

    public void setName(String name)
    {
        CarName = name;
    }
}
