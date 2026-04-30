package gui;

import evaluation.EvaluationService;
import evaluation.GroundTruth;
import query.SearchEngine;
import query.SearchResponse;
import query.SearchResult;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Set;

public class SearchGUI {
    private final SearchEngine searchEngine;
    private final EvaluationService evaluationService;
    private final GroundTruth groundTruth;

    public SearchGUI(SearchEngine searchEngine, EvaluationService evaluationService, GroundTruth groundTruth) {
        this.searchEngine = searchEngine;
        this.evaluationService = evaluationService;
        this.groundTruth = groundTruth;
    }

    public void launch() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Bilingual Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(900, 650));

        // ── Top: query input row ────────────────────────────────────────────
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JTextField queryField = new JTextField();
        queryField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton searchButton = new JButton("Search");
        topPanel.add(queryField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        // ── Centre: retrieval results ───────────────────────────────────────
        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        // ── Evaluation metrics panel ────────────────────────────────────────
        JLabel precisionLabel = new JLabel("Precision: —");
        JLabel recallLabel    = new JLabel("Recall:    —");
        precisionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        recallLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel metricsPanel = new JPanel();
        metricsPanel.setLayout(new BoxLayout(metricsPanel, BoxLayout.Y_AXIS));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Evaluation Metrics"));
        metricsPanel.add(precisionLabel);
        metricsPanel.add(recallLabel);

        // ── Bottom: suggestion + metrics ────────────────────────────────────
        JLabel suggestionLabel = new JLabel("Did you mean: ");

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 4));
        bottomPanel.add(suggestionLabel, BorderLayout.NORTH);
        bottomPanel.add(metricsPanel, BorderLayout.CENTER);

        // ── Search action ───────────────────────────────────────────────────
        Runnable runSearch = () -> {
            String query = queryField.getText();
            SearchResponse response = searchEngine.search(query);

            // Retrieval results
            StringBuilder builder = new StringBuilder();
            if (response.getResults().isEmpty()) {
                builder.append("No results found.\n");
            } else {
                int rank = 1;
                for (SearchResult result : response.getResults()) {
                    builder.append(rank++)
                            .append(". ")
                            .append(result.getDocumentId())
                            .append(" | score=")
                            .append(String.format("%.4f", result.getScore()))
                            .append("\n");
                }
            }
            resultsArea.setText(builder.toString());

            // Spelling suggestion
            if (response.getSuggestion() != null) {
                suggestionLabel.setText("Did you mean: " + response.getSuggestion());
            } else {
                suggestionLabel.setText("Did you mean: ");
            }

            // Evaluation metrics
            Set<String> relevantDocs = groundTruth.getRelevantDocs(query);
            double p = evaluationService.precision(response.getResults(), relevantDocs);
            double r = evaluationService.recall(response.getResults(), relevantDocs);
            precisionLabel.setText(String.format("Precision: %.4f", p));
            recallLabel.setText(String.format("Recall:    %.4f", r));
        };

        searchButton.addActionListener(e -> runSearch.run());
        queryField.addActionListener(e -> runSearch.run());

        frame.setLayout(new BorderLayout(8, 8));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
