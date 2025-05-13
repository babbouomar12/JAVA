package Intefaces;

import Exceptions.RobotException;

public interface Conntectable {

    void connecter ( String reseau ) throws RobotException;
    void deconnecter () ;
    void envoyerDonnees( String donnees) throws RobotException;






}
