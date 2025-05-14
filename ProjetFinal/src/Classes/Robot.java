// File: Classes/Robot.java
package Classes;


import Exceptions.EnergieInsuffisanteException;
import Exceptions.MaintenanceRequiseException;
import Exceptions.RobotException;

import java.time.LocalDateTime;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.Color;
import java.util.function.IntConsumer;


public abstract class Robot {
    protected String id = "";
    protected int x, y;
    protected int energie = 100;
    protected int heuresUtilisation = 0;
    protected boolean enMarche = false;
    protected ArrayList<String> historiqueActions = new ArrayList<>();
    protected JProgressBar barreEnergie;

    public Robot(String id, int x, int y, int energie, boolean enMarche, ArrayList<String> historiqueActions) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = energie;
        this.enMarche = enMarche;
        this.historiqueActions = historiqueActions != null ? historiqueActions : new ArrayList<>();
        this.ajouterHistorique("Robot Crée");
        this.barreEnergie = new JProgressBar(0, 100);
        this.barreEnergie.setValue(energie);
        this.barreEnergie.setStringPainted(true);
    }

    public JProgressBar getBarreEnergie() {
        return barreEnergie;
    }

    public int getEnergie() {
        return energie;
    }
        public boolean getEnMarche() {
        return enMarche;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void ajouterHistorique(String action) {
        String message = this.getDate() + " " + action;
        System.out.println(message);
        this.historiqueActions.add(message);
    }

    public void verifierEnergie(int energieRequise) throws EnergieInsuffisanteException {
        if (this.energie < energieRequise) {
            throw new EnergieInsuffisanteException("Énergie insuffisante. Requis: " + energieRequise + "%");
        }
    }

    public void verifierMaintenance() throws MaintenanceRequiseException {
        if (this.heuresUtilisation >= 100) {
            throw new MaintenanceRequiseException("Maintenance requise après " + heuresUtilisation + " heures d'utilisation");
        }
    }

    public void demarrer() throws RobotException {
        try {
            verifierEnergie(10);
            enMarche = true;
            ajouterHistorique("Démarrage du robot");
        } catch (EnergieInsuffisanteException e) {
            ajouterHistorique("Échec du démarrage: " + e.getMessage());
            throw new RobotException("Impossible de démarrer le robot: " + e.getMessage());
        }
    }

    public void arreter() {
        enMarche = false;
        ajouterHistorique("Arrêt du robot");
    }

    public void consommerEnergie(int quantite) {
        energie = Math.max(0, energie - quantite);
        if (barreEnergie != null) {
            barreEnergie.setValue(energie);
        }
    }

    public void recharger(int quantite) {
        energie = Math.min(100, energie + quantite);
        ajouterHistorique("Rechargé de " + quantite + "% -> " + energie + "%");
        if (barreEnergie != null) {
            barreEnergie.setValue(energie);
        }
    }

    public abstract void deplacer(int dx, int dy) throws RobotException;
    public abstract void effectuerTache() throws RobotException;

    public ArrayList<String> getHistorique() {
        return historiqueActions;
    }

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Position: (%d,%d), Énergie: %d%%, Heures: %d]",
                this.getClass().getSimpleName(), id, x, y, energie, heuresUtilisation);
    }

    public String getDate() {
        List<String> months = Arrays.asList("janvier", "février", "mars", "avril", "mai", "juin",
                "juillet", "août", "septembre", "octobre", "novembre", "décembre");
        LocalDateTime now = LocalDateTime.now();
        return now.getDayOfMonth() + " " + months.get(now.getMonthValue() - 1) + " " + now.getYear()
                + " " + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond();
    }

    // A* Pathfinding (grid 4x4 assumed)
    public static List<Integer> astar(int start, int goal) {
        class Node implements Comparable<Node> {
            int index;
            int g;
            int f;
            Node parent;

            Node(int index, int g, int f, Node parent) {
                this.index = index;
                this.g = g;
                this.f = f;
                this.parent = parent;
            }

            @Override
            public int compareTo(Node o) {
                return Integer.compare(this.f, o.f);
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Integer> closedSet = new HashSet<>();
        openSet.add(new Node(start, 0, heuristic(start, goal), null));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.index == goal) {
                List<Integer> path = new ArrayList<>();
                while (current != null) {
                    path.add(0, current.index);
                    current = current.parent;
                }
                return path;
            }

            closedSet.add(current.index);

            for (int neighbor : getNeighbors(current.index)) {
                if (closedSet.contains(neighbor)) continue;
                int g = current.g + 1;
                int f = g + heuristic(neighbor, goal);
                openSet.add(new Node(neighbor, g, f, current));
            }
        }

        return Collections.emptyList();
    }

    private static int heuristic(int a, int b) {
        int ax = a % 4, ay = a / 4;
        int bx = b % 4, by = b / 4;
        return Math.abs(ax - bx) + Math.abs(ay - by);
    }

    private static List<Integer> getNeighbors(int index) {
        int x = index % 4;
        int y = index / 4;
        List<Integer> neighbors = new ArrayList<>();
        if (x > 0) neighbors.add(index - 1);
        if (x < 3) neighbors.add(index + 1);
        if (y > 0) neighbors.add(index - 4);
        if (y < 3) neighbors.add(index + 4);
        return neighbors;
    }

    public static void deplacerRobotAStar(JPanel[] cells, int start, int goal, IntConsumer placer, Runnable onComplete) {
        List<Integer> path = astar(start, goal);
        if (path.isEmpty()) {
            // No path found
            return;
        }
        
        Timer timer = new Timer(250, null);
        final int[] step = {0};
        
        timer.addActionListener(e -> {
            if (step[0] >= path.size()) {
                ((Timer) e.getSource()).stop();
                if (onComplete != null) {
                    onComplete.run();  // Execute callback when movement is complete
                }
                return;
            }
            
            int index = path.get(step[0]);
            Color originalColor = cells[index].getBackground();
            cells[index].setBackground(new Color(100, 255, 100));
            
            // Move the robot to the new position
            placer.accept(index);
            step[0]++;
            
            // Restore cells to original color gradually
            Timer colorTimer = new Timer(500, colorEvent -> {
                cells[index].setBackground(originalColor);
            });
            colorTimer.setRepeats(false);
            colorTimer.start();
        });
        
        timer.start();
    }

    // Keep the old method for backward compatibility
    public static void deplacerRobotAStar(JPanel[] cells, int start, int goal, IntConsumer placer) {
        deplacerRobotAStar(cells, start, goal, placer, null);
    }
}
