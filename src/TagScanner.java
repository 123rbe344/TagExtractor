import java.io.*;
import java.nio.file.*;
import java.util.*;
public class TagScanner
{
    // Loads the stop word file (one word per line) into a sorted set
    public static Set<String> loadStopWords(Path stopFile) throws IOException
    {
        Set<String> stopWords = new TreeSet<>();
        try (BufferedReader reader = Files.newBufferedReader(stopFile))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty())
                {
                    stopWords.add(word);
                }
            }
        }
        return stopWords;
    }
    // Scans the text file and counts every word that is not a stop word
    public static Map<String, Integer> extractTags(Path textFile, Set<String> stopWords) throws IOException
    {
        Map<String, Integer> tags = new TreeMap<>();
        try (BufferedReader reader = Files.newBufferedReader(textFile))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                // split on whitespace and dashes so "said—and" becomes two words
                String[] words = line.split("[\\s\\-—–]+");
                for (String raw : words)
                {
                    String word = raw.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if (word.isEmpty() || stopWords.contains(word))
                    {
                        continue;
                    }
                    tags.put(word, tags.getOrDefault(word, 0) + 1);
                }
            }
        }
        return tags;
    }
    // Returns the tags ordered by frequency (highest first), ties broken alphabetically
    public static List<Map.Entry<String, Integer>> sortByFrequency(Map<String, Integer> tags)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(tags.entrySet());
        entries.sort((a, b) -> b.getValue().equals(a.getValue())
                ? a.getKey().compareTo(b.getKey())
                : b.getValue() - a.getValue());
        return entries;
    }
    // Builds the text shown in the text area and written to the output file
    public static String formatTags(Map<String, Integer> tags)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s %s%n", "Tag", "Frequency"));
        sb.append("-".repeat(30)).append(System.lineSeparator());
        for (Map.Entry<String, Integer> entry : sortByFrequency(tags))
        {
            sb.append(String.format("%-20s %d%n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
