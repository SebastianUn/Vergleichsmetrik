package org.processmining.plugins.guete;

import java.util.HashMap;
import java.util.Map;

/**
 * Eine Klasse in der alle Berechungen des Plugins gespeichert werden. 
 * Speichert die Ergebnisse in einer Map [<code>String, Object</code>]. 
 * @author Sebastians
 *
 */
public class GueteErgebnis {
	
	private Map<String, Object> info = new HashMap<String, Object>();
	private Map<String, Object> infoMap2 = new HashMap<String, Object>();
	
	/*
	 * Informationen ueber den Ablauf 
	 */
	public final String ZEIT = "Berechnungszeit (ms)";
	public final String BEMERKUNGEN = "Bemerkung";
	
	/*
	 * Informationen ueber das Petri-Netz 
	 */
	public final String ANZAHLTRANSITIONEN = "Anzahl Transitionen";
	public final String ANZAHLEINZIGARTIGETRANSITIONEN = "Anzahl Einzigartiger Transitionen";
	public final String ANZAHLUNSICHTBARERTRANSITIONEN = "Anzahl Unsichtbarer Transitionen";
	public final String ANZAHLSTELLEN = "Anzahl Stellen";
	
	/*
	 * Informationen ueber den Event Log 
	 */
	public final String VERWENDETECLASSIFIER = "Verwendete Classifier"; // Standard_Classifier oder log.getClassifiers().get(0)
	public final String ANZAHLSEQUENZEN = "Anzahl Sequenzen"; 		   // Traces
	
	/*
	 * Informationen ueber die verwendeten Einstellungen 
	 */
	public final String GEWICHTUNGGEWICHTETEPRAEZISION = "Gewichtung Gewichtete Praezision";
	public final String GEWICHTUNGGENERALSISIERUNG = "Gewichtung Generalisierung";
	public final String GEWICHTUNGSTRUKTUR = "Gewichtung Struktur";
	public final String GEWICHTUNGSIMPLEADVANCED = "Gewichtung Simple zu Advanced";
	
	/*
	 * Werte aus der Dimension Vollstaendigkeit
	 */
	public final String PRODUCEDTOKENS = "Produced Tokens";
	public final String MISSINGTOKENS = "Missing Tokens";
	public final String CONSUMEDTOKENS = "Consumend Tokens";
	public final String REMAININGTOKENS = "Remaining Tokens";
	public final String VOLLSTAENDIGKEIT = "Vollstaendigkeit";
	
	/*
	 * Werte aus der Dimension Praezision
	 */
	public final String PRAEZISION = "Praezision";
	public final String DURCHSCHNITTLICHEABWEICHUNG = "Durchschnittliche Abweichung";
	public final String DURCHSCHNITTLICHEBESTRAFUNG = "Durchschnittliche Bestrafung";
	
	/*
	 * Werte aus der Dimension Generalisierung
	 */
	public final String GENERALISIERUNG = "Generalisierung";
	
	/*
	 * Werte aus der Dimension Struktur
	 */
	public final String SIMPLESTRUCUTURALAPPROPRIATNESS = "Simple Strucutural Appropriatness";
	public final String ADVANCEDSTRUCUTURALAPPROPRIATNESS = "Advanced Strucutural Appropriatness";
	public final String ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN = "Anzahl Ueberfluessiger Unsichtbarer Transitionen";
	public final String ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN = "Anzahl Alternativer Doppelter Transitionen";
	public final String STRUKTUR = "Struktur";

	/*
	 * Werte der Dimension Gewichtete Praezision 
	 */
	public final String PRAEZISIONGEWICHTET = "Praezision Gewichtet";
	
	/*
	 * Guete
	 */
	public final String GUETE = "Guete";
	
	// Methoden \\
	
	/**
	 * Fuege Informationen zum Ergebnis hinzu. 
	 * @param key
	 * @param wert
	 */
	public void addInfo(String key, String wert) {
		info.put(key, wert);
	}
	
	/**
	 * Erstellt eine String-Repraesentation der gesamten Map. Geordnet nach Aufruf im restlichen Code.
	 * @return Eine String-Repraesentation der Map.
	 */
	public String toString() {
		String ausgabe = "";
		for (String key : info.keySet()) {
			ausgabe = ausgabe + "<" + key + ", " + info.get(key) + ">" + System.lineSeparator();
		}
		return ausgabe;
	}
	
	/**
	 * Erstellt eine String-Repraesentation der gesamten Map. Ordnung besitzt folgende Reihenfolge: 
	 * <p> 1. Wert der Guete
	 * <p> 2. Werte der Dimensionen
	 * <p> 3. Werte aus den Dimensionen
	 * <p> 4. Werte aus Event-Log und Petri-Netz
	 * <p> 4. Werte aus Berechnung
	 */
	public String toStringGeordnet () {
		String ausgabe = "";
		
		ausgabe = ausgabe + "<" + this.GUETE + ", " + info.get(this.GUETE) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "<" + this.VOLLSTAENDIGKEIT + ", " + info.get(this.VOLLSTAENDIGKEIT) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.PRAEZISION + ", " + info.get(this.PRAEZISION) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.GENERALISIERUNG + ", " + info.get(this.GENERALISIERUNG) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.STRUKTUR + ", " + info.get(this.STRUKTUR) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.PRAEZISIONGEWICHTET + ", " + info.get(this.PRAEZISIONGEWICHTET) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Werte aus der Dimension Vollstaendigkeit */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.PRODUCEDTOKENS + ", " + info.get(this.PRODUCEDTOKENS) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.MISSINGTOKENS + ", " + info.get(this.MISSINGTOKENS) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.CONSUMEDTOKENS + ", " + info.get(this.CONSUMEDTOKENS) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.REMAININGTOKENS + ", " + info.get(this.REMAININGTOKENS) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Werte aus der Dimension Praezision */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.DURCHSCHNITTLICHEABWEICHUNG + ", " + info.get(this.DURCHSCHNITTLICHEABWEICHUNG) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.DURCHSCHNITTLICHEBESTRAFUNG + ", " + info.get(this.DURCHSCHNITTLICHEBESTRAFUNG) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Werte aus der Dimension Struktur */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.SIMPLESTRUCUTURALAPPROPRIATNESS + ", " + info.get(this.SIMPLESTRUCUTURALAPPROPRIATNESS) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ADVANCEDSTRUCUTURALAPPROPRIATNESS + ", " + info.get(this.ADVANCEDSTRUCUTURALAPPROPRIATNESS) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN + ", " + info.get(this.ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN + ", " + 
						 info.get(this.ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Gewichtungen */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.GEWICHTUNGGEWICHTETEPRAEZISION + ", " + info.get(this.GEWICHTUNGGEWICHTETEPRAEZISION) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.GEWICHTUNGGENERALSISIERUNG + ", " + info.get(this.GEWICHTUNGGENERALSISIERUNG) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.GEWICHTUNGSTRUKTUR + ", " + info.get(this.GEWICHTUNGSTRUKTUR) + ">" + System.lineSeparator() + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.GEWICHTUNGSIMPLEADVANCED + ", " + info.get(this.GEWICHTUNGSIMPLEADVANCED) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Werte des Event Logs */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.VERWENDETECLASSIFIER + ", " + info.get(this.VERWENDETECLASSIFIER) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLSEQUENZEN + ", " + info.get(this.ANZAHLSEQUENZEN) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Werte des Petri-Netzes */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLTRANSITIONEN + ", " + info.get(this.ANZAHLTRANSITIONEN) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLEINZIGARTIGETRANSITIONEN + ", " + info.get(this.ANZAHLEINZIGARTIGETRANSITIONEN) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLUNSICHTBARERTRANSITIONEN + ", " + info.get(this.ANZAHLUNSICHTBARERTRANSITIONEN) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ANZAHLSTELLEN + ", " + info.get(this.ANZAHLSTELLEN) + ">" + System.lineSeparator() + System.lineSeparator();
		
		ausgabe = ausgabe + "/* Berechnung */" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.ZEIT + ", " + info.get(this.ZEIT) + ">" + System.lineSeparator();
		ausgabe = ausgabe + "<" + this.BEMERKUNGEN + ", " + info.get(this.BEMERKUNGEN) + ">" + System.lineSeparator();
		
		
		return ausgabe;
	}
	
	public void ordneMap () {

		// Map ordnen
		infoMap2.put(  this.GUETE , info.get(this.GUETE));
		
		infoMap2.put(  this.VOLLSTAENDIGKEIT , info.get(this.VOLLSTAENDIGKEIT));
		infoMap2.put(  this.PRAEZISION , info.get(this.PRAEZISION));
		infoMap2.put(  this.GENERALISIERUNG , info.get(this.GENERALISIERUNG));
		infoMap2.put(  this.STRUKTUR , info.get(this.STRUKTUR));
		infoMap2.put(  this.PRAEZISIONGEWICHTET , info.get(this.PRAEZISIONGEWICHTET));
		
		infoMap2.put(  this.PRODUCEDTOKENS , info.get(this.PRODUCEDTOKENS));
		infoMap2.put(  this.MISSINGTOKENS , info.get(this.MISSINGTOKENS));
		infoMap2.put(  this.CONSUMEDTOKENS , info.get(this.CONSUMEDTOKENS));
		infoMap2.put(  this.REMAININGTOKENS , info.get(this.REMAININGTOKENS));
		
		infoMap2.put(  this.DURCHSCHNITTLICHEABWEICHUNG , info.get(this.DURCHSCHNITTLICHEABWEICHUNG));
		infoMap2.put(  this.DURCHSCHNITTLICHEBESTRAFUNG , info.get(this.DURCHSCHNITTLICHEBESTRAFUNG));
		
		infoMap2.put(  this.SIMPLESTRUCUTURALAPPROPRIATNESS , info.get(this.SIMPLESTRUCUTURALAPPROPRIATNESS));
		infoMap2.put(  this.ADVANCEDSTRUCUTURALAPPROPRIATNESS , info.get(this.ADVANCEDSTRUCUTURALAPPROPRIATNESS));
		infoMap2.put(  this.ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN , info.get(this.ANZAHLUEBERFLUESSIGERUNSICHTBARERTRANSITIONEN));
		infoMap2.put(  this.ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN , info.get(this.ANZAHLALTERNATIVERDOPPELTERTRANSITIONEN));

		infoMap2.put(  this.GEWICHTUNGGEWICHTETEPRAEZISION , info.get(this.GEWICHTUNGGEWICHTETEPRAEZISION));
		infoMap2.put(  this.GEWICHTUNGGENERALSISIERUNG , info.get(this.GEWICHTUNGGENERALSISIERUNG));
		infoMap2.put(  this.GEWICHTUNGSTRUKTUR , info.get(this.GEWICHTUNGSTRUKTUR));
		infoMap2.put(  this.GEWICHTUNGSIMPLEADVANCED , info.get(this.GEWICHTUNGSIMPLEADVANCED));

		infoMap2.put(  this.VERWENDETECLASSIFIER , info.get(this.VERWENDETECLASSIFIER));
		infoMap2.put(  this.ANZAHLSEQUENZEN , info.get(this.ANZAHLSEQUENZEN));
		

		infoMap2.put(  this.ANZAHLTRANSITIONEN , info.get(this.ANZAHLTRANSITIONEN));
		infoMap2.put(  this.ANZAHLEINZIGARTIGETRANSITIONEN , info.get(this.ANZAHLEINZIGARTIGETRANSITIONEN));
		infoMap2.put(  this.ANZAHLUNSICHTBARERTRANSITIONEN , info.get(this.ANZAHLUNSICHTBARERTRANSITIONEN));
		infoMap2.put(  this.ANZAHLSTELLEN , info.get(this.ANZAHLSTELLEN));

		infoMap2.put(  this.ZEIT , info.get(this.ZEIT));
		infoMap2.put(  this.BEMERKUNGEN , info.get(this.BEMERKUNGEN));
		
	}
	
	// Getter und Setter \\
	
	/**
	 * Gebe die gesamte Map zurueck.
	 * @return Ergebnis-Map
	 */
	public Map<String, Object> getInfo() {
		return info;
	}
	
	/**
	 * 
	 * @return Geordnete Ergebnis-Map
	 */
	public Map<String, Object> getInfoGeordnet() {
		return infoMap2;
	}
	
}
