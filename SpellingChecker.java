import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class TrieNode {
    TrieNode[] children = new TrieNode[26]; // Assuming only lowercase a-z
    boolean isEndOfWord;
}

class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            c = Character.toLowerCase(c); // Convert to lowercase

            // Only process lowercase a-z characters
            if (c < 'a' || c > 'z') {
                continue; // Skip non-alphabetic characters
            }

            int index = c - 'a';
            if (node.children[index] == null) {
                node.children[index] = new TrieNode();
            }
            node = node.children[index];
        }
        node.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            c = Character.toLowerCase(c); // Convert to lowercase
            if (c < 'a' || c > 'z') {
                return false; // Ignore non-alphabetic characters
            }

            int index = c - 'a';
            if (node.children[index] == null) {
                return false;
            }
            node = node.children[index];
        }
        return node.isEndOfWord;
    }

    public List<String> getSuggestions(String prefix) {
        List<String> suggestions = new ArrayList<>();
        TrieNode node = root;

        // Find the node corresponding to the prefix
        for (char c : prefix.toCharArray()) {
            c = Character.toLowerCase(c);
            if (c < 'a' || c > 'z') {
                return suggestions; // No further suggestions
            }

            int index = c - 'a';
            if (node.children[index] == null) {
                return suggestions; // No further suggestions
            }
            node = node.children[index];
        }

        findWords(node, suggestions, prefix);
        return suggestions;
    }

    private void findWords(TrieNode node, List<String> suggestions, String prefix) {
        if (node.isEndOfWord) {
            suggestions.add(prefix);
        }

        for (char c = 'a'; c <= 'z'; c++) {
            int index = c - 'a';
            if (node.children[index] != null) {
                findWords(node.children[index], suggestions, prefix + c);
            }
        }
    }
}

public class SpellingChecker {
    private final Trie dictionary;

    public SpellingChecker() {
        dictionary = new Trie();
        loadDictionary("C:/Java_Pro/src/dictionary.txt"); // Adjust path as needed
    }

    private void loadDictionary(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String word;
            while ((word = br.readLine()) != null) {
                dictionary.insert(word.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("Error loading dictionary: " + e.getMessage());
        }
    }

    public boolean checkSpelling(String word) {
        return dictionary.search(word.trim());
    }

    public List<String> getSuggestions(String word) {
        return dictionary.getSuggestions(word.trim());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Spelling Checker");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.setLayout(new GridBagLayout());
            frame.getContentPane().setBackground(Color.PINK);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel label = new JLabel("Enter a word:");
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(label, gbc);

            gbc.gridy++;
            JTextField textField = new JTextField(20);
            textField.setFont(new Font("Arial", Font.PLAIN, 18));
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(textField, gbc);

            gbc.gridy++;
            JButton checkButton = new JButton("Check Spelling");
            checkButton.setFont(new Font("Arial", Font.BOLD, 20));
            checkButton.setBackground(Color.BLACK);
            checkButton.setForeground(Color.WHITE);
            frame.add(checkButton, gbc);

            gbc.gridy++;
            JLabel resultLabel = new JLabel("");
            resultLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(resultLabel, gbc);

            gbc.gridy++;
            JLabel suggestionLabel = new JLabel("");
            suggestionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            suggestionLabel.setHorizontalAlignment(SwingConstants.CENTER);
            frame.add(suggestionLabel, gbc);

            SpellingChecker spellingChecker = new SpellingChecker();

            checkButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String inputWord = textField.getText();
                    if (spellingChecker.checkSpelling(inputWord)) {
                        resultLabel.setText("Correct spelling!");
                        resultLabel.setForeground(Color.GREEN);
                        suggestionLabel.setText(""); // Clear suggestions
                    } else {
                        resultLabel.setText("Incorrect spelling! Please check.");
                        resultLabel.setForeground(Color.RED);
                        List<String> suggestions = spellingChecker.getSuggestions(inputWord);
                        if (!suggestions.isEmpty()) {
                            suggestionLabel.setText("Suggestions: " + String.join(", ", suggestions));
                        } else {
                            suggestionLabel.setText("No suggestions available.");
                        }
                    }
                }
            });

            frame.setVisible(true);
        });
    }
}