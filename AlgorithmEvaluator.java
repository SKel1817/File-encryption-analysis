import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmEvaluator {
    
    // Class to store algorithm performance metrics
    public static class AlgorithmPerformance {
        private String name;
        private double encryptionTime; // In milliseconds
        private double throughput; // In MB/s
        private int avalancheEffect; // Higher is better
        private double entropy; // Higher is better
        private int keyLength; // In bits
        private Map<String, Double> scores = new HashMap<>();
        private double totalScore;
        
        public AlgorithmPerformance(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setEncryptionTime(double encryptionTime) {
            this.encryptionTime = encryptionTime;
        }
        
        public double getEncryptionTime() {
            return encryptionTime;
        }
        
        public void setThroughput(double throughput) {
            this.throughput = throughput;
        }
        
        public double getThroughput() {
            return throughput;
        }
        
        public void setAvalancheEffect(int avalancheEffect) {
            this.avalancheEffect = avalancheEffect;
        }
        
        public int getAvalancheEffect() {
            return avalancheEffect;
        }
        
        public void setEntropy(double entropy) {
            this.entropy = entropy;
        }
        
        public double getEntropy() {
            return entropy;
        }
        
        public void setKeyLength(int keyLength) {
            this.keyLength = keyLength;
        }
        
        public int getKeyLength() {
            return keyLength;
        }
        
        public void setScore(String metric, double score) {
            scores.put(metric, score);
        }
        
        public double getScore(String metric) {
            return scores.getOrDefault(metric, 0.0);
        }
        
        public void calculateTotalScore() {
            // Weighted sum of all scores
            double speedWeight = 0.3;
            double securityWeight = 0.5;
            double efficiencyWeight = 0.2;
            
            totalScore = 
                speedWeight * (scores.getOrDefault("encryptionTime", 0.0) + 
                              scores.getOrDefault("throughput", 0.0)) / 2 +
                securityWeight * (scores.getOrDefault("avalancheEffect", 0.0) + 
                                scores.getOrDefault("entropy", 0.0) + 
                                scores.getOrDefault("keyLength", 0.0)) / 3 +
                efficiencyWeight * (scores.getOrDefault("resourceUsage", 0.0));
        }
        
        public double getTotalScore() {
            return totalScore;
        }
    }
    
    // Store performances of all algorithms
    private List<AlgorithmPerformance> performances = new ArrayList<>();
    
    // Add performance data for an algorithm
    public void addPerformance(AlgorithmPerformance performance) {
        performances.add(performance);
    }
    
    // Normalize scores (0 to 10) for each metric across algorithms
    public void normalizeScores() {
        // For encryption time (lower is better)
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        for (AlgorithmPerformance perf : performances) {
            minTime = Math.min(minTime, perf.getEncryptionTime());
            maxTime = Math.max(maxTime, perf.getEncryptionTime());
        }
        
        // For throughput (higher is better)
        double minThroughput = Double.MAX_VALUE;
        double maxThroughput = Double.MIN_VALUE;
        for (AlgorithmPerformance perf : performances) {
            minThroughput = Math.min(minThroughput, perf.getThroughput());
            maxThroughput = Math.max(maxThroughput, perf.getThroughput());
        }
        
        // For avalanche effect (higher is better)
        int minAvalanche = Integer.MAX_VALUE;
        int maxAvalanche = Integer.MIN_VALUE;
        for (AlgorithmPerformance perf : performances) {
            minAvalanche = Math.min(minAvalanche, perf.getAvalancheEffect());
            maxAvalanche = Math.max(maxAvalanche, perf.getAvalancheEffect());
        }
        
        // For entropy (higher is better)
        double minEntropy = Double.MAX_VALUE;
        double maxEntropy = Double.MIN_VALUE;
        for (AlgorithmPerformance perf : performances) {
            minEntropy = Math.min(minEntropy, perf.getEntropy());
            maxEntropy = Math.max(maxEntropy, perf.getEntropy());
        }
        
        // For key length (higher is better)
        int minKeyLength = Integer.MAX_VALUE;
        int maxKeyLength = Integer.MIN_VALUE;
        for (AlgorithmPerformance perf : performances) {
            minKeyLength = Math.min(minKeyLength, perf.getKeyLength());
            maxKeyLength = Math.max(maxKeyLength, perf.getKeyLength());
        }
        
        // Calculate normalized scores for each algorithm
        for (AlgorithmPerformance perf : performances) {
            // For encryption time (lower is better, so inversed)
            double timeRange = maxTime - minTime;
            double timeScore = timeRange > 0 ? 
                10 * (1 - (perf.getEncryptionTime() - minTime) / timeRange) : 5.0;
            perf.setScore("encryptionTime", timeScore);
            
            // For throughput (higher is better)
            double throughputRange = maxThroughput - minThroughput;
            double throughputScore = throughputRange > 0 ? 
                10 * ((perf.getThroughput() - minThroughput) / throughputRange) : 5.0;
            perf.setScore("throughput", throughputScore);
            
            // For avalanche effect (higher is better)
            double avalancheRange = maxAvalanche - minAvalanche;
            double avalancheScore = avalancheRange > 0 ? 
                10 * ((perf.getAvalancheEffect() - minAvalanche) / avalancheRange) : 5.0;
            perf.setScore("avalancheEffect", avalancheScore);
            
            // For entropy (higher is better)
            double entropyRange = maxEntropy - minEntropy;
            double entropyScore = entropyRange > 0 ? 
                10 * ((perf.getEntropy() - minEntropy) / entropyRange) : 5.0;
            perf.setScore("entropy", entropyScore);
            
            // For key length (higher is better)
            double keyLengthRange = maxKeyLength - minKeyLength;
            double keyLengthScore = keyLengthRange > 0 ? 
                10 * ((perf.getKeyLength() - minKeyLength) / keyLengthRange) : 5.0;
            perf.setScore("keyLength", keyLengthScore);
            
            // Calculate total score
            perf.calculateTotalScore();
        }
    }
    
    // Get the best algorithm based on total score
    public AlgorithmPerformance getBestAlgorithm() {
        return Collections.max(performances, Comparator.comparing(AlgorithmPerformance::getTotalScore));
    }
    
    // Get best algorithm for specific criteria
    public AlgorithmPerformance getBestForSpeed() {
        return Collections.max(performances, Comparator.comparing(perf -> 
            (perf.getScore("encryptionTime") + perf.getScore("throughput")) / 2));
    }
    
    public AlgorithmPerformance getBestForSecurity() {
        return Collections.max(performances, Comparator.comparing(perf -> 
            (perf.getScore("avalancheEffect") + perf.getScore("entropy") + perf.getScore("keyLength")) / 3));
    }
    
    public AlgorithmPerformance getBestForSmallFiles() {
        return Collections.max(performances, Comparator.comparing(perf -> perf.getScore("encryptionTime")));
    }
    
    public AlgorithmPerformance getBestForLargeFiles() {
        return Collections.max(performances, Comparator.comparing(perf -> perf.getScore("throughput")));
    }
    
    // Get all performances
    public List<AlgorithmPerformance> getAllPerformances() {
        return performances;
    }
    
    // Get performances sorted by total score
    public List<AlgorithmPerformance> getSortedPerformances() {
        List<AlgorithmPerformance> sorted = new ArrayList<>(performances);
        Collections.sort(sorted, Comparator.comparing(AlgorithmPerformance::getTotalScore).reversed());
        return sorted;
    }
}
