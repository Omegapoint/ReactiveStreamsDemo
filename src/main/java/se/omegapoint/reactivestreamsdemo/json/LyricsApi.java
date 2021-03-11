package se.omegapoint.reactivestreamsdemo.json;

import se.omegapoint.reactivestreamsdemo.domain.Lyrics;

public class LyricsApi
{
    public String lyrics;

    public LyricsApi()
    {
    }

    public LyricsApi(String lyrics)
    {
        this.lyrics = lyrics;
    }

    public static LyricsApi fromDomain(Lyrics lyrics)
    {
        return new LyricsApi(lyrics.getLyrics());
    }
}
