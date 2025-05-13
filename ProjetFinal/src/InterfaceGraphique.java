// InterfaceGraphique.java (corrigé avec barre d'énergie toujours visible)

import Classes.RobotLivraison;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import Classes.Robot;

public class InterfaceGraphique extends JFrame {

    private void verifierEtatEnergie() {
        boolean actif = robot.getEnergie() > 0;
        livraison.setEnabled(actif);
        recharger.setEnabled(actif);
    }

    private void mettreAJourEnergie() {
        energieBar.setValue(robot.getEnergie());
        verifierEtatEnergie();
    }

    private boolean modeLivraisonActif = false;
    private final Color gridColor = new Color(240, 245, 250);
    private RobotLivraison robot = new RobotLivraison("KING ROBOT", 1, 2,50);
    private JPanel[] cells = new JPanel[16];
    private JLabel robotLabel;
    private int curr_index = -1;

    private JButton nettoyer = createStyledButton("Nettoyer la map");
    private JButton recharger = createStyledButton("Recharger");
    private JButton eteindre = createStyledButton("Eteindre le robot");
    private JButton historique = createStyledButton("Afficher historique");
    private JButton livraison = createStyledButton("Livrer");
    private JButton ramasser = createStyledButton("Ramasser");
    private JButton dechet = createStyledButton("dechet");
    private JProgressBar energieBar = new JProgressBar(0, 100);

    ImageIcon originalIcon = new ImageIcon("src/images/robot.png");
    Image scaledImage = originalIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
    ImageIcon robotIcon = new ImageIcon(scaledImage);

    ImageIcon dead = new ImageIcon("src/images/dead.jpeg");
    Image scaledDead = dead.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
    ImageIcon robotIcon2 = new ImageIcon(scaledDead);

    JButton createStyledButton(String text) {
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        JButton button = new JButton(text);
        button.setBackground(new Color(180, 205, 255));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setFont(buttonFont);
        return button;
    }

    public void placerAlea(ImageIcon r) {
        if (curr_index != -1) {
            cells[curr_index].removeAll();
            cells[curr_index].revalidate();
            cells[curr_index].repaint();
        }
        int newIndex = (int) (Math.random() * 16);
        curr_index = newIndex;
        robotLabel = new JLabel(r);
        cells[curr_index].add(robotLabel);
        cells[curr_index].revalidate();
        cells[curr_index].repaint();
        robot.ajouterHistorique("Placé aléatoirement à l'index " + curr_index);
    }

    public void placerRobot(ImageIcon r) {
        if (curr_index != -1) {
            cells[curr_index].removeAll();
            cells[curr_index].revalidate();
            cells[curr_index].repaint();
        }
        curr_index = robot.getX() + robot.getY() * 4;
        robotLabel = new JLabel(r);
        cells[curr_index].add(robotLabel);
        cells[curr_index].revalidate();
        cells[curr_index].repaint();
    }

    public void deplacerRobotVers(int destinationIndex) {
        if (destinationIndex == curr_index) return;

        int startRow = curr_index / 4;
        int startCol = curr_index % 4;
        int endRow = destinationIndex / 4;
        int endCol = destinationIndex % 4;

        java.util.List<Integer> path = new java.util.ArrayList<>();
        int rowStep = (endRow > startRow) ? 1 : -1;
        for (int r = startRow; r != endRow; r += rowStep) {
            path.add(r * 4 + startCol);
        }
        int colStep = (endCol > startCol) ? 1 : -1;
        for (int c = startCol; c != endCol; c += colStep) {
            path.add(endRow * 4 + c);
        }
        path.add(destinationIndex);

        Timer timer = new Timer(250, null);
        final int[] step = {0};
        timer.addActionListener(e -> {
            if (step[0] >= path.size()) {
                ((Timer) e.getSource()).stop();
                modeLivraisonActif = false;
                robot.ajouterHistorique("Livraison effectuée à l'index " + destinationIndex);
                return;
            }
            int index = path.get(step[0]);
            cells[index].setBackground(new Color(100, 255, 100));
            placerRobotALaPosition(index);
            step[0]++;
        });
        timer.start();
    }

    public void placerRobotALaPosition(int index) {
        if (curr_index != -1) {
            cells[curr_index].removeAll();
            cells[curr_index].revalidate();
            cells[curr_index].repaint();
        }
        curr_index = index;
        robot.setX(index % 4);
        robot.setY(index / 4);
        robotLabel = new JLabel(robotIcon);
        cells[curr_index].add(robotLabel);
        cells[curr_index].revalidate();
        cells[curr_index].repaint();
        robot.ajouterHistorique("Déplacé vers index " + curr_index);
    }

    public InterfaceGraphique() {
        setSize(800, 800);
        setTitle("Classes.Robot Graphique");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color backgroundColor = new Color(230, 240, 255);
        Color panelColor = new Color(210, 225, 245);
        Color gridColor = new Color(240, 245, 250);
        Color borderGray = new Color(180, 190, 200);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        JPanel topBlock = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBlock.setBackground(panelColor);
        energieBar.setStringPainted(true);
        energieBar.setForeground(new Color(50, 180, 50));
        topBlock.add(new JLabel("Énergie:"));
        topBlock.add(energieBar);

        JPanel map = new JPanel();
        map.setPreferredSize(new Dimension(500, 500));
        map.setBackground(panelColor);
        map.setBorder(BorderFactory.createLineBorder(borderGray, 2));
        map.setLayout(new GridLayout(4, 4, 3, 3));

        for (int i = 0; i < 16; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBackground(gridColor);
            cell.setBorder(BorderFactory.createLineBorder(borderGray, 1));
            cells[i] = cell;
            map.add(cell);
        }

        this.placerRobot(robotIcon);

        JPanel leftBlock = new JPanel();
        leftBlock.setPreferredSize(new Dimension(150, 300));
        leftBlock.setLayout(new GridLayout(4, 1, 10, 15));
        leftBlock.setBorder(BorderFactory.createTitledBorder("Actions"));
        leftBlock.setBackground(panelColor);
        leftBlock.add(nettoyer);
        leftBlock.add(recharger);
        leftBlock.add(eteindre);
        leftBlock.add(historique);

        JPanel rightBlock = new JPanel();
        rightBlock.setPreferredSize(new Dimension(150, 300));
        rightBlock.setLayout(new GridLayout(3, 1, 10, 15));
        rightBlock.setBorder(BorderFactory.createTitledBorder("Options"));
        rightBlock.setBackground(panelColor);
        rightBlock.add(livraison);
        rightBlock.add(ramasser) ;
        rightBlock.add(dechet) ;

        JPanel downBlock = new JPanel();
        downBlock.setPreferredSize(new Dimension(600, 60));
        downBlock.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        downBlock.setBorder(BorderFactory.createTitledBorder("Commandes rapides"));
        downBlock.setBackground(panelColor);

        JButton upButton = createStyledButton("Déplacer haut");
        JButton downButton = createStyledButton("Déplacer bas");
        JButton leftButton = createStyledButton("Déplacer gauche");
        JButton rightButton = createStyledButton("Déplacer droite");

        downBlock.add(upButton);
        downBlock.add(downButton);
        downBlock.add(leftButton);
        downBlock.add(rightButton);

        nettoyer.addActionListener(e -> {
            for (int i = 0; i < 16; i++) {
                cells[i].removeAll();
                cells[i].setBackground(gridColor);
                cells[i].revalidate();
                cells[i].repaint();
            }
            placerAlea(robotIcon);
            modeLivraisonActif = false;
        });

        eteindre.addActionListener(e -> {
            cells[curr_index].removeAll();
            cells[curr_index].add(new JLabel(robotIcon2));
            cells[curr_index].revalidate();
            cells[curr_index].repaint();
            robot.ajouterHistorique("Robot éteint.");
        });

        recharger.addActionListener(e -> {
            robot.recharger(5);  // autorise la recharge même à 0
            mettreAJourEnergie();
        });


        livraison.addActionListener(e -> {
            if (robot.getEnergie() > 0) {
                modeLivraisonActif = true;
                for (int i = 0; i < 16; i++) {
                    int index = i;
                    for (MouseListener ml : cells[i].getMouseListeners()) {
                        cells[i].removeMouseListener(ml);
                    }
                    cells[i].addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            if (modeLivraisonActif) {
                                Robot.deplacerRobotAStar(cells, curr_index, index, InterfaceGraphique.this::placerRobotALaPosition);
                                modeLivraisonActif = false;
                                robot.ajouterHistorique("Livraison effectuée à l'index " + index);
                                robot.consommerEnergie(15);
                                mettreAJourEnergie();
                            }
                        }
                    });
                }
            }
        });

        historique.addActionListener(e -> {
            java.util.List<String> historiqueActions = robot.getHistorique();
            StringBuilder sb = new StringBuilder();
            for (String action : historiqueActions) {
                sb.append("- ").append(action).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Historique du robot", JOptionPane.INFORMATION_MESSAGE);
        });

        ImageIcon dechetIcon = new ImageIcon(new ImageIcon("src/images/dechet.jpg").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        final int[] dechetIndex = {-1};
        dechet.addActionListener(e -> {
            if (dechetIndex[0] != -1) {
                cells[dechetIndex[0]].removeAll();
                cells[dechetIndex[0]].revalidate();
                cells[dechetIndex[0]].repaint();
            }
            dechetIndex[0] = (int) (Math.random() * 16);
            cells[dechetIndex[0]].add(new JLabel(dechetIcon));
            cells[dechetIndex[0]].revalidate();
            cells[dechetIndex[0]].repaint();
            robot.ajouterHistorique("Déchet apparu à l'index " + dechetIndex[0]);
        });

        ramasser.addActionListener(e -> {
            if (dechetIndex[0] != -1) {
                Robot.deplacerRobotAStar(cells, curr_index, dechetIndex[0], InterfaceGraphique.this::placerRobotALaPosition);

                Timer timer = new Timer(250 * 6, evt -> { // délai approximatif pour laisser le temps de déplacement
                    if (curr_index == dechetIndex[0]) {
                        cells[dechetIndex[0]].removeAll();
                        cells[dechetIndex[0]].revalidate();
                        cells[dechetIndex[0]].repaint();
                        robot.ajouterHistorique("Déchet récupéré à l'index " + dechetIndex[0]);
                        dechetIndex[0] = -1;
                    } else {
                        JOptionPane.showMessageDialog(this, "Le robot doit être sur le déchet pour le ramasser", "Attention", JOptionPane.WARNING_MESSAGE);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                JOptionPane.showMessageDialog(this, "Aucun déchet à récupérer", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        mainPanel.add(topBlock, BorderLayout.NORTH);
        mainPanel.add(leftBlock, BorderLayout.WEST);
        mainPanel.add(map, BorderLayout.CENTER);
        mainPanel.add(rightBlock, BorderLayout.EAST);
        mainPanel.add(downBlock, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
        mettreAJourEnergie();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new InterfaceGraphique();
        });
    }
}