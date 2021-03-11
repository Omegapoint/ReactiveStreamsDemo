package se.omegapoint.reactivestreamsdemo.domain;

public class Authentication
{
    private boolean authenticated;

    public Authentication(boolean authenticated)
    {
        this.authenticated = authenticated;
    }

    public Authentication()
    {
    }

    public static Authentication granted()
    {
        return new Authentication(true);
    }

    public static Authentication notGranted()
    {
        return new Authentication(false);
    }


    public boolean isAuthenticated()
    {
        return authenticated;
    }
}
