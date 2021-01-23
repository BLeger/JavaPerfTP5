package org.polytechtours.javaperformance.tp.paintingants;
// package PaintingAnts_v2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

// version : 2.0

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>
 * Titre : Painting Ants
 * </p>
 * <p>
 * Description :
 * </p>
 * <p>
 * Copyright : Copyright (c) 2003
 * </p>
 * <p>
 * Société : Equipe Réseaux/TIC - Laboratoire d'Informatique de l'Université de
 * Tours
 * </p>
 *
 * @author Nicolas Monmarché
 * @version 1.0
 */

public class CPainting extends Canvas implements MouseListener {
	private static final long serialVersionUID = 1L;
	// matrice servant pour le produit de convolution
	//initialisation de la matrice de convolution : lissage moyen sur 9 cases

	/*
	 * 1 2 1
	 * 2 4 2
	 * 1 2 1
	 */
	private static final float[][] mMatriceConv9 = {{1/16f, 2/16f, 1/16f}, 
			{2/16f, 4/16f, 2/16f}, 
			{1/16f, 2/16f, 1/16f}};

	//initialisation de la matrice de convolution : lissage moyen sur 25 cases
	/*
	 * 1 1 2 1 1 
	 * 1 2 3 2 1 
	 * 2 3 4 3 2 
	 * 1 2 3 2 1 
	 * 1 1 2 1 1
	 */
	private static final float[][] mMatriceConv25 = {{1/44f, 1/44f, 2/44f, 1/44f, 1/44f},
			{1/44f, 2/44f, 3/44f, 2/44f, 1/44f},
			{2/44f, 3/44f, 4/44f, 3/44f, 2/44f},
			{1/44f, 2/44f, 3/44f, 2/44f, 1/44f},
			{1/44f, 1/44f, 2/44f, 1/44f, 1/44f}};


	//initialisation de la matrice de convolution : lissage moyen sur 49
	// cases
	/*
	 * 1 1 2 2 2 1 1
	 * 1 2 3 4 3 2 1
	 * 2 3 4 5 4 3 2
	 * 2 4 5 8 5 4 2
	 * 2 3 4 5 4 3 2
	 * 1 2 3 4 3 2 1
	 * 1 1 2 2 2 1 1
	 */
	private static final float[][] mMatriceConv49 = {{1/128f, 1/128f, 2/128f, 2/128f, 2/128f, 1/128f, 1/128f},
			{1/128f, 2/128f, 3/128f, 4/128f, 3/128f, 2/128f, 1/128f},
			{2/128f, 3/128f, 4/128f, 5/128f, 4/128f, 3/128f, 2/128f},
			{2/128f, 4/128f, 5/128f, 8/128f, 5/128f, 4/128f, 2/128f},
			{2/128f, 3/128f, 4/128f, 5/128f, 4/128f, 3/128f, 2/128f},
			{1/128f, 2/128f, 3/128f, 4/128f, 3/128f, 2/128f, 1/128f},
			{1/128f, 1/128f, 2/128f, 2/128f, 2/128f, 1/128f, 1/128f}};

	// Objet de type Graphics permettant de manipuler l'affichage du Canvas
	private Graphics mGraphics;
	// Objet ne servant que pour les bloc synchronized pour la manipulation du
	// tableau des couleurs
	private Object mMutexCouleurs = new Object();
	private Object mMutexGraphics = new Object();
	// tableau des couleurs, il permert de conserver en memoire l'état de chaque
	// pixel du canvas, ce qui est necessaire au deplacemet des fourmi
	// il sert aussi pour la fonction paint du Canvas
	private List<Color> concurrentColors = new ArrayList<Color>(); 
	// couleur du fond
	private static final Color mCouleurFond = new Color(255, 255, 255);
	// dimensions
	private Dimension mDimension = new Dimension();

	private PaintingAnts mApplis;

	private boolean mSuspendu = false;

	/******************************************************************************
	 * Titre : public CPainting() Description : Constructeur de la classe
	 ******************************************************************************/
	public CPainting(Dimension pDimension, PaintingAnts pApplis) {
		addMouseListener(this);

		mApplis = pApplis;

		mDimension = pDimension;
		setBounds(new Rectangle(0, 0, mDimension.width, mDimension.height));

		this.setBackground(mCouleurFond);
		// initialisation de la matrice des couleurs
		for (int j = 0; j != mDimension.height; j++) {
			for (int i = 0; i != mDimension.width; i++) {
				concurrentColors.add(new Color(mCouleurFond.getRed(), mCouleurFond.getGreen(), mCouleurFond.getBlue()));
			}
		}
	}

	/******************************************************************************
	 * Titre : Color getCouleur Description : Cette fonction renvoie la couleur
	 * d'une case
	 ******************************************************************************/
	public Color getCurrentCouleur(int x, int y) {
		return concurrentColors.get(y * mDimension.width + x);
	}

	public void setCurrentCouleur(int x, int y, Color c) {
		concurrentColors.set(y * mDimension.width + x, c);
	}

	/******************************************************************************
	 * Titre : Color getDimension Description : Cette fonction renvoie la
	 * dimension de la peinture
	 ******************************************************************************/
	public Dimension getDimension() {
		return mDimension;
	}

	/******************************************************************************
	 * Titre : Color getHauteur Description : Cette fonction renvoie la hauteur de
	 * la peinture
	 ******************************************************************************/
	public int getHauteur() {
		return mDimension.height;
	}

	/******************************************************************************
	 * Titre : Color getLargeur Description : Cette fonction renvoie la hauteur de
	 * la peinture
	 ******************************************************************************/
	public int getLargeur() {
		return mDimension.width;
	}

	/******************************************************************************
	 * Titre : void init() Description : Initialise le fond a la couleur blanche
	 * et initialise le tableau des couleurs avec la couleur blanche
	 ******************************************************************************/
	public void init() {
		mGraphics = getGraphics();
		synchronized (mMutexCouleurs) {
			mGraphics.clearRect(0, 0, mDimension.width, mDimension.height);

			// initialisation de la matrice des couleurs
			for (int j = 0; j != mDimension.height; j++) {
				for (int i = 0; i != mDimension.width; i++) {
					concurrentColors.add(new Color(mCouleurFond.getRed(), mCouleurFond.getGreen(), mCouleurFond.getBlue()));
				}
			}
		}

		mSuspendu = false;
	}

	/****************************************************************************/
	public void mouseClicked(MouseEvent pMouseEvent) {
		pMouseEvent.consume();
		if (pMouseEvent.getButton() == MouseEvent.BUTTON1) {
			// double clic sur le bouton gauche = effacer et recommencer
			if (pMouseEvent.getClickCount() == 2) {
				init();
			}
			// simple clic = suspendre les calculs et l'affichage
			mApplis.pause();
		} else {
			// bouton du milieu (roulette) = suspendre l'affichage mais
			// continuer les calculs
			if (pMouseEvent.getButton() == MouseEvent.BUTTON2) {
				suspendre();
			} else {
				// clic bouton droit = effacer et recommencer
				// case pMouseEvent.BUTTON3:
				init();
			}
		}
	}

	/****************************************************************************/
	public void mouseEntered(MouseEvent pMouseEvent) {
	}

	/****************************************************************************/
	public void mouseExited(MouseEvent pMouseEvent) {
	}

	/****************************************************************************/
	public void mousePressed(MouseEvent pMouseEvent) {

	}

	/****************************************************************************/
	public void mouseReleased(MouseEvent pMouseEvent) {
	}

	/******************************************************************************
	 * Titre : void paint(Graphics g) Description : Surcharge de la fonction qui
	 * est appelé lorsque le composant doit être redessiné
	 ******************************************************************************/
	@Override
	public void paint(Graphics pGraphics) {
		int i, j;

		synchronized (mMutexCouleurs) {
			for (i = 0; i < mDimension.width; i++) {
				for (j = 0; j < mDimension.height; j++) {
					pGraphics.setColor(getCurrentCouleur(i, j));
					pGraphics.fillRect(i, j, 1, 1);
				}
			}
		}
	}

	public float[][] getMatriceFlou(int size) {
		if (size == 3) {
			return CPainting.mMatriceConv9;
		}
		if (size == 5) {
			return CPainting.mMatriceConv25;
		}

		return CPainting.mMatriceConv49;
	}

	public int getCoeffConvolution(int size) {
		if (size == 3) {
			return 1;
		}
		if (size == 5) {
			return 2;
		}

		return 3;
	}

	public void convolution(int x, int y, int size) {
		float R, G, B;
		int m, n;
		Color lColor;

		final float[][] matriceFlou = getMatriceFlou(size);
		int coeff = getCoeffConvolution(size);

		synchronized (mMutexCouleurs) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					R = G = B = 0f;
					for (int k = 0; k < size; k++) {
						for (int l = 0; l < size; l++) {
							m = (x + i + k - (coeff * 2) + mDimension.width) % mDimension.width;
							n = (y + j + l - (coeff * 2) + mDimension.height) % mDimension.height;
							final Color color = getCurrentCouleur(m, n);
							R += matriceFlou[k][l] * color.getRed();
							G += matriceFlou[k][l] * color.getGreen();
							B += matriceFlou[k][l] * color.getBlue();
						}
					}
					lColor = new Color((int) R, (int) G, (int) B);
					
					m = (x + i - coeff + mDimension.width) % mDimension.width;
					n = (y + j - coeff + mDimension.height) % mDimension.height;
					setCurrentCouleur(m, n, lColor);
				}
			}
		}
		
		synchronized (mMutexGraphics) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					int u = (x + i - (coeff * 2) + mDimension.width) % mDimension.width;
					int v = (y + j - (coeff * 2) + mDimension.height) % mDimension.height;
					Color c = getCurrentCouleur(u, v);
					mGraphics.setColor(c);
					if (!mSuspendu) {
						mGraphics.fillRect(u, v, 1, 1);
					}
				}
			}
		}
	}

	/******************************************************************************
	 * Titre : void setCouleur(int x, int y, int pTaille) Description : Cette
	 * fonction va colorer le pixel correspondant et mettre a jour le tableau des
	 * couleurs
	 ******************************************************************************/
	public void setCouleur(int x, int y, Color c, int pTaille) {

		synchronized (mMutexCouleurs) {
			setCurrentCouleur(x, y, c);
		}

		if (pTaille != 0) {
			convolution(x, y, pTaille * 2 + 1);
		}
	}

	/******************************************************************************
	 * Titre : suspendre Description : Cette fonction change l'état de suspension
	 ******************************************************************************/
	public void suspendre() {
		mSuspendu = !mSuspendu;
		if (!mSuspendu) {
			repaint();
		}
	}
}
