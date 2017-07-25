package org.processmining.plugins.guete;

import java.text.NumberFormat;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

/**
 * Kombiniert die beiden Dimensionen {@link Praezision} und {@link Generalisierung}. 
 * {@link Generalisierung} dient hierbei als Ueberpruefungsmetrik für die {@link Praezision}.
 *  
 * @author Sebastian Reiners
 *
 */
public class PraezisionGewichtet {

	
	private GueteErgebnis Ergebnis;
	private PluginContext Kontext;
	
	private double PGW = 0; 		// Default-Wert
	private double Gewichtung = 1; 	// Default-Wert
	
	
	// Konstkruktoren \\ 
	
	/**
	 * Default-Konstruktor. Nicht erlaubt
	 */
	@SuppressWarnings("unused")
	private PraezisionGewichtet () {}
	
	/**
	 * Erstellt ein Objekt der Klasse {@link PraezisionGewichtet}. Benutzt die Standard-Gewichtung.
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden
	 */
	public PraezisionGewichtet (GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}
	
	/**
	 * Erstellt ein Objekt der Klasse {@link PraezisionGewichtet}. Überschreibt die aktuelle Gewichtung.
	 * @param gewichtung Gewichtung mit dem die Praezision mit der Generalisierung verrechnet wird
	 * @param ergebnis Objekt in dem die Berechnungen gespeichert werden
	 * @param kontext Plugin Kontext
	 */
	public PraezisionGewichtet (double gewichtung, GueteErgebnis ergebnis, PluginContext kontext) {
		Gewichtung = gewichtung;
		Ergebnis = ergebnis;
		Kontext = kontext;
	}
	
	// Methoden \\ 
	
	/**
	 * Erstellt aus zwei Werten mit einer gegebenen Gewichtung einen neuen Wert. Der zweite Wert wird
	 * auf einen Wert zwischen [0, 1] gebracht. 
	 * @param pw Wert der Praezision. Zwischen [0, 1]
	 * @param gw Wert der Generalisierung. Zwischen [-1, 1].
	 * @return
	 */
	public double SetzeWert (double pw, double gw) {
		
		double bestrafung = 0; // Wert mit dem die Praezision bestraft wird fuer schlechte Generalisierung
		double pgw = 0; 	   // Default-Wert
		
		if (gw == 1) {
			bestrafung = 0;
		} else if (gw == (-1)) {
			bestrafung = 1;
		} else if (gw == 0) {
			bestrafung = 0.5;
		} else {
			bestrafung = 0.5 - (gw * 0.5); 
		} 
		
		pgw = pw - (Gewichtung * bestrafung);
		
		if (pgw < 0) {
			pgw = 0;
		}
		
		PGW = pgw;
		double pgw2 = pgw;
		
		// Werte in GueteErgebnis einfuegen, Wert wird gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.PRAEZISIONGEWICHTET, nf.format(pgw2));
		Ergebnis.addInfo(Ergebnis.GEWICHTUNGGENERALSISIERUNG, Gewichtung + "");
		
		Kontext.log("Gewichtete Praezision erfolgreich berechnet", MessageLevel.TEST);
		return pgw;
	}


	

	// Getter und Setter \\
	
	/**
	 * Erhalte den aktuellen Wert der Praezision. 
	 * @return
	 */
	public double getPGW() {
		return PGW;
	}

	/**
	 * Erhalte die aktuelle Gewichtung. 
	 * @return Double der Gewichtung
	 */
	public double getGewichtung() {
		return Gewichtung;
	}

	/**
	 * Setze eine neue Gewichtung. Sollte zwischen 0 und 1 liegen, kann aber auch außerhalb liegen.
	 * @param gewichtung Neuer Wert der Gewichtung
	 */
	public void setGewichtung(double gewichtung) {
		Gewichtung = gewichtung;
	}

	/**
	 * Aktuelles Ergebnis Objekt in dem die Berechnung gespeichert wird.
	 * @return Aktuelles Ergebnis-Objekt.
	 */
	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}

	/**
	 * Setze ein neues Ergebnis Objekt.
	 * @param ergebnis
	 */
	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}
}
