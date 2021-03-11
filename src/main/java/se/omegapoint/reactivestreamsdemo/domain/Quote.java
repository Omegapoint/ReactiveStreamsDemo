package se.omegapoint.reactivestreamsdemo.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Quote
{
    private String quote;

    public Quote()
    {
    }

    public Quote(String quote)
    {
        this.quote = quote;
    }

    public List<String> getLines()
    {
        return Arrays.stream(quote.split("\\r?\\n")).collect(Collectors.toList());
    }

    public String getQuote()
    {
        return quote;
    }
}
