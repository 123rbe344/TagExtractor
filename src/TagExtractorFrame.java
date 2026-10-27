import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
public class TagExtractorFrame extends JFrame
{
    JPanel mainPnl;
    JPanel filePnl;
    JPanel tagPnl;
    JPanel buttonPnl;
    JLabel textFileLbl;
    JLabel stopFileLbl;
    JTextArea tagTA;
    JScrollPane scroller;
    JButton textFileBtn;
    JButton stopFileBtn;
    JButton extractBtn;
    JButton saveBtn;
    JButton quitBtn;
    Path textFile;
    Path stopFile;
    Map<String, Integer> tags;
    Font labelFont = new Font("SansSerif", Font.PLAIN, 15);
    Font tagFont = new Font("Monospaced", Font.PLAIN, 14);
    Font buttonFont = new Font("SansSerif", Font.BOLD, 16);
    public TagExtractorFrame()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());
        createFilePanel();
        mainPnl.add(filePnl, BorderLayout.NORTH);
        createTagPanel();
        mainPnl.add(tagPnl, BorderLayout.CENTER);
        createButtonPanel();
        mainPnl.add(buttonPnl, BorderLayout.SOUTH);
        add(mainPnl);
    }
    private void createFilePanel()
    {
        filePnl = new JPanel();
        filePnl.setLayout(new GridLayout(2, 1));
        filePnl.setBorder(new TitledBorder("Files"));
        textFileLbl = new JLabel("Text file: (none selected)");
        textFileLbl.setFont(labelFont);
        stopFileLbl = new JLabel("Stop words file: (none selected)");
        stopFileLbl.setFont(labelFont);
        filePnl.add(textFileLbl);
        filePnl.add(stopFileLbl);
    }
    private void createTagPanel()
    {
        tagPnl = new JPanel();
        tagPnl.setLayout(new BorderLayout());
        tagPnl.setBorder(new TitledBorder("Tags and Frequencies"));
        tagTA = new JTextArea();
        tagTA.setFont(tagFont);
        tagTA.setEditable(false);
        scroller = new JScrollPane(tagTA);
        tagPnl.add(scroller, BorderLayout.CENTER);
    }
    private void createButtonPanel()
    {
        buttonPnl = new JPanel();
        buttonPnl.setLayout(new GridLayout(1, 5));
        textFileBtn = new JButton("Text File");
        stopFileBtn = new JButton("Stop Words");
        extractBtn = new JButton("Extract");
        saveBtn = new JButton("Save Tags");
        quitBtn = new JButton("Quit");
        for (JButton btn : new JButton[]{textFileBtn, stopFileBtn, extractBtn, saveBtn, quitBtn})
        {
            btn.setFont(buttonFont);
            buttonPnl.add(btn);
        }
        textFileBtn.addActionListener(e -> chooseTextFile());
        stopFileBtn.addActionListener(e -> chooseStopFile());
        extractBtn.addActionListener(e -> extractTags());
        saveBtn.addActionListener(e -> saveTags());
        quitBtn.addActionListener(e -> System.exit(0));
    }
    private JFileChooser makeChooser()
    {
        return new JFileChooser(new File(System.getProperty("user.dir")));
    }
    private void chooseTextFile()
    {
        JFileChooser chooser = makeChooser();
        chooser.setDialogTitle("Choose a text file to extract tags from");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            textFile = chooser.getSelectedFile().toPath();
            textFileLbl.setText("Text file: " + textFile.getFileName());
        }
    }
    private void chooseStopFile()
    {
        JFileChooser chooser = makeChooser();
        chooser.setDialogTitle("Choose a stop words file");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            stopFile = chooser.getSelectedFile().toPath();
            stopFileLbl.setText("Stop words file: " + stopFile.getFileName());
        }
    }
    private void extractTags()
    {
        if (textFile == null || stopFile == null)
        {
            JOptionPane.showMessageDialog(this, "Choose both a text file and a stop words file first.");
            return;
        }
        try
        {
            Set<String> stopWords = TagScanner.loadStopWords(stopFile);
            tags = TagScanner.extractTags(textFile, stopWords);
            tagTA.setText("Tags extracted from: " + textFile.getFileName() + System.lineSeparator()
                    + tags.size() + " unique tags (" + stopWords.size() + " stop words filtered)"
                    + System.lineSeparator() + System.lineSeparator()
                    + TagScanner.formatTags(tags));
            tagTA.setCaretPosition(0);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
        }
    }
    private void saveTags()
    {
        if (tags == null)
        {
            JOptionPane.showMessageDialog(this, "Extract tags before saving.");
            return;
        }
        JFileChooser chooser = makeChooser();
        chooser.setDialogTitle("Save tags to a file");
        chooser.setSelectedFile(new File("tags.txt"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            Path outFile = chooser.getSelectedFile().toPath();
            try
            {
                Files.writeString(outFile, "Tags extracted from: " + textFile.getFileName()
                        + System.lineSeparator() + System.lineSeparator() + TagScanner.formatTags(tags));
                JOptionPane.showMessageDialog(this, "Tags saved to " + outFile.getFileName());
            }
            catch (IOException ex)
            {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }
}
