package inforetrieval_part1;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    //************************************************************************
    // Variable declaration
    private JLabel fileChooserLabel;
    private JButton fileChooserButton;
    private JFileChooser fileChooser;
    private File selectedFile;
    
    private JLabel seedLabel;
    private JTextField seedText;
    
    private JLabel maxPagesLabel;
    private JTextField maxPagesText;
    
    private JLabel domainRestrictionLabel;
    private JTextField domainRestrictionText;
    
    private JButton goButton;
    
    private String[] csvInfo;
    
    // Getters and setters
    public File getFile() {return selectedFile;}
    public void setFile(File file) {this.selectedFile = file;}
    
    public String[] getCsvInfo() {return this.csvInfo;}
    public void setCsvInfo(String[] csvInfo) {this.csvInfo = csvInfo;}
    
    // End variable declaration
    //************************************************************************
    
    public MainFrame() {
        init();
    }
    
    
    /**
     * Function to init all frames
     */
    public void init() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        GridBagConstraints gridBagConstraints;
        
        fileChooserLabel = new JLabel();
        fileChooserLabel.setText("Choose CSV file");
        gridBagConstraints = getBaseGridbagConstraints(0, 0);
        getContentPane().add(fileChooserLabel, gridBagConstraints);
        
        fileChooser = new JFileChooser();       
        fileChooserButton = new JButton();
        fileChooserButton.setText("Browse");
        fileChooserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileChooserButtonActionPerformed(arg0);
            }
        });
        gridBagConstraints = getBaseGridbagConstraints(1, 0);
        getContentPane().add(fileChooserButton, gridBagConstraints);
        
        seedLabel = new JLabel();
        seedLabel.setText("Seed");
        gridBagConstraints = getBaseGridbagConstraints(0, 1);
        getContentPane().add(seedLabel, gridBagConstraints);
        
        seedText = new JTextField();
        seedText.setEditable(false);
        // If this changes, enable the "Go" button
        seedText.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {}

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                // Text changed, enable the go button
                goButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {}
        });
        gridBagConstraints = getBaseGridbagConstraints(1, 1);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(seedText, gridBagConstraints);
        
        maxPagesLabel = new JLabel();
        maxPagesLabel.setText("Max Pages");
        gridBagConstraints = getBaseGridbagConstraints(0, 2);
        getContentPane().add(maxPagesLabel, gridBagConstraints);
        
        maxPagesText = new JTextField();
        maxPagesText.setEditable(false);
        gridBagConstraints = getBaseGridbagConstraints(1, 2);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(maxPagesText, gridBagConstraints);
        
        domainRestrictionLabel = new JLabel();
        domainRestrictionLabel.setText("Domain Restriction");
        gridBagConstraints = getBaseGridbagConstraints(0, 3);
        getContentPane().add(domainRestrictionLabel, gridBagConstraints);
        
        domainRestrictionText = new JTextField();
        domainRestrictionText.setEditable(false);
        gridBagConstraints = getBaseGridbagConstraints(1, 3);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(domainRestrictionText, gridBagConstraints);
        
        goButton = new JButton();
        goButton.setText("Go!");
        goButton.setEnabled(false);
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println(arg0.getActionCommand());
                Controller controller = Controller.getInstance();
                controller.execute(getCsvInfo());
            }
        });
        gridBagConstraints = getBaseGridbagConstraints(0, 4);
        getContentPane().add(goButton, gridBagConstraints);
        
        
        //**********************************************************************
        // Do NOT delete the line below
        this.pack();
    }
    
    
    /**
     * Obtain the base gridbag constraints.
     * @param gridx - where this content should be placed
     * @param gridy - where this content should be placed
     * @return
     */
    private GridBagConstraints getBaseGridbagConstraints(int gridx, int gridy) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        int ipadnum = 10;
        gridBagConstraints.ipadx = ipadnum;
        gridBagConstraints.ipady = ipadnum;
        
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        return gridBagConstraints;
    }
    
    
    /**
     * Action event to perform what the FileChooser button does
     * @param evt
     */
    private void fileChooserButtonActionPerformed(ActionEvent evt) {
        // Set the file chooser to the current directory
        File currentDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(currentDirectory);
        // Open the file chooser
        int returnVal = fileChooser.showOpenDialog(this);
        
        // If the user selects "OK"
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // Check if the file ends with ".csv"
            File selectedFile = fileChooser.getSelectedFile();
            // If the file does not end with csv
            if (selectedFile.getName().endsWith(".csv") == false) {
                JOptionPane.showMessageDialog(this, "Please select a .csv file");
            }
            else {
                // Set this class's file to the selected file
                this.setFile(fileChooser.getSelectedFile());
                this.readFileContents();
            }
        }
        else {
            System.out.println("File access cancelled by user.");
        }
    }
    
    
    /**
     * Read the content of the selected file
     */
     /**
     * Read the file contents of the selected csv file
     */
    private void readFileContents() {
        // Check if the file is not null
        if (this.getFile() == null) {
            System.out.println("No file selected yet.");
        }
        else {
            Path path = Paths.get(this.getFile().getPath());
            try {
                // Read the file contents
                List<String> fileContents = Files.readAllLines(path);
                
                // If there is more than one line
                if (fileContents.size() > 1) {
                    JOptionPane.showMessageDialog(this, "CSV file should only have one line");
                }
                else {
                    String[] splits = fileContents.get(0).split(",");
                    this.setCsvInfo(splits);
                    
                    String seed = splits[0];
                    int maxPages = Integer.parseInt(splits[1]);
                    String domainRestriction = splits[2];
                    
                    seedText.setText(seed);
                    maxPagesText.setText(String.valueOf(maxPages));
                    domainRestrictionText.setText(domainRestriction);
                    
                    this.pack();
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        new MainFrame().setVisible(true);;
    }
}
