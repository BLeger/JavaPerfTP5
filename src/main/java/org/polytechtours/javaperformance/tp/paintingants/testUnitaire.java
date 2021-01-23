package org.polytechtours.javaperformance.tp.paintingants;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.polytechtours.javaperformance.tp.paintingants.CFourmi.*;

public class testUnitaire {
    @Test
    void testCouleurTest(){
        Color pCouleur = new Color(208,137,164);
        Color pCouleurSuivi = new Color(8,13,9);

        float mLuminanceCouleurSuivie = 0.2426f * pCouleurSuivi.getRed() + 0.7152f * pCouleurSuivi.getGreen()
                + 0.0722f * pCouleurSuivi.getBlue();

        System.out.println(mLuminanceCouleurSuivie);

        Color pCouleur2 = new Color(195,188,190);
        float mLuminanceCouleurSuivie2 = 0.2426f * pCouleur2.getRed() + 0.7152f * pCouleur2.getGreen() + 0.0722f * pCouleur2.getBlue();
        System.out.println(mLuminanceCouleurSuivie2);

        // si seuil de luminance n'est pas défini
        float mSeuilLuminance = 40.0F;

        assertFalse(testCouleur(pCouleur, mLuminanceCouleurSuivie,mSeuilLuminance ));
        assertTrue(testCouleur(pCouleur2, mLuminanceCouleurSuivie2,mSeuilLuminance ));

    }
}
