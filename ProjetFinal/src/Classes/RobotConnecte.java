package Classes;

import Exceptions.EnergieInsuffisanteException;
import Exceptions.RobotException;

import java.util.ArrayList;

public abstract class RobotConnecte extends Robot {

    boolean connecte;
    String reseauConnecte;

    public RobotConnecte(String id, int x, int y, int energie, boolean enMarche, ArrayList<String> historiqueActions) {
        super(id, x, y, energie, enMarche, historiqueActions);
        connecte = false;
        reseauConnecte = null;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;

    }
    public void setY(int y) {
        this.y = y;
    }

    public void connecter(String reseau) throws RobotException {
        try {
            verifierEnergie(5);
            this.connecte = true;
            this.reseauConnecte = reseau;
            this.consommerEnergie(5);
            this.ajouterHistorique("Connecté au réseau: " + reseau);
        } catch (EnergieInsuffisanteException e) {
            ajouterHistorique("Échec de la connexion: " + e.getMessage());
            throw new RobotException("Connexion impossible: " + e.getMessage());
        }
    }

    public void deconnecter() {
        if (connecte) {
            ajouterHistorique("Déconnecté du réseau: " + reseauConnecte);
            this.reseauConnecte = null;
            this.connecte = false;
        }
    }

    public void envoyerDonnes(String donnees) throws RobotException {
        if (!connecte) {
            throw new RobotException("Classes.Robot non connecté");
        }
        try {
            verifierEnergie(3);
            consommerEnergie(3);
            ajouterHistorique("Données envoyées: " + donnees);
        } catch (EnergieInsuffisanteException e) {
            ajouterHistorique("Échec d'envoi de données: " + e.getMessage());
            throw new RobotException("Envoi de données impossible: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", Connecté: %s", connecte ? "Oui (" + reseauConnecte + ")" : "Non");
    }
}
