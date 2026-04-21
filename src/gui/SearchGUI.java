package gui;

import query.SearchEngine;
import query.SearchResponse;
import query.SearchResult;

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

public class SearchGUI {
    private final SearchEngine searchEngine;

    public SearchGUI(SearchEngine searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void launch() {
        SwingUtilities.invokeLater(this::createAndShow);
    }

    private void createAndShow() {
        JFrame frame = new JFrame("Bilingual Search Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(900, 600));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JTextField queryField = new JTextField();
        queryField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton searchButton = new JButton("Search");

        topPanel.add(queryField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(resultsArea);

        JLabel suggestionLabel = new JLabel("Did you mean: ");

        Runnable runSearch = () -> {
            String query = queryField.getText();
            SearchResponse response = searchEngine.search(query);

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

            if (response.getSuggestion() != null) {
                suggestionLabel.setText("Did you mean: " + response.getSuggestion());
            } else {
                suggestionLabel.setText("Did you mean: ");
            }
        };

        searchButton.addActionListener(e -> runSearch.run());
        queryField.addActionListener(e -> runSearch.run());

        frame.setLayout(new BorderLayout(8, 8));
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(suggestionLabel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
