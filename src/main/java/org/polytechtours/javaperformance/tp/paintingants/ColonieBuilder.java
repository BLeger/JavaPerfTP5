package org.polytechtours.javaperformance.tp.paintingants;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;

public class ColonieBuilder {

	// =========================================================================
	// cette fonction analyse une chaine :
	// si pStr est un nombre : sa valeur est retournée
	// si pStr est un interval x..y : une valeur au hasard dans [x,y] est
	// retournée
	private float readFloatParameter(String pStr) {
		float lMin, lMax, lResult;
		// System.out.println(" chaine pStr: "+pStr);
		StringTokenizer lStrTok = new StringTokenizer(pStr, ":");
		// on lit une premiere valeur
		lMin = Float.valueOf(lStrTok.nextToken()).floatValue();
		// System.out.println(" lMin: "+lMin);
		lResult = lMin;
		// on essaye d'en lire une deuxieme
		try {
			lMax = Float.valueOf(lStrTok.nextToken()).floatValue();
			// System.out.println(" lMax: "+lMax);
			if (lMax > lMin) {
				// on choisit un nombre entre lMin et lMax
				lResult = (float) (Math.random() * (lMax - lMin)) + lMin;
			}
		} catch (java.util.NoSuchElementException e) {
			// il n'y pas de deuxieme nombre et donc le nombre retourné correspond au
			// premier nombre
		}
		return lResult;
	}

	// =========================================================================
	// cette fonction analyse une chaine :
	// si pStr est un nombre : sa valeur est retournée
	// si pStr est un interval x..y : une valeur au hasard dans [x,y] est
	// retournée
	private int readIntParameter(String pStr) {
		int lMin, lMax, lResult;
		StringTokenizer lStrTok = new StringTokenizer(pStr, ":");
		// on lit une premiere valeur
		lMin = Integer.valueOf(lStrTok.nextToken()).intValue();
		lResult = lMin;
		// on essaye d'en lire une deuxieme
		try {
			lMax = Integer.valueOf(lStrTok.nextToken()).intValue();
			if (lMax > lMin) {
				// on choisit un nombre entre lMin et lMax
				lResult = (int) (Math.random() * (lMax - lMin + 1)) + lMin;
			}
		} catch (java.util.NoSuchElementException e) {
			// il n'y pas de deuxieme nombre et donc le nombre retourné correspond au
			// premier nombre
		}
		return lResult;
	}

	public Vector<CFourmi> readParameterFourmis(String seuilLuminance, String fourmis, String nbFourmis, CPainting mPainting, PaintingAnts paintingAnts) {
		String lChaine;
		int R, G, B;
		Color lCouleurDeposee, lCouleurSuivie;
		CFourmi lFourmi;
		float lProbaTD, lProbaG, lProbaD, lProbaSuivre, lSeuilLuminance;
		char lTypeDeplacement = ' ';
		int lInitDirection, lTaille;
		float lInit_x, lInit_y;
		int lNbFourmis = -1;
		
		Vector<CFourmi> mColonie = new Vector<CFourmi>();

		// Lecture des paramètres des fourmis

		// Lecture du seuil de luminance
		// <PARAM NAME="SeuilLuminance" VALUE="N">
		// N : seuil de luminance : -1 = random(2..60), x..y = random(x..y)
		lChaine = seuilLuminance;
		if (lChaine != null) {
			lSeuilLuminance = readFloatParameter(lChaine);
		} else {
			// si seuil de luminance n'est pas défini
			lSeuilLuminance = 40f;
		}
		System.out.println("Seuil de luminance:" + lSeuilLuminance);

		// Lecture du nombre de fourmis :
		// <PARAM NAME="NbFourmis" VALUE="N">
		// N : nombre de fourmis : -1 = random(2..6), x..y = random(x..y)
		lChaine = nbFourmis;
		if (lChaine != null) {
			lNbFourmis = readIntParameter(lChaine);
		} else {
			// si le parametre NbFourmis n'est pas défini
			lNbFourmis = -1;
		}
		// si le parametre NbFourmis n'est pas défini ou alors s'il vaut -1 :
		if (lNbFourmis == -1) {
			// Le nombre de fourmis est aléatoire entre 2 et 6 !
			lNbFourmis = (int) (Math.random() * 5) + 2;
		}

		// <PARAM NAME="Fourmis"
		// VALUE="(255,0,0)(255,255,255)(20,40,1)([d|o],0.2,0.6,0.2,0.8)">
		// (R,G,B) de la couleur déposée : -1 = random(0...255); x:y = random(x...y)
		// (R,G,B) de la couleur suivie : -1 = random(0...255); x:y = random(x...y)
		// (x,y,d,t) position , direction initiale et taille du trait
		// x,y = 0.0 ... 1.0 : -1 = random(0.0 ... 1.0); x:y = random(x...y)
		// d = 7 0 1
		// 6 X 2
		// 5 4 3 : -1 = random(0...7); x:y = random(x...y)
		// t = 0, 1, 2, 3 : -1 = random(0...3); x:y = random(x...y)
		//
		// (type deplacement,proba gauche,proba tout droit,proba droite,proba
		// suivre)
		// type deplacement = o/d : -1 = random(o/d)
		// probas : -1 = random(0.0 ... 1.0); x:y = random(x...y)

		lChaine = fourmis;
		if (lChaine != null) {
			// on affiche la chaine de parametres
			System.out.println("Paramètres:" + lChaine);

			// on va compter le nombre de fourmis dans la chaine de parametres :
			lNbFourmis = 0;
			// chaine de paramètres pour une fourmi
			StringTokenizer lSTFourmi = new StringTokenizer(lChaine, ";");
			while (lSTFourmi.hasMoreTokens()) {
				// chaine de parametres de couleur et proba
				StringTokenizer lSTParam = new StringTokenizer(lSTFourmi.nextToken(), "()");
				// lecture de la couleur déposée
				StringTokenizer lSTCouleurDéposée = new StringTokenizer(lSTParam.nextToken(), ",");
				R = readIntParameter(lSTCouleurDéposée.nextToken());
				if (R == -1) {
					R = (int) (Math.random() * 256);
				}

				G = readIntParameter(lSTCouleurDéposée.nextToken());
				if (G == -1) {
					G = (int) (Math.random() * 256);
				}
				B = readIntParameter(lSTCouleurDéposée.nextToken());
				if (B == -1) {
					B = (int) (Math.random() * 256);
				}
				lCouleurDeposee = new Color(R, G, B);
				System.out.print("Parametres de la fourmi " + lNbFourmis + ":(" + R + "," + G + "," + B + ")");

				// lecture de la couleur suivie
				StringTokenizer lSTCouleurSuivi = new StringTokenizer(lSTParam.nextToken(), ",");
				R = readIntParameter(lSTCouleurSuivi.nextToken());
				G = readIntParameter(lSTCouleurSuivi.nextToken());
				B = readIntParameter(lSTCouleurSuivi.nextToken());
				lCouleurSuivie = new Color(R, G, B);
				System.out.print("(" + R + "," + G + "," + B + ")");

				// lecture de la position de la direction de départ et de la taille de
				// la trace
				StringTokenizer lSTDéplacement = new StringTokenizer(lSTParam.nextToken(), ",");
				lInit_x = readFloatParameter(lSTDéplacement.nextToken());
				if (lInit_x < 0.0 || lInit_x > 1.0) {
					lInit_x = (float) Math.random();
				}
				lInit_y = readFloatParameter(lSTDéplacement.nextToken());
				if (lInit_y < 0.0 || lInit_y > 1.0) {
					lInit_y = (float) Math.random();
				}
				lInitDirection = readIntParameter(lSTDéplacement.nextToken());
				if (lInitDirection < 0 || lInitDirection > 7) {
					lInitDirection = (int) (Math.random() * 8);
				}
				lTaille = readIntParameter(lSTDéplacement.nextToken());
				if (lTaille < 0 || lTaille > 3) {
					lTaille = (int) (Math.random() * 4);
				}
				System.out.print("(" + lInit_x + "," + lInit_y + "," + lInitDirection + "," + lTaille + ")");

				// lecture des probas
				StringTokenizer lSTProbas = new StringTokenizer(lSTParam.nextToken(), ",");
				lTypeDeplacement = lSTProbas.nextToken().charAt(0);
				// System.out.println(" lTypeDeplacement:"+lTypeDeplacement);

				if (lTypeDeplacement != 'o' && lTypeDeplacement != 'd') {
					if (Math.random() < 0.5) {
						lTypeDeplacement = 'o';
					} else {
						lTypeDeplacement = 'd';
					}
				}

				lProbaG = readFloatParameter(lSTProbas.nextToken());
				lProbaTD = readFloatParameter(lSTProbas.nextToken());
				lProbaD = readFloatParameter(lSTProbas.nextToken());
				lProbaSuivre = readFloatParameter(lSTProbas.nextToken());
				// on normalise au cas ou
				float lSomme = lProbaG + lProbaTD + lProbaD;
				lProbaG /= lSomme;
				lProbaTD /= lSomme;
				lProbaD /= lSomme;

				System.out.println(
						"(" + lTypeDeplacement + "," + lProbaG + "," + lProbaTD + "," + lProbaD + "," + lProbaSuivre + ");");

				// création de la fourmi
				lFourmi = new CFourmi(lCouleurDeposee, lCouleurSuivie, lProbaTD, lProbaG, lProbaD, lProbaSuivre, mPainting,
						lTypeDeplacement, lInit_x, lInit_y, lInitDirection, lTaille, lSeuilLuminance, paintingAnts);
				mColonie.addElement(lFourmi);
				lNbFourmis++;
			}
		} else // initialisation aléatoire des fourmis
		{

			int i;
			Color lTabColor[] = new Color[lNbFourmis];
			int lColor;

			// initialisation aléatoire de la couleur de chaque fourmi
			for (i = 0; i < lNbFourmis; i++) {
				R = (int) (Math.random() * 256);
				G = (int) (Math.random() * 256);
				B = (int) (Math.random() * 256);
				lTabColor[i] = new Color(R, G, B);
			}

			// construction des fourmis
			for (i = 0; i < lNbFourmis; i++) {
				// la couleur suivie est la couleur d'une autre fourmi
				lColor = (int) (Math.random() * lNbFourmis);
				if (i == lColor) {
					lColor = (lColor + 1) % lNbFourmis;
				}

				// une chance sur deux d'avoir un déplacement perpendiculaire
				if ((float) Math.random() < 0.5f) {
					lTypeDeplacement = 'd';
				} else {
					lTypeDeplacement = 'o';
				}

				// position initiale
				lInit_x = (float) (Math.random()); // *mPainting.getLargeur()
				lInit_y = (float) (Math.random()); // *mPainting.getHauteur()

				// direction initiale
				lInitDirection = (int) (Math.random() * 8);

				// taille du trait
				lTaille = (int) (Math.random() * 4);

				// proba de déplacement :
				lProbaTD = (float) (Math.random());
				lProbaG = (float) (Math.random() * (1.0 - lProbaTD));
				lProbaD = (float) (1.0 - (lProbaTD + lProbaG));
				lProbaSuivre = (float) (0.5 + 0.5 * Math.random());

				System.out.print(
						"Random:(" + lTabColor[i].getRed() + "," + lTabColor[i].getGreen() + "," + lTabColor[i].getBlue() + ")");
				System.out.print("(" + lTabColor[lColor].getRed() + "," + lTabColor[lColor].getGreen() + ","
						+ lTabColor[lColor].getBlue() + ")");
				System.out.print("(" + lInit_x + "," + lInit_y + "," + lInitDirection + "," + lTaille + ")");
				System.out.println(
						"(" + lTypeDeplacement + "," + lProbaG + "," + lProbaTD + "," + lProbaD + "," + lProbaSuivre + ");");

				// création et ajout de la fourmi dans la colonie
				lFourmi = new CFourmi(lTabColor[i], lTabColor[lColor], lProbaTD, lProbaG, lProbaD, lProbaSuivre, mPainting,
						lTypeDeplacement, lInit_x, lInit_y, lInitDirection, lTaille, lSeuilLuminance, paintingAnts);
				mColonie.addElement(lFourmi);
			}
		}
		
		return mColonie;
	}

}
