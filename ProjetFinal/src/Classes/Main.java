package Classes;

import Exceptions.RobotException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RobotLivraison robot = new RobotLivraison("R2-D2", 0, 0);

        System.out.println("=== Système de gestion de robot de livraison ===");

        boolean quitter = false;
        while (!quitter) {
            System.out.println("\nOptions:");
            System.out.println("1. Démarrer le robot");
            System.out.println("2. Arrêter le robot");
            System.out.println("3. Connecter le robot");
            System.out.println("4. Déconnecter le robot");
            System.out.println("5. Effectuer une tâche");
            System.out.println("6. Recharger le robot");
            System.out.println("7. Afficher l'état du robot");
            System.out.println("8. Afficher l'historique");
            System.out.println("9. Quitter");
            System.out.print("Choix: ");

            int choix = scanner.nextInt();
            scanner.nextLine(); // Consommer le retour à la ligne

            try {
                switch (choix) {
                    case 1:
                        robot.demarrer();
                        System.out.println("Classes.Robot démarré avec succès");
                        break;
                    case 2:
                        robot.arreter();
                        System.out.println("Classes.Robot arrêté");
                        break;
                    case 3:
                        System.out.print("Entrez le nom du réseau: ");
                        String reseau = scanner.nextLine();
                        robot.connecter(reseau);
                        System.out.println("Connecté au réseau " + reseau);
                        break;
                    case 4:
                        robot.deconnecter();
                        System.out.println("Déconnecté du réseau");
                        break;
                    case 5:
                        robot.effectuerTache();
                        break;
                    case 6:
                        System.out.print("Quantité d'énergie à ajouter (1-100): ");
                        int quantite = scanner.nextInt();
                        robot.recharger(quantite);
                        System.out.println("Classes.Robot rechargé");
                        break;
                    case 7:
                        System.out.println(robot);
                        break;
                    case 8:
                        System.out.println("\n=== Historique ===");
                        System.out.println(robot.getHistorique());
                        break;
                    case 9:
                        quitter = true;
                        break;
                    default:
                        System.out.println("Option invalide");
                }
            } catch (RobotException e) {
                System.out.println("Erreur: " + e.getMessage());
            }
        }

        System.out.println("Programme terminé");
        scanner.close();
    }
}