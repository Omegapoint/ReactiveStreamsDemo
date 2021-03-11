package se.omegapoint.reactivestreamsdemo.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lyrics
{
    private String lyrics;

    public Lyrics()
    {
    }

    public Lyrics(String lyrics)
    {
        this.lyrics = lyrics;
    }

    public static Lyrics fromLines(List<String> lines)
    {
        String text = String.join("\n", lines);

        return new Lyrics(text);
    }

    public List<String> getLines()
    {
        return Arrays.stream(lyrics.split("\\r?\\n"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
    }

    public String getLyrics()
    {
        return lyrics;
    }
}
