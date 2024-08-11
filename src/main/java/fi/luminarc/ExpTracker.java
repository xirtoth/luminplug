package fi.luminarc;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.mythicscape.batclient.interfaces.BatClientPlugin;
import com.mythicscape.batclient.interfaces.BatClientPluginTrigger;
import com.mythicscape.batclient.interfaces.BatWindow;
import com.mythicscape.batclient.interfaces.ParsedResult;

public class ExpTracker extends BatClientPlugin implements BatClientPluginTrigger {

    private BatWindow window;
    private Player player;
    private ExperiencePanel experiencePanel;
    private Timer timer;
    private final int updateTime = 30000;

    public void loadPlugin() {
        this.getClientGUI().printText("generic", "--- Loading LumiPlugi ---\n");
        this.getClientGUI().createBatWindow("testi", 200, 200, 200, 200);
        player = new Player(100, "Luminarc");
        this.getClientGUI().printText("generic", player.toString());

        JFrame frame = new JFrame("Luminarc");
        frame.setBackground(Color.BLACK);
        frame.setSize(900, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        experiencePanel = new ExperiencePanel();
        JScrollPane scrollPane = new JScrollPane(experiencePanel);
        frame.add(scrollPane);
        frame.setVisible(true);

        // Initialize and start the timer
        timer = new Timer(updateTime, e -> {
            experiencePanel.updateExperience(player.getExperience(), player.getMoney(), player.getBank());
            this.getClientGUI().doCommand("exp");
        });
        timer.start();
    }

    @Override
    public void process(Object data) {
        // Called when the trigger is executed (from commandline for example)
        // Read more in BatClientPlugin
        System.out.println("MyPluginTrigger process");
    }

    public String getName() {
        return "MyPluginTrigger";
    }

    @Override
    public ParsedResult trigger(ParsedResult arg0) {
        player.setHealth(player.getHealth() + 10);
        extractNumbers(arg0.getStrippedText());
        return null;
    }

    private void extractNumbers(String text) {

        String patternString = "Exp:\\s*(\\d+)\\s+Money:\\s*([\\d.]+)\\s+Bank:\\s*([\\d.]+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            int exp = Integer.parseInt(matcher.group(1));
            double money = Double.parseDouble(matcher.group(2));
            double bank = Double.parseDouble(matcher.group(3));

            player.setExperience(exp);
            player.setMoney(money);
            player.setBank(bank);
            experiencePanel.updateExperience(exp, money, bank);
        } else {
            System.out.println("No match found: " + text);
        }
    }

    private class ExperiencePanel extends JPanel {
        private List<Integer> experienceHistory = new ArrayList<>();
        private List<Double> moneyHistory = new ArrayList<>();
        private List<Double> bankHistory = new ArrayList<>();
        private List<String> timestampHistory = new ArrayList<>();
        private final int MAX_EXP = 2000000;
        private final int MAX_MONEY = 2000000; // Adjust this to fit expected money values
        private final int SCALE_STEP_EXP = 50000;
        private final int SCALE_STEP_MONEY = 1000; // Adjust scale step for money
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        private int xOffset = 0;
        private int pointSpacing = 20;

        public void updateExperience(int experience, double money, double bank) {

            experienceHistory.add(experience);
            moneyHistory.add(money);
            bankHistory.add(bank);
            timestampHistory.add(dateFormat.format(new Date()));

            // Check if the line reached the middle of the screen and adjust the offset
            int currentWidth = 50 + experienceHistory.size() * pointSpacing;
            if (currentWidth - xOffset > getWidth() / 10 * 8) {
                xOffset += pointSpacing; // Move the graph leftward by the spacing amount
            }

            setPreferredSize(new Dimension(Math.max(getWidth(), currentWidth - xOffset), getHeight()));
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int width = getWidth();
            int height = getHeight();
            int margin = 50;

            // Draw background grid for experience
            g2d.setColor(Color.GRAY);
            for (int i = 0; i <= MAX_EXP; i += SCALE_STEP_EXP) {
                int y = height - margin - (i * (height - 2 * margin) / MAX_EXP);
                g2d.drawLine(margin, y, width - margin, y);
            }

            // Draw background grid for money (right side)
            for (int i = 0; i <= MAX_MONEY; i += SCALE_STEP_MONEY) {
                int y = height - margin - (i * (height - 2 * margin) / MAX_MONEY);
                g2d.drawLine(margin, y, width - margin, y);
            }

            for (int i = 0; i < experienceHistory.size(); i++) {
                int x = margin + (i * pointSpacing) - xOffset;
                g2d.drawLine(x, margin, x, height - margin);
            }

            // Draw y-axis scale
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(margin, margin, margin, height - margin);
            g2d.drawLine(margin, height - margin, width - margin, height - margin);

            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            for (int i = 0; i <= MAX_EXP; i += SCALE_STEP_EXP) {
                int y = height - margin - (i * (height - 2 * margin) / MAX_EXP);
                g2d.drawString(String.valueOf(i), margin - 40, y + 5);
            }

            // Draw experience, money, and bank lines with points and timestamps
            drawGraphLines(g2d, experienceHistory, Color.RED, "Exp", margin, height, MAX_EXP);
            drawGraphLines(g2d, moneyHistory, Color.GREEN, "Money", margin, height, MAX_MONEY);
            drawGraphLines(g2d, bankHistory, Color.BLUE, "Bank", margin, height, MAX_MONEY);
        }

        private <T extends Number> void drawGraphLines(Graphics2D g2d, List<T> data, Color color, String label,
                int margin, int height, int maxScale) {
            int prevX = margin - xOffset;
            int prevY = height - margin;

            for (int i = 0; i < data.size(); i++) {
                Number value = data.get(i);
                int newX = margin + (i * pointSpacing) - xOffset;
                int newY = height - margin - (int) (value.doubleValue() * (height - 2 * margin) / maxScale);

                // Draw the line with gradient
                GradientPaint gradient = new GradientPaint(prevX, prevY, color, newX, newY, color.brighter());
                g2d.setPaint(gradient);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(prevX, prevY, newX, newY);

                // Draw the point as a circle
                g2d.setColor(color);
                g2d.fillOval(newX - 5, newY - 5, 10, 10);

                // Draw value at the point only if it's the last point
                if (i == data.size() - 1) {
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(label + ": " + value.toString(), newX - 25, newY - 10);

                    // Draw timestamp
                    g2d.setColor(Color.CYAN);
                    g2d.drawString(timestampHistory.get(i), newX - 20, height - margin + 20);
                }

                prevX = newX;
                prevY = newY;
            }
        }
    }
}
