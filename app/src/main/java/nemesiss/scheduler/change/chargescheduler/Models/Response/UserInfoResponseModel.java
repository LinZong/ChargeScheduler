package nemesiss.scheduler.change.chargescheduler.Models.Response;

import nemesiss.scheduler.change.chargescheduler.Models.User;

public class UserInfoResponseModel extends CommonResponseModel
{
    private User UserInformation;

    public User getUserInformation()
    {
        return UserInformation;
    }

    public void setUserInformation(User userInformation)
    {
        UserInformation = userInformation;
    }
}
