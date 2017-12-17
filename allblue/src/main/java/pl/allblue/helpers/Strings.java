package pl.allblue.helpers;

public class Strings
{

    static public String EscapeLangChars(String s)
    {
        char[] from = new char[] { 'ą', 'ć', 'ę', 'ł', 'ń', 'ó', 'ś', 'ź', 'ż',
                'Ą', 'Ć', 'Ę', 'Ł', 'Ń', 'Ó', 'Ś', 'Ź', 'Ż' };
        char[] to = new char[] {'a', 'c', 'e', 'l', 'n', 'o', 's', 'z', 'z',
                'A', 'C', 'E', 'L', 'N', 'O', 'S', 'Z', 'Z' };

        for (int i = 0; i < from.length; i++)
            s = s.replace(from[i], to[i]);

        return s;
    }

}