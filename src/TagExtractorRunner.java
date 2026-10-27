import javax.swing.*;
public class TagExtractorRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ex)
            {
            }
            TagExtractorFrame frame = new TagExtractorFrame();
            frame.setTitle("Tag / Keyword Extractor");
            frame.setSize(800, 640);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
