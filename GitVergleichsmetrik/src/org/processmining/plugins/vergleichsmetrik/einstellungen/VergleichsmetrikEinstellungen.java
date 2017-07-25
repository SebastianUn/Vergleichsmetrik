package org.processmining.plugins.vergleichsmetrik.einstellungen;

/**
 * Speichert die Einstellungen des Plugins. 
 * Verwendet Werte aus {@link VerleichsmetrikKonstanten}, wenn keine neuen Werte eingegeben wurden. 
 * @author Sebastian Reiners
 *
 */
public class VergleichsmetrikEinstellungen {

	private double gewichtungGewichtetePraezision;
	private double gewichtungGeneralisierung;
	private double gewichungStruktur;
	private double gewichtungSimpleAdvanced;
	
	// Konkstruktoren \\
	
	/**
	 * Erstellt neue Einstellungen mit neuen Werten.
	 * @param gewichtungGewichtetePraezision2 Gewichtung der gewichteten Praezision
	 * @param gewichtungGeneralisierung2 Gewichtung fuer die Generalisierung
	 * @param gewichungStruktur2 Gewichtung fuer die Struktur
	 * @param gewichtungSimpleAdvanced2 Gewichtung fuer die simple und die advanced behavioral appropriateness
	 */
	public VergleichsmetrikEinstellungen (double gewichtungGewichtetePraezision2, double gewichtungGeneralisierung2, double gewichungStruktur2, double gewichtungSimpleAdvanced2) {
		setGewichtungGewichtetePraezision(gewichtungGewichtetePraezision2);
		setGewichtungGeneralisierung(gewichtungGeneralisierung2);
		setGewichungStruktur(gewichungStruktur2);
		setGewichtungSimpleAdvanced(gewichtungSimpleAdvanced2);
	}
	
	/**
	 * Erstellt Einstellungen auf Grundlagen von Konstanten.
	 */
	public VergleichsmetrikEinstellungen () {
		this.gewichtungGewichtetePraezision = VergleichsmetrikKonstanten.GEWICHTUNGGEWICHTETEPRAEZISION;
		this.gewichtungGeneralisierung = VergleichsmetrikKonstanten.GEWICHTUNGGENERALISIERUNG;
		this.gewichungStruktur = VergleichsmetrikKonstanten.GEWICHTUNGSTRUKTUR;
		this.gewichtungSimpleAdvanced = VergleichsmetrikKonstanten.GEWICHTUNGSIMPLEADVANCED;
	}

	// Methoden \\
	
	/**
	 * Erstellt einen String, der alle Werte der Einstellungen beinhaltet. 
	 */
	public String toString () {
		return "\ngewichtungGewichtetePraezision = " + gewichtungGewichtetePraezision + "\ngewichtungGeneralisierung = " + 
				gewichtungGeneralisierung + "\ngewichungStruktur = " + gewichungStruktur + "\ngewichtungSimpleAdvanced" + gewichtungSimpleAdvanced;
	}
	
	// Getter und Setter \\
	public double getGewichtungGewichtetePraezision() {
		return gewichtungGewichtetePraezision;
	}

	/**
	 * Setzt einen neuen Wert fuer die Gewichtung der gewichteten Praezision. Ist der eingegebene Wert kleiner als 0, wird dieser automatisch 0.
	 * @param gewichtungGewichtetePraezision Neuer Wert 
	 */
	public void setGewichtungGewichtetePraezision(double gewichtungGewichtetePraezision) {
		if (gewichtungGewichtetePraezision < 0) gewichtungGewichtetePraezision = 0;
		this.gewichtungGewichtetePraezision = gewichtungGewichtetePraezision;
	}

	public double getGewichtungGeneralisierung() {
		return gewichtungGeneralisierung;
	}

	/**
	 * Setzt einen neuen Wert fuer die Gewichtung der Generalisierung. Ist der eingegebene Wert kleiner als 0, wird dieser automatisch 0.
	 * @param gewichtungGeneralisierung Neuer Wert 
	 */
	public void setGewichtungGeneralisierung(double gewichtungGeneralisierung) {
		if (gewichtungGeneralisierung < 0) gewichtungGeneralisierung = 0;
		this.gewichtungGeneralisierung = gewichtungGeneralisierung;
	}

	public double getGewichungStruktur() {
		return gewichungStruktur;
	}

	/**
	 * Setzt einen neuen Wert fuer die Gewichtung der Struktur. Ist der eingegebene Wert kleiner als 0, wird dieser automatisch 0.
	 * @param gewichungStruktur Neuer Wert 
	 */
	public void setGewichungStruktur(double gewichungStruktur) {
		if (gewichungStruktur < 0) gewichungStruktur = 0;
		this.gewichungStruktur = gewichungStruktur;
	}

	public double getGewichtungSimpleAdvanced() {
		return gewichtungSimpleAdvanced;
	}

	/**
	 * Setzt einen neuen Wert fuer die Gewichtung der simple zur advanced behavioral appropriateness. Ist der eingegebene Wert kleiner als 0, wird dieser automatisch 0.
	 * @param gewichtungSimpleAdvanced Neuer Wert 
	 */
	public void setGewichtungSimpleAdvanced(double gewichtungSimpleAdvanced) {
		if (gewichtungSimpleAdvanced < 0) gewichtungSimpleAdvanced = 0;
		this.gewichtungSimpleAdvanced = gewichtungSimpleAdvanced;
	}
}
