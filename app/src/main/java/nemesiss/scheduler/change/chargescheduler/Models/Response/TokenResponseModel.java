package nemesiss.scheduler.change.chargescheduler.Models.Response;

public class TokenResponseModel extends CommonResponseModel
{
    private long UserID;
    private TokenResponseInfo TokenResponse;

    public TokenResponseInfo getTokenResponse()
    {
        return TokenResponse;
    }

    public void setTokenResponse(TokenResponseInfo tokenResponse)
    {
        this.TokenResponse = tokenResponse;
    }

    public long getUserID()
    {
        return UserID;
    }

    public void setUserID(long userID)
    {
        UserID = userID;
    }
}




//{
//        "TokenResponse": {
//        "token": "eyJhbGciOiJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzA0L3htbGRzaWctbW9yZSNobWFjLXNoYTI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9tb2JpbGVwaG9uZSI6IjEzODAwMTM4MDAwIiwiZXhwIjoxNTUyMTAxMjkyLCJpc3MiOiJDSEFSR0VTQ0hFRFVMRVItQkFDS0VORCIsImF1ZCI6IkNIQVJHRVNDSEVEVUxFUi1CQUNLRU5EIn0.7HavMynfI_tP0c89FY_Tka0crqifiPctsB8Nx93gNSE",
//        "expire": "2019/3/9 11:14:52"
//        },
//        "statusCode": 1100,
//        "statusMessage": "登陆成功"
//        }