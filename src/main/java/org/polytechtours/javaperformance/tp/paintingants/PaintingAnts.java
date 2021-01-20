package org.polytechtours.javaperformance.tp.paintingants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Timer;

public class PaintingAnts extends java.applet.Applet implements Runnable {
	private static final long serialVersionUID = 1L;
	// parametres
	private int mLargeur;
	private int mHauteur;

	// l'objet graphique lui meme
	private CPainting mPainting;

	// les fourmis
	private Vector<CFourmi> mColonie;
	private CColonie mColony;

	private Thread mApplis, mThreadColony;

	private Dimension mDimension;
	private long mCompteur = 0;
	private Object mMutexCompteur = new Object();
	private boolean mPause = false;

	public BufferedImage mBaseImage;
	private Timer fpsTimer;

	/** Fourmis per second :) */
	private Long fpsCounter = 0L;
	/** stocke la valeur du compteur lors du dernier timer */
	private Long lastFps = 0L;

	/****************************************************************************/
	/**
	 * incrémenter le compteur
	 *
	 */
	public void compteur() {
		synchronized (mMutexCompteur) {
			mCompteur++;
		}
	}

	/****************************************************************************/
	/**
	 * Détruire l'applet
	 *
	 */
	@Override
	public void destroy() {
		// System.out.println(this.getName()+ ":destroy()");

		if (mApplis != null) {
			mApplis = null;
		}
	}

	/****************************************************************************/
	/**
	 * Obtenir l'information Applet
	 *
	 */
	@Override
	public String getAppletInfo() {
		return "Painting Ants";
	}

	/****************************************************************************/
	/**
	 * Obtenir l'information Applet
	 *
	 */

	@Override
	public String[][] getParameterInfo() {
		String[][] lInfo = { { "SeuilLuminance", "string", "Seuil de luminance" }, { "Img", "string", "Image" },
				{ "NbFourmis", "string", "Nombre de fourmis" }, { "Fourmis", "string",
				"Paramètres des fourmis (RGB_déposée)(RGB_suivie)(x,y,direction,taille)(TypeDeplacement,ProbaG,ProbaTD,ProbaD,ProbaSuivre);...;" } };
		return lInfo;
	}

	/****************************************************************************/
	/**
	 * Obtenir l'état de pause
	 *
	 */
	public boolean getPause() {
		return mPause;
	}

	public synchronized void IncrementFpsCounter() {
		fpsCounter++;
	}

	/****************************************************************************/
	/**
	 * Initialisation de l'applet
	 *
	 */
	@Override
	public void init() {
		URL lFileName;
		URLClassLoader urlLoader = (URLClassLoader) this.getClass().getClassLoader();

		// lecture des parametres de l'applet

		mDimension = getSize();
		mLargeur = mDimension.width;
		mHauteur = mDimension.height;

		mPainting = new CPainting(mDimension, this);
		add(mPainting);

		// lecture de l'image
		lFileName = urlLoader.findResource("images/" + getParameter("Img"));
		try {
			if (lFileName != null) {
				mBaseImage = javax.imageio.ImageIO.read(lFileName);
			}
		} catch (java.io.IOException ex) {
		}

		if (mBaseImage != null) {
			mLargeur = mBaseImage.getWidth();
			mHauteur = mBaseImage.getHeight();
			mDimension.setSize(mLargeur, mHauteur);
			resize(mDimension);
		}

		readParameterFourmis();

		setLayout(null);
	}

	/****************************************************************************/
	/**
	 * Paint the image and all active highlights.
	 */
	@Override
	public void paint(Graphics g) {

		if (mBaseImage == null) {
			return;
		}
		g.drawImage(mBaseImage, 0, 0, this);
	}
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/

	/****************************************************************************/
	/**
	 * Mettre en pause
	 *
	 */
	public void pause() {
		mPause = !mPause;
		// if (!mPause)
		// {
		// notify();
		// }
	}

	// =========================================================================
	// lecture des paramètres de l'applet
	private void readParameterFourmis() {
		String seuilLuminance = getParameter("SeuilLuminance");
		String fourmis = getParameter("Fourmis");
		String nbFourmis = getParameter("NbFourmis");
		
		mColonie = new ColonieBuilder().readParameterFourmis(seuilLuminance, fourmis, nbFourmis, mPainting, this);
	}

	/*************************************************************************************************
	 * Titre : boolean testCouleur() Description : fonction testant l'égalité de
	 * deux couleurs
	 *
	 */
	@Override
	public void run() {
		// System.out.println(this.getName()+ ":run()");

		int i;
		String lMessage;

		mPainting.init();

		Thread currentThread = Thread.currentThread();

		mThreadColony.start();

		while (mApplis == currentThread) {
			if (mPause) {
				lMessage = "pause";
			} else {
				synchronized (this) {
					lMessage = "running (" + lastFps + ") ";
				}

				synchronized (mMutexCompteur) {
					mCompteur %= 10000;
					for (i = 0; i < mCompteur / 1000; i++) {
						lMessage += ".";
					}
				}

			}
			showStatus(lMessage);
			if (!mThreadColony.isAlive()) {
				stop();
				System.exit(0);
			}

			/*try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        showStatus(e.toString());
      }*/
		}
	}

	/****************************************************************************/
	/**
	 * Lancer l'applet
	 *
	 */
	@Override
	public void start() {
		// System.out.println(this.getName()+ ":start()");
		mColony = new CColonie(mColonie, this);
		mThreadColony = new Thread(mColony);
		mThreadColony.setPriority(Thread.MIN_PRIORITY);

		fpsTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateFPS();
			}
		});
		fpsTimer.setRepeats(true);
		fpsTimer.start();

		showStatus("starting...");
		// Create the thread.
		mApplis = new Thread(this);
		// and let it start running
		mApplis.setPriority(Thread.MIN_PRIORITY);
		mApplis.start();
	}

	/****************************************************************************/
	/**
	 * Arrêter l'applet
	 *
	 */
	@Override
	public void stop() {
		showStatus("stopped...");

		fpsTimer.stop();

		// On demande au Thread Colony de s'arreter et on attend qu'il s'arrete
		mColony.pleaseStop();
		try {
			mThreadColony.join();
		} catch (Exception e) {
		}

		mThreadColony = null;
		mApplis = null;
	}

	/**
	 * update Fourmis per second
	 */
	private synchronized void updateFPS() {
		lastFps = fpsCounter;
		fpsCounter = 0L;
	}
}
