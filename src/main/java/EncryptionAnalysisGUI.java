import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;

public class EncryptionAnalysisGUI extends JFrame {
    private JTextField filePathField;
    private JButton browseButton;
    private JButton analyzeButton;
    private JProgressBar progressBar;
    private JTabbedPane resultsTabbedPane;
    private JTextArea logTextArea;
    private JTable resultsTable;
    private JPanel chartsPanel;
    private File selectedFile;
    private List<AlgorithmEvaluator.AlgorithmPerformance> performances;
    private StringBuilder logBuilder = new StringBuilder();
    private JPanel recommendationsPanel;
    
    public EncryptionAnalysisGUI() {
        setTitle("Encryption Algorithm Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        createComponents();
        layoutComponents();
        addListeners();
    }
    
    private void createComponents() {
        filePathField = new JTextField(30);
        filePathField.setEditable(false);
        
        browseButton = new JButton("Browse");
        analyzeButton = new JButton("Analyze");
        analyzeButton.setEnabled(false);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        
        resultsTabbedPane = new JTabbedPane();
        
        // Log tab
        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        
        // Results table tab
        String[] columnNames = {"Algorithm", "Encrypt Time (ms)", "Throughput (MB/s)", "Avalanche Effect", "Entropy", "Key Length (bits)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(model);
        resultsTable.setFillsViewportHeight(true);
        
        // Charts tab
        chartsPanel = new JPanel(new GridLayout(2, 2));
        
        // Recommendations tab
        recommendationsPanel = new JPanel();
        recommendationsPanel.setLayout(new BorderLayout());
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // File selection panel
        JPanel filePanel = new JPanel();
        filePanel.setBorder(BorderFactory.createTitledBorder("File Selection"));
        filePanel.add(new JLabel("File:"));
        filePanel.add(filePathField);
        filePanel.add(browseButton);
        
        // Button panel
        JPanel actionPanel = new JPanel();
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        actionPanel.add(analyzeButton);
        
        // Top panel combining file selection and action buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filePanel, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Progress panel
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        // Results tabs
        resultsTabbedPane.addTab("Log", new JScrollPane(logTextArea));
        resultsTabbedPane.addTab("Comparison Table", new JScrollPane(resultsTable));
        resultsTabbedPane.addTab("Charts", new JScrollPane(chartsPanel));
        resultsTabbedPane.addTab("Recommendations", new JScrollPane(recommendationsPanel));
        
        // Add components to the main frame
        add(topPanel, BorderLayout.NORTH);
        add(progressPanel, BorderLayout.SOUTH);
        add(resultsTabbedPane, BorderLayout.CENTER);
    }
    
    private void addListeners() {
        browseButton.addActionListener(e -> selectFile());
        analyzeButton.addActionListener(e -> performAnalysis());
    }
    
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            analyzeButton.setEnabled(true);
        }
    }
    
    private void performAnalysis() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Clear previous results
        logTextArea.setText("");
        logBuilder = new StringBuilder();
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        model.setRowCount(0);
        chartsPanel.removeAll();
        recommendationsPanel.removeAll();
        
        // Disable UI during analysis
        analyzeButton.setEnabled(false);
        browseButton.setEnabled(false);
        progressBar.setValue(0);
        
        // Run analysis in background thread
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    runEncryptionAnalysis();
                } catch (Exception e) {
                    e.printStackTrace();
                    publish("ERROR: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logTextArea.append(message + "\n");
                    logBuilder.append(message).append("\n");
                }
            }
            
            @Override
            protected void done() {
                // Re-enable UI
                analyzeButton.setEnabled(true);
                browseButton.setEnabled(true);
                progressBar.setValue(100);
                
                // Save log to file
                saveResultsToFile();
                
                // Switch to the recommendations tab
                resultsTabbedPane.setSelectedIndex(3);
            }
        };
        
        worker.execute();
    }
    
    private void runEncryptionAnalysis() throws Exception {
        publish("Encryption Algorithm Analysis Results");
        publish("Generated: " + new Date());
        publish("=====================================\n");
        
        publish("Selected file: " + selectedFile.getAbsolutePath());
        
        // Load plaintext from the selected file
        byte[] plaintext = Files.readAllBytes(selectedFile.toPath());
        
        List<EncryptionAlgorithm> algorithms = Arrays.asList(
            new AES(),
            new DES(),
            new TDES(),
            new RSA(),
            new ChaCha20(),
            new Blowfish(),
            new PBEEncryption()
        );
        
        // Create an evaluator to collect performance data
        AlgorithmEvaluator evaluator = new AlgorithmEvaluator();
        
        int totalAlgorithms = algorithms.size();
        final int[] progressCounter = {0}; // Wrapper to make it effectively final
        
        // Loop through each algorithm and run the tests
        for (EncryptionAlgorithm algo : algorithms) {
            publish("\n=== Testing " + algo.getName() + " ===");
            
            // Create performance object for this algorithm
            AlgorithmEvaluator.AlgorithmPerformance performance = new AlgorithmEvaluator.AlgorithmPerformance(algo.getName());
            
            // Update progress
            SwingUtilities.invokeLater(() -> {
                progressCounter[0]++;
                progressBar.setValue((int)((float)(progressCounter[0]) / totalAlgorithms * 100));
            });
            
            // Speed testing
            long startEnc = System.nanoTime();
            byte[] ciphertext = algo.encrypt(plaintext);
            long endEnc = System.nanoTime();
            double encTimeMs = (endEnc - startEnc) / 1_000_000.0;
            double fileSizeMB = plaintext.length / (1024.0 * 1024.0);
            double throughput = fileSizeMB / ((endEnc - startEnc) / 1e9);
            publish(algo.getName() + " Encryption Time (ms): " + encTimeMs);
            publish(algo.getName() + " Throughput (MB/s): " + throughput);
            
            // Display samples of original and encrypted data
            displayFileSamples(plaintext, ciphertext, algo.getName());
            
            // Store the speed metrics
            performance.setEncryptionTime(encTimeMs);
            performance.setThroughput(throughput);
            
            // Avalanche Effect testing
            byte[] originalCipher = algo.encrypt(plaintext);
            byte[] modifiedPlaintext = Arrays.copyOf(plaintext, plaintext.length);
            modifiedPlaintext[0] ^= 0x01; // Flip one bit
            byte[] modifiedCipher = algo.encrypt(modifiedPlaintext);
            int distance = hammingDistance(originalCipher, modifiedCipher);
            publish(algo.getName() + " Avalanche Effect Hamming Distance: " + distance);
            
            // Store avalanche effect
            performance.setAvalancheEffect(distance);
            
            // Randomness and Entropy testing
            double entropy = calculateEntropy(ciphertext);
            publish(algo.getName() + " Ciphertext Shannon Entropy: " + entropy);
            
            // Store entropy
            performance.setEntropy(entropy);
            
            // Display and store the key length
            int keyLength = algo.getKeyLength();
            publish(algo.getName() + " Key Length (bits): " + keyLength);
            performance.setKeyLength(keyLength);
            
            // Add this algorithm's performance to the evaluator
            evaluator.addPerformance(performance);
        }
        
        // Normalize scores and compare algorithms
        evaluator.normalizeScores();
        performances = evaluator.getSortedPerformances();
        
        // Display comparison and recommendations
        createComparisonTable();
        createCharts();
        createRecommendations(evaluator);
    }
    
    private void displayFileSamples(byte[] original, byte[] encrypted, String algorithmName) {
        int sampleSize = Math.min(50, original.length);
        
        publish("\n=== Provided Data for " + algorithmName + " ===");
        publish("Original data (first " + sampleSize + " bytes): ");
        displayHexAndText(original, sampleSize);
        
        publish("\nEncrypted data (first " + sampleSize + " bytes): ");
        displayHexAndText(encrypted, sampleSize);
        publish("===================================");
    }
    
    private void displayHexAndText(byte[] data, int limit) {
        int displayLimit = Math.min(limit, data.length);
        StringBuilder hexView = new StringBuilder();
        StringBuilder textView = new StringBuilder();
        
        for (int i = 0; i < displayLimit; i++) {
            // Convert to hex
            String hex = String.format("%02X ", data[i] & 0xFF);
            hexView.append(hex);
            
            // Convert to displayable text (or . if not printable)
            char c = (char) (data[i] & 0xFF);
            if (c >= 32 && c < 127) {
                textView.append(c);
            } else {
                textView.append('.');
            }
            
            // Add spacing every 8 bytes
            if ((i + 1) % 8 == 0) {
                hexView.append(" ");
                textView.append(" ");
            }
        }
        
        publish("HEX: " + hexView);
        publish("TXT: " + textView);
    }
    
    // Utility: Calculate Hamming distance between two byte arrays
    private int hammingDistance(byte[] a, byte[] b) {
        int distance = 0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int xor = a[i] ^ b[i];
            distance += Integer.bitCount(xor & 0xFF);
        }
        return distance;
    }
    
    // Utility: Calculate Shannon entropy of data
    private double calculateEntropy(byte[] data) {
        int[] freq = new int[256];
        for (byte b : data) {
            freq[b & 0xFF]++;
        }
        double entropy = 0.0;
        int length = data.length;
        for (int count : freq) {
            if (count == 0) continue;
            double p = (double) count / length;
            entropy -= p * (Math.log(p) / Math.log(2));
        }
        return entropy;
    }
    
    private void createComparisonTable() {
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        
        publish("\n===================================================");
        publish("           ALGORITHM COMPARISON RESULTS           ");
        publish("===================================================");
        
        // Display comparison table header
        publish(String.format("%-15s %-15s %-15s %-15s %-15s %-15s", 
                "Algorithm", "Encrypt Time", "Throughput", "Avalanche", "Entropy", "Key Length"));
        publish("-------------------------------------------------------------------------------------------------------------------");
        
        // Display each algorithm's metrics
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            String row = String.format("%-15s %-15.2f %-15.2f %-15d %-15.4f %-15d", 
                perf.getName(), 
                perf.getEncryptionTime(), 
                perf.getThroughput(), 
                perf.getAvalancheEffect(), 
                perf.getEntropy(),
                perf.getKeyLength());
            publish(row);
            
            // Add to table
            model.addRow(new Object[] {
                perf.getName(), 
                perf.getEncryptionTime(), 
                perf.getThroughput(), 
                perf.getAvalancheEffect(), 
                perf.getEntropy(), 
                perf.getKeyLength()
            });
        }
        
        publish("\n===================================================");
        publish("              ALGORITHM SCORES (0-10)             ");
        publish("===================================================");
        
        // Display normalized scores
        publish(String.format("%-15s %-15s %-15s %-15s %-15s %-15s %-15s", 
                "Algorithm", "Speed", "Throughput", "Avalanche", "Entropy", "Key Strength", "Total Score"));
        publish("-----------------------------------------------------------------------------------------------------------------------");
        
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            publish(String.format("%-15s %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f %-15.2f", 
                perf.getName(), 
                perf.getScore("encryptionTime"), 
                perf.getScore("throughput"), 
                perf.getScore("avalancheEffect"),
                perf.getScore("entropy"),
                perf.getScore("keyLength"),
                perf.getTotalScore()));
        }
    }
    
    private void createCharts() {
        chartsPanel.removeAll();
        
        // Create dataset for the encryption time chart
        DefaultCategoryDataset encryptionTimeDataset = new DefaultCategoryDataset();
        
        // Create dataset for the throughput chart
        DefaultCategoryDataset throughputDataset = new DefaultCategoryDataset();
        
        // Create dataset for the avalanche effect chart
        DefaultCategoryDataset avalancheDataset = new DefaultCategoryDataset();
        
        // Create dataset for the entropy chart
        DefaultCategoryDataset entropyDataset = new DefaultCategoryDataset();
        
        // Add data to datasets
        for (AlgorithmEvaluator.AlgorithmPerformance perf : performances) {
            encryptionTimeDataset.addValue(perf.getEncryptionTime(), "Encryption Time (ms)", perf.getName());
            throughputDataset.addValue(perf.getThroughput(), "Throughput (MB/s)", perf.getName());
            avalancheDataset.addValue(perf.getAvalancheEffect(), "Avalanche Effect", perf.getName());
            entropyDataset.addValue(perf.getEntropy(), "Entropy", perf.getName());
        }
        
        // Create charts
        JFreeChart encryptionTimeChart = ChartFactory.createBarChart(
                "Encryption Time", "Algorithm", "Time (ms)",
                encryptionTimeDataset, PlotOrientation.VERTICAL, true, true, false);
        
        JFreeChart throughputChart = ChartFactory.createBarChart(
                "Throughput", "Algorithm", "MB/s",
                throughputDataset, PlotOrientation.VERTICAL, true, true, false);
        
        JFreeChart avalancheChart = ChartFactory.createBarChart(
                "Avalanche Effect", "Algorithm", "Hamming Distance",
                avalancheDataset, PlotOrientation.VERTICAL, true, true, false);
        
        JFreeChart entropyChart = ChartFactory.createBarChart(
                "Entropy", "Algorithm", "Shannon Entropy",
                entropyDataset, PlotOrientation.VERTICAL, true, true, false);
        
        // Add charts to panel
        chartsPanel.add(new ChartPanel(encryptionTimeChart));
        chartsPanel.add(new ChartPanel(throughputChart));
        chartsPanel.add(new ChartPanel(avalancheChart));
        chartsPanel.add(new ChartPanel(entropyChart));
        
        chartsPanel.revalidate();
        chartsPanel.repaint();
    }
    
    private void createRecommendations(AlgorithmEvaluator evaluator) {
        recommendationsPanel.removeAll();
        
        AlgorithmEvaluator.AlgorithmPerformance bestOverall = evaluator.getBestAlgorithm();
        AlgorithmEvaluator.AlgorithmPerformance bestSpeed = evaluator.getBestForSpeed();
        AlgorithmEvaluator.AlgorithmPerformance bestSecurity = evaluator.getBestForSecurity();
        AlgorithmEvaluator.AlgorithmPerformance bestSmallFiles = evaluator.getBestForSmallFiles();
        AlgorithmEvaluator.AlgorithmPerformance bestLargeFiles = evaluator.getBestForLargeFiles();
        
        // Display recommendations
        publish("\n===================================================");
        publish("               RECOMMENDATIONS                    ");
        publish("===================================================");
        publish("Best Overall Algorithm: " + bestOverall.getName() + " (Score: " + String.format("%.2f", bestOverall.getTotalScore()) + ")");
        publish("Best for Speed: " + bestSpeed.getName());
        publish("Best for Security: " + bestSecurity.getName());
        publish("Best for Small Files: " + bestSmallFiles.getName());
        publish("Best for Large Files: " + bestLargeFiles.getName());
        
        // Create a panel with fancy recommendations
        JPanel recPanel = new JPanel();
        recPanel.setLayout(new BoxLayout(recPanel, BoxLayout.Y_AXIS));
        recPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add recommendations with nice formatting
        addRecommendationSection(recPanel, "Best Overall Algorithm", 
                bestOverall.getName(), 
                String.format("Score: %.2f/10", bestOverall.getTotalScore()),
                "Balanced performance across all metrics");
        
        addRecommendationSection(recPanel, "Best for Speed", 
                bestSpeed.getName(), 
                String.format("Encryption Time: %.2fms, Throughput: %.2fMB/s", 
                        bestSpeed.getEncryptionTime(), bestSpeed.getThroughput()),
                "Optimal choice when speed is the primary concern");
        
        addRecommendationSection(recPanel, "Best for Security", 
                bestSecurity.getName(), 
                String.format("Key Length: %d bits, Avalanche Effect: %d, Entropy: %.4f", 
                        bestSecurity.getKeyLength(), bestSecurity.getAvalancheEffect(), bestSecurity.getEntropy()),
                "Recommended for highly sensitive data");
        
        addRecommendationSection(recPanel, "Best for Small Files", 
                bestSmallFiles.getName(), 
                String.format("Encryption Time: %.2fms", bestSmallFiles.getEncryptionTime()),
                "Ideal for encrypting small files or messages");
        
        addRecommendationSection(recPanel, "Best for Large Files", 
                bestLargeFiles.getName(), 
                String.format("Throughput: %.2fMB/s", bestLargeFiles.getThroughput()),
                "Recommended for encrypting large files or streams of data");
        
        // Create a scroll pane for the recommendations
        JScrollPane scrollPane = new JScrollPane(recPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        recommendationsPanel.add(scrollPane, BorderLayout.CENTER);
        recommendationsPanel.revalidate();
        recommendationsPanel.repaint();
    }
    
    private void addRecommendationSection(JPanel panel, String title, String algorithm, String metrics, String description) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        section.add(titleLabel);
        
        JLabel algoLabel = new JLabel(algorithm);
        algoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        algoLabel.setForeground(new Color(0, 102, 204));
        section.add(algoLabel);
        
        JLabel metricsLabel = new JLabel(metrics);
        metricsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        section.add(metricsLabel);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        section.add(descLabel);
        
        panel.add(section);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }
    
    private void saveResultsToFile() {
        try {
            // Create a timestamped filename
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = sdf.format(new Date());
            
            // Create or overwrite the results.txt file
            try (PrintWriter resultWriter = new PrintWriter(new FileWriter("results.txt"))) {
                resultWriter.print(logBuilder.toString());
                JOptionPane.showMessageDialog(this, 
                        "Results have been saved to results.txt", 
                        "Analysis Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                    "Error saving results: " + e.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void publish(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message + "\n");
            logBuilder.append(message).append("\n");
        });
    }
    
    public static void main(String[] args) {
        // Try to set the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            EncryptionAnalysisGUI gui = new EncryptionAnalysisGUI();
            gui.setVisible(true);
        });
    }
}
