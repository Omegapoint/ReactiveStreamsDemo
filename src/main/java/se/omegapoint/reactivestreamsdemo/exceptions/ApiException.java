package se.omegapoint.reactivestreamsdemo.exceptions;

public class ApiException extends RuntimeException
{
    private final int httpCode;

    public ApiException(String s, int httpStatusCode)
    {
        super(s);
        this.httpCode = httpStatusCode;
    }

    public int getHttpCode()
    {
        return httpCode;
    }
}
