package nemesiss.scheduler.change.chargescheduler.Models.Response;

public class CommonResponseModel
{
    private int StatusCode;
    private String StatusMessage;

    public int getStatusCode()
    {
        return StatusCode;
    }

    public String getStatusMessage()
    {
        return StatusMessage;
    }

    public void setStatusCode(int statusCode)
    {
        this.StatusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage)
    {
        this.StatusMessage = statusMessage;
    }
}
