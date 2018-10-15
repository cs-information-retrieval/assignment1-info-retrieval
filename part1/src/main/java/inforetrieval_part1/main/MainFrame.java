package inforetrieval_part1.main;

import java.awt.Desktop;
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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;

import inforetrieval_part1.controller.Controller;
import inforetrieval_part1.controller.CrawlController;
import inforetrieval_part1.controller.MultiThreadedCrawler;
import inforetrieval_part1.controller.SingleThreadedCrawler;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    
    //************************************************************************
    // Variable declaration
    private JLabel fileChooserLabel;
    private JButton fileChooserButton;
    private JFileChooser fileChooser;
    private File selectedFile;
    private boolean firstTimeFileBrowsed;
    private File prevPath;
    
    private JLabel seedLabel;
    private JTextField seedText;
    
    private JLabel maxPagesLabel;
    private JTextField maxPagesText;
    
    private JLabel domainRestrictionLabel;
    private JTextField domainRestrictionText;
    
    private JButton goButton;
    private JButton openReportButton;
    
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
        this.setTitle("Information Retrieval - Part 1: Crawler");
        this.setLayout(new GridBagLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.firstTimeFileBrowsed = true;
        
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
        //seedText.setEditable(false);
        seedText.setPreferredSize(new Dimension(200, 20));
        seedText.setComponentPopupMenu(this.getMenu());
        // If this changes, enable the "Go" button
        seedText.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent arg0) {
                // Text changed, enable the go button
                goButton.setEnabled(true);
                openReportButton.setEnabled(true);
            }

            public void changedUpdate(DocumentEvent e) {}

            public void removeUpdate(DocumentEvent e) {}
        });
        gridBagConstraints = getBaseGridbagConstraints(1, 1);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(seedText, gridBagConstraints);
        
        maxPagesLabel = new JLabel();
        maxPagesLabel.setText("Max Pages");
        gridBagConstraints = getBaseGridbagConstraints(0, 2);
        getContentPane().add(maxPagesLabel, gridBagConstraints);
        
        maxPagesText = new JTextField();
        //maxPagesText.setEditable(false);
        maxPagesText.setPreferredSize(new Dimension(200, 20));
        maxPagesText.setComponentPopupMenu(this.getMenu());
        gridBagConstraints = getBaseGridbagConstraints(1, 2);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(maxPagesText, gridBagConstraints);
        
        domainRestrictionLabel = new JLabel();
        domainRestrictionLabel.setText("Domain Restriction");
        gridBagConstraints = getBaseGridbagConstraints(0, 3);
        getContentPane().add(domainRestrictionLabel, gridBagConstraints);
        
        domainRestrictionText = new JTextField();
        //domainRestrictionText.setEditable(false);
        domainRestrictionText.setPreferredSize(new Dimension(200, 20));
        domainRestrictionText.setComponentPopupMenu(this.getMenu());
        gridBagConstraints = getBaseGridbagConstraints(1, 3);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        getContentPane().add(domainRestrictionText, gridBagConstraints);
        
        goButton = new JButton();
        goButton.setText("Go!");
        goButton.setEnabled(false);
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // Set the csvInfo based on current GUI
                String[] localCsvInfo = new String[3];
                localCsvInfo[0] = seedText.getText();
                localCsvInfo[1] = maxPagesText.getText();
                localCsvInfo[2] = domainRestrictionText.getText();
                setCsvInfo(localCsvInfo);
                
                // Multi-threaded Controller
                // MultiThreadedCrawler mtc = new MultiThreadedCrawler();
                // mtc.setSpecification(seedText.getText(), 
                      // Integer.parseInt(maxPagesText.getText()), 
                      // domainRestrictionText.getText());
                // mtc.execute(20);
                
                // Single-threaded Controller
                Controller controller = SingleThreadedCrawler.getInstance();
                controller.execute(getCsvInfo());
                
                JOptionPane.showMessageDialog(null, "Finished Crawling!");
            }
        });
        gridBagConstraints = getBaseGridbagConstraints(0, 4);
        getContentPane().add(goButton, gridBagConstraints);
        
        
        openReportButton = new JButton();
        openReportButton.setText("Open report.html");
        openReportButton.setEnabled(false);
        openReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                File reportHtml = new File("report.html");
                try {
                    Desktop.getDesktop().browse(reportHtml.toURI());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        gridBagConstraints = getBaseGridbagConstraints(1, 4);
        getContentPane().add(openReportButton, gridBagConstraints);
        
        
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
        if (firstTimeFileBrowsed) {
            // Set the file chooser to the current directory
            File currentDirectory = new File(System.getProperty("user.dir"));
            fileChooser.setCurrentDirectory(currentDirectory);
            firstTimeFileBrowsed = false;
        }
        else {
            // Set the file chooser to last chosen directory
            fileChooser.setCurrentDirectory(this.prevPath);
        }
        // Open the file chooser
        int returnVal = fileChooser.showOpenDialog(this);
        this.prevPath = fileChooser.getSelectedFile();
        
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
                    
                    String seed = splits[0];
                    int maxPages = Integer.parseInt(splits[1]);
                    String domainRestriction = "";
                    // If the domain restriction is not empty
                    if (splits.length > 2) {
                        domainRestriction = splits[2];
                    }                   
                    
                    seedText.setText(seed);
                    maxPagesText.setText(String.valueOf(maxPages));
                    domainRestrictionText.setText(domainRestriction);
                    
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
    // Reference: https://stackoverflow.com/questions/30682416/java-right-click-copy-cut-paste-on-textfield
    private JPopupMenu getMenu() {
        JPopupMenu menu = new JPopupMenu();
        Action cut = new DefaultEditorKit.CutAction();
        cut.putValue(Action.NAME, "Cut");
        cut.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
        menu.add( cut );

        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        menu.add( copy );

        Action paste = new DefaultEditorKit.PasteAction();
        paste.putValue(Action.NAME, "Paste");
        paste.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
        menu.add( paste );
        
        return menu;
    }

    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        new MainFrame().setVisible(true);;
    }
}
