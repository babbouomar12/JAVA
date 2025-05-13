package Classes;

import Exceptions.EnergieInsuffisanteException;
import Exceptions.MaintenanceRequiseException;
import Exceptions.RobotException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;

public class RobotLivraison extends RobotConnecte {
    private String colisActuel;
    private String destination;
    private boolean enLivraison;
    private static final int ENERGIE_LIVRAISON = 15;
    private static final int ENERGIE_CHARGEMENT = 5;

    public RobotLivraison(String id, int x, int y) {
        super(id, x, y, 0, false, new ArrayList<>());
        this.colisActuel = null;
        this.destination = null;
        this.enLivraison = false;
    }

    public RobotLivraison(String id, int x, int y, int energie) {
        super(id, x, y, energie, false, new ArrayList<>());
        this.colisActuel = null;
        this.destination = null;
        this.enLivraison = false;
    }


    @Override
    public void effectuerTache() throws RobotException {
        if (!enMarche) {
            throw new RobotException("Le robot doit être démarré pour effectuer une tâche");
        }

        Scanner scanner = new Scanner(System.in);

        if (enLivraison) {
            System.out.print("Entrez les coordonnées x de destination: ");
            int destX = scanner.nextInt();
            System.out.print("Entrez les coordonnées y de destination: ");
            int destY = scanner.nextInt();
            faireLivraison(destX, destY);
        } else {
            System.out.print("Voulez-vous charger un nouveau colis? (O/N): ");
            String reponse = scanner.next();
            scanner.nextLine(); // vider le buffer

            if (reponse.equalsIgnoreCase("O")) {
                System.out.print("Entrez la description du colis: ");
                String colis = scanner.nextLine();
                System.out.print("Entrez la destination: ");
                String dest = scanner.nextLine();
                chargerColis(colis, dest);
            } else {
                ajouterHistorique("En attente de colis");
            }
        }
    }

    private void faireLivraison(int destX, int destY) throws RobotException {
        try {
            deplacer(destX, destY);
            colisActuel = null;
            destination = null;
            enLivraison = false;
            ajouterHistorique("Livraison terminée à (" + destX + "," + destY + ")");
        } catch (RobotException e) {
            ajouterHistorique("Échec de la livraison: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void deplacer(int x, int y) throws RobotException {
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));

        if (distance > 100) {
            throw new RobotException("Distance trop grande (max 100 unités)");
        }

        try {
            verifierMaintenance();
            int energieRequise = (int) (distance * 0.3);
            verifierEnergie(energieRequise);
            consommerEnergie(energieRequise);
            int heuresAjoutees = (int) distance / 10;
            heuresUtilisation += heuresAjoutees;

            this.x = x;
            this.y = y;

            ajouterHistorique(String.format("Déplacement vers (%d,%d). Distance: %.2f, Énergie utilisée: %d%%, Temps: %dh",
                    x, y, distance, energieRequise, heuresAjoutees));
        } catch (EnergieInsuffisanteException | MaintenanceRequiseException e) {
            ajouterHistorique("Échec du déplacement: " + e.getMessage());
            throw new RobotException("Déplacement impossible: " + e.getMessage());
        }
    }

    public void chargerColis(String colis, String destination) throws RobotException {
        if (enLivraison) {
            throw new RobotException("Le robot est déjà en livraison");
        }
        if (colisActuel != null) {
            throw new RobotException("Le robot a déjà un colis");
        }

        try {
            verifierEnergie(ENERGIE_CHARGEMENT);
            consommerEnergie(ENERGIE_CHARGEMENT);

            this.colisActuel = colis;
            this.destination = destination;
            this.enLivraison = true;

            ajouterHistorique("Colis chargé: " + colis + " pour " + destination);
        } catch (EnergieInsuffisanteException e) {
            ajouterHistorique("Échec du chargement: " + e.getMessage());
            throw new RobotException("Chargement impossible: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", Colis: %s, Destination: %s",
                colisActuel != null ? colisActuel : "Aucun",
                destination != null ? destination : "Non définie");
    }


    private java.util.List<String> historique = new java.util.ArrayList<>();


    @Override
    public void ajouterHistorique(String action) {
        super.ajouterHistorique(action); // conserve la logique de date + console
    }

    @Override
    public void recharger(int energie) {
        super.recharger(energie);
    }
}

