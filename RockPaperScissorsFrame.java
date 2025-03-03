import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Random;

interface Strategy {
    String determineMove(HashMap<String, Integer> playerMoveCount);
}

class RandomStrategy implements Strategy {
    private final String[] moves = {"Rock", "Paper", "Scissors"};
    private final Random random = new Random();

    @Override
    public String determineMove(HashMap<String, Integer> playerMoveCount) {
        return moves[random.nextInt(moves.length)];
    }
}

class LeastUsedStrategy implements Strategy {
    @Override
    public String determineMove(HashMap<String, Integer> playerMoveCount) {
        return playerMoveCount.entrySet().stream()
                .min((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .map(entry -> entry.getKey())
                .orElse("Rock");
    }
}

class MostUsedStrategy implements Strategy {
    @Override
    public String determineMove(HashMap<String, Integer> playerMoveCount) {
        return playerMoveCount.entrySet().stream()
                .max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .map(entry -> entry.getKey())
                .orElse("Rock");
    }
}

class LastUsedStrategy implements Strategy {
    private String lastMove = "Rock";

    public void setLastMove(String move) {
        lastMove = move;
    }

    @Override
    public String determineMove(HashMap<String, Integer> playerMoveCount) {
        return lastMove;
    }
}

class CheatStrategy implements Strategy {
    private final Random random = new Random();

    @Override
    public String determineMove(HashMap<String, Integer> playerMoveCount) {
        if (random.nextInt(10) < 1) { // 10% chance to cheat
            if (playerMoveCount.containsKey("Rock")) return "Paper";
            if (playerMoveCount.containsKey("Paper")) return "Scissors";
            if (playerMoveCount.containsKey("Scissors")) return "Rock";
        }
        return new RandomStrategy().determineMove(playerMoveCount);
    }
}

public class RockPaperScissorsFrame extends JFrame {
    private final JButton rockButton, paperButton, scissorsButton, quitButton;
    private final JTextArea resultArea;
    private final JTextField playerWinsField, computerWinsField, tiesField;
    private int playerWins = 0, computerWins = 0, ties = 0;
    private final HashMap<String, Integer> playerMoveCount = new HashMap<>();
    private final Strategy[] strategies = {new RandomStrategy(), new LeastUsedStrategy(), new MostUsedStrategy(), new LastUsedStrategy(), new CheatStrategy()};
    private final LastUsedStrategy lastUsedStrategy = new LastUsedStrategy();
    private final Random random = new Random();

    public RockPaperScissorsFrame() {
        setTitle("Rock Paper Scissors Game");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Choose Your Move"));

        rockButton = new JButton("Rock");
        paperButton = new JButton("Paper");
        scissorsButton = new JButton("Scissors");
        quitButton = new JButton("Quit");

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 6));
        statsPanel.add(new JLabel("Player Wins:"));
        playerWinsField = new JTextField("0", 5);
        playerWinsField.setEditable(false);
        statsPanel.add(playerWinsField);

        statsPanel.add(new JLabel("Computer Wins:"));
        computerWinsField = new JTextField("0", 5);
        computerWinsField.setEditable(false);
        statsPanel.add(computerWinsField);

        statsPanel.add(new JLabel("Ties:"));
        tiesField = new JTextField("0", 5);
        tiesField.setEditable(false);
        statsPanel.add(tiesField);

        add(statsPanel, BorderLayout.CENTER);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.SOUTH);

        rockButton.addActionListener(new MoveListener("Rock"));
        paperButton.addActionListener(new MoveListener("Paper"));
        scissorsButton.addActionListener(new MoveListener("Scissors"));
        quitButton.addActionListener(e -> System.exit(0));
    }

    private class MoveListener implements ActionListener {
        private final String playerMove;

        public MoveListener(String move) {
            this.playerMove = move;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            playerMoveCount.put(playerMove, playerMoveCount.getOrDefault(playerMove, 0) + 1);
            lastUsedStrategy.setLastMove(playerMove);

            Strategy strategy = strategies[random.nextInt(strategies.length)];
            String computerMove = strategy.determineMove(playerMoveCount);

            String result = determineWinner(playerMove, computerMove);
            resultArea.append(result + " (" + strategy.getClass().getSimpleName() + ")\n");
            updateStats();
        }
    }

    private String determineWinner(String playerMove, String computerMove) {
        if (playerMove.equals(computerMove)) {
            ties++;
            return playerMove + " vs " + computerMove + ". It's a Tie!";
        }
        if ((playerMove.equals("Rock") && computerMove.equals("Scissors")) ||
                (playerMove.equals("Paper") && computerMove.equals("Rock")) ||
                (playerMove.equals("Scissors") && computerMove.equals("Paper"))) {
            playerWins++;
            return playerMove + " beats " + computerMove + ". Player Wins!";
        } else {
            computerWins++;
            return computerMove + " beats " + playerMove + ". Computer Wins!";
        }
    }

    private void updateStats() {
        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
            frame.setVisible(true);
        });
    }
}
