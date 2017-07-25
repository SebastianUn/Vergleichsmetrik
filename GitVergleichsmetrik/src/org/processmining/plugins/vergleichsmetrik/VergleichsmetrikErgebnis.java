package org.processmining.plugins.vergleichsmetrik;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.guete.Generalisierung;
import org.processmining.plugins.guete.GueteErgebnis;
import org.processmining.plugins.guete.Praezision;
import org.processmining.plugins.guete.PraezisionGewichtet;
import org.processmining.plugins.guete.Struktur;
import org.processmining.plugins.guete.Vollstaendigkeit;
import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikEinstellungen;


/**
 * Klasse in dem das Ergebnis der Vergleichsmetrik Gespeichert wird. 
 * @author Sebastian Reiners
 *
 */
public class VergleichsmetrikErgebnis {


	private PluginContext Kontext;
	
	// Default-Werte f¸r alle Dimensionen
	private double guete = 0;
	private double VW = 0; // Vollstaendigkeit
	private double PW = 0; // Praezision
	private double GW = 0; // Generalisierung
	private double SW = 0; // Struktur 
	private double PGW = 0; // Praezision gewichtet
	private GueteErgebnis Ergebnis;
	private XLog Log;
	private Petrinet Netz;
	
	private VergleichsmetrikEinstellungen Einstellungen = new VergleichsmetrikEinstellungen();
	
	// Konstruktoren \\

	/**
	 * Konstruktor mit Moeglichkeit die Einstellungen zu veraendern.
	 * @param kontext Plugin Kontext
	 * @param ergebnis Ergebnis-Objekt der Klasse
	 */
	public VergleichsmetrikErgebnis (PluginContext kontext, GueteErgebnis ergebnis) {
		this.Ergebnis = ergebnis;
		this.Kontext = kontext;
	}
	
	// Methoden \\
	
	/**
	 * Berechnet alle Dimensionen der Guete und speichert das Ergebnis in dem Ergebnis-Objekt der Klasse. 
	 * Die Dimension werden nacheinander berechnet und das Ergebnis-Objekt wird nach jeder Berechneten Dimension um die Werte
	 * in dieser Dimension erweitert. 
	 * @param log Event Log
	 * @param netz Petri-Netz aus Event Log
	 * @param markierung Markierung des Petri-Netzes
	 */
	public synchronized void BerechneGuete (XLog log, Petrinet netz, Marking markierung) {
		
		Kontext.log(PruefeEingaben(log, netz, markierung), MessageLevel.NORMAL);
		
		/*
		 * Speichere Gewichtungen in Ergebnis 
		 */
		Ergebnis.addInfo(Ergebnis.GEWICHTUNGGEWICHTETEPRAEZISION, "" + Einstellungen.getGewichtungGewichtetePraezision());
		Ergebnis.addInfo(Ergebnis.GEWICHTUNGGENERALSISIERUNG, "" + Einstellungen.getGewichtungGeneralisierung()); 
		Ergebnis.addInfo(Ergebnis.GEWICHTUNGSTRUKTUR, "" + Einstellungen.getGewichungStruktur());
		
		/*
		 * Speichere Event Log und Petri-Netz fuer Ergebnis
		 */
		setLog(log);
		setNetz(netz);
		
		/*
		 * Vollstaendigkeit
		 */
		Vollstaendigkeit V = new Vollstaendigkeit (Kontext, Ergebnis);
		try {
		Kontext.log("Vollstaendigkeit wird berechnet.", MessageLevel.NORMAL);
		V.berechne(log, netz, markierung); 
		VW = V.getVW();
		} catch (Exception e) {
			Kontext.log("Vollstaendigkeit konnte nicht berechnet werden. Der Fehler war: " + e.getMessage());
			VW = 0;
			Ergebnis = V.getErgebnis(); // Erhalte das Ergebnis-Objekt, um alle Ergebnisse in einem einzigen Objekt zu haben
		}
		
		/*
		 * Praezision
		 */
		Praezision P = new Praezision(Kontext, Ergebnis);
		try {
			Kontext.log("Praezision wird berechnet.", MessageLevel.NORMAL);
			P.berechne(log, netz, markierung); 
			PW = P.getPW();
		} catch (Exception e) {
			Kontext.log("Praezision konnte nicht berechnet werden. Der Fehler war: " + e.getMessage());
			PW = 0;
			Ergebnis = P.getErgebnis();
		}
		
		/*
		 * Generalisierung
		 */
		Generalisierung G = new Generalisierung(Kontext, Ergebnis);
		
		try {
			Kontext.log("Generalisierung wird berechnet.", MessageLevel.NORMAL);
			G.berechne(log, netz, markierung); 
			GW = G.getGW();
		} catch (Exception e) {
			Kontext.log("Generalisierung konnte nicht berechnet werden. Der Fehler war: " + e.getMessage());
			GW = 0;
			Ergebnis = G.getErgebnis();
		}
		
		/*
		 * Struktur
		 */
		Struktur S = new Struktur(Kontext, Ergebnis, Einstellungen.getGewichtungSimpleAdvanced());
		try {
			Kontext.log("Struktur wird berechnet.", MessageLevel.NORMAL);
			S.berechne(log, netz, markierung); 
			SW = S.getSW();
		} catch (Exception e) {
			Kontext.log("Struktur konnte nicht berechnet werden. Der Fehler war: " + e.getMessage());
			SW = 0;
			Ergebnis = S.getErgebnis();
		}
		
		/*
		 * Gewichtete Praezision
		 */
		PraezisionGewichtet PG = new PraezisionGewichtet(Einstellungen.getGewichtungGeneralisierung(), Ergebnis, Kontext);
		Kontext.log("Gewichtete Praezision wird berechnet.", MessageLevel.NORMAL);
		PGW = PG.SetzeWert(PW, GW);
		Kontext.log("Gewichtete Praezision hat den Wert: " + PGW + System.lineSeparator(), MessageLevel.NORMAL);
		Ergebnis = PG.getErgebnis();
		
		/*
		 * Guete
		 */
		guete = VW - (Einstellungen.getGewichungStruktur() * (1 - SW)) - (Einstellungen.getGewichtungGewichtetePraezision() * (1 - PGW));
		
		/*
		 * Schlechte Gewichtungen koennen dazu fuehren, dass die Guete < 0 oder > 1 ist, in dem Falle wird die guete auf 0 bzw. 1 gesetzt
		 */
		ueberpruefeGuete();
		
		// Werte in GueteErgebnis einfuegen, Wert wird gerundet
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMinimumFractionDigits(4);
		Ergebnis.addInfo(Ergebnis.GUETE,  nf.format(guete));
		
		Kontext.log("Alle Dimensionen wurden berechnet. Die Werte sind: " + System.lineSeparator() 
		+ "Vollstaendigkeit: " + VW + System.lineSeparator() + "Praezision: " + PW + System.lineSeparator()
		+ "Generalisierung: " + GW + System.lineSeparator() + "Struktur: " + SW + System.lineSeparator()
		+ "Gewichtete Praezision: " + PGW + "."
		, MessageLevel.TEST);
		Kontext.log("Die Guete hat den Wert: " + nf.format(guete), MessageLevel.NORMAL);
		
		// ErstelleErbebnis();
		
	}

	/**
	 * Ueberprueft die Eingaben auf Gueltigkeit.
	 * @param log Event Log
	 * @param netz Petri-Netz aus Event Log
	 * @param markierung Markierung des Petri-Netzes
	 * @return String mit Informationen welche Eingaben gueltig oder nicht gueltig sind.
	 */
	private String PruefeEingaben (XLog log, Petrinet netz, Marking markierung) {
		String ausgabe = "Die Eingaben wurden ueberprueft. Das Ergebnis war: " + System.lineSeparator();
		
		if (log.equals(null)) {
			ausgabe = ausgabe + "Der Event Log ist gleich 'null'." + System.lineSeparator();
		} else {
			ausgabe = ausgabe + "Der Event Log ist gueltig." + System.lineSeparator();
		}
		
		if (netz.equals(null)) {
			ausgabe = ausgabe + "Das Petri-Netz ist gleich 'null'." + System.lineSeparator();
		} else {
			ausgabe = ausgabe + "Das Petri-Netz ist gueltig." + System.lineSeparator();
		}
		
		if (markierung.equals(null)) {
			ausgabe = ausgabe + "Die Markierung ist gleich 'null'.";
		} else {
			ausgabe = ausgabe + "Die Markierung ist gueltig.";
		}
		
		return ausgabe;
	}
	
	/**
	 * Ueberpruefe ob die Guete zwischen 0 und 1 liegt. Tut sie dies nicht, so setze sie auf 1, wenn sie groeﬂer als 1 ist und auf 0, wenn sie kleiner als 0 ist.
	 * Bemerke auﬂerdem in dem Ergebnis-Objekt, dass die guete auﬂerhalb von 0 und 1 lag.
	 */
	private void ueberpruefeGuete () {
		
		if (guete < 0) {
			guete = 0;
			Kontext.log("Die ausgew‰hlten Gewichtungen fuerten dazu, dass die berechnete Guete auﬂerhalb von"
						+ "0 und 1 liegt. Die Guete wurde trotzdem berechnet. Werte kleiner 0 wurden zu 0, Werte grˆﬂer als 1 wurden zu 1." );
			Ergebnis.addInfo(Ergebnis.BEMERKUNGEN, "Berechnete Guete auﬂerhalb von 0 und 1");
		} else {
			if (guete > 1) {
				guete = 1;
				Kontext.log("Die ausgew‰hlten Gewichtungen fuerten dazu, dass die berechnete Guete auﬂerhalb von"
						+ "0 und 1 liegt. Die Guete wurde trotzdem berechnet. Werte kleiner 0 wurden zu 0, Werte grˆﬂer als 1 wurden zu 1." );
				Ergebnis.addInfo(Ergebnis.BEMERKUNGEN, "Berechnete Guete auﬂerhalb von 0 und 1");
			}
		}
	}
	/**
	 * Erstellt eine txt-Datei des Ergebnis.
	 */
	@SuppressWarnings("unused")
	private void ErstelleErbebnis () {
		List<String> lines = new ArrayList<String>();
		lines.add(("Die folgenden Ergebnisse wurden mithilfe des Vergleichsmetrik Plugin berechnet: "));
		lines.add(System.lineSeparator());
		lines.add(System.lineSeparator() + "Vollstaendigkeit: " + VW);
		lines.add(System.lineSeparator() + "Praezision: " + PW);
		lines.add(System.lineSeparator() + "Generalisierung: " + GW);
		lines.add(System.lineSeparator() + "Struktur: " + SW);
		lines.add(System.lineSeparator() + "Gewichtete Praezision: " + PGW);
		lines.add(System.lineSeparator() + "____________________________________");
		lines.add(System.lineSeparator() + "Guete: " + guete);
		Path file = Paths.get("Das-Ergebnis");
		try {
			Files.write(file,  lines,  Charset.forName("UTF-8"));
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
	
	
	
	// Getter und Setter \\
	
	public VergleichsmetrikEinstellungen getEinstellungen() {
		return Einstellungen;
	}

	public void VergleichsmetrikEinstellungen(VergleichsmetrikEinstellungen einstellungen) {
		Einstellungen = einstellungen;
	}

	

	public double getGuete() {
		return guete;
	}

	public double getVW() {
		return VW;
	}

	public double getPW() {
		return PW;
	}

	public double getGW() {
		return GW;
	}

	public double getSW() {
		return SW;
	}

	public double getPGW() {
		return PGW;
	}

	public GueteErgebnis getErgebnis() {
		return Ergebnis;
	}

	public void setErgebnis(GueteErgebnis ergebnis) {
		Ergebnis = ergebnis;
	}

	public XLog getLog() {
		return Log;
	}

	public void setLog(XLog log) {
		Log = log;
	}

	public Petrinet getNetz() {
		return Netz;
	}

	public void setNetz(Petrinet netz) {
		Netz = netz;
	}
	
	public void setEinstellungen(VergleichsmetrikEinstellungen einstellungen) {
		Einstellungen = einstellungen;
	}
}
