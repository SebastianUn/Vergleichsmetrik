package org.processmining.plugins.vergleichsmetrik.einstellungen;

import org.processmining.plugins.guete.Generalisierung;
import org.processmining.plugins.guete.PraezisionGewichtet;
import org.processmining.plugins.guete.Struktur;

/**
 * 
 * @author Sebastian Reiners
 *
 */
public class VergleichsmetrikKonstanten {

	/**
	 * Default Gewichtungsfaktor für die {@link PraezisionGewichtet}. Wird benutzt in der Guete-Formel.
	 * Hat den Wert 0.5 
	 */
	public static final double GEWICHTUNGGEWICHTETEPRAEZISION = 0.5;
	
	/**
	 * Default Gewichtungsfaktor für die {@link Generalisierung}. Wird benutzt um die Praezision zu gewichten.
	 * Hat den Wert 0.2
	 */
	public static final double GEWICHTUNGGENERALISIERUNG = 0.2;
	
	
	/**
	 * Default Gewichtungsfaktor für die {@link Struktur}. Wird benutzt in der Guete-Formel. 
	 * Hat den Wert 0.2  
	 */
	public static final double GEWICHTUNGSTRUKTUR = 0.2;
	
	/**
	 * Default-Gewichtungsfaktor für die {@link Struktur}. Wird benutzt um die simple und die advanced structural appropriateness zu gewichten.
	 */
	public static final double GEWICHTUNGSIMPLEADVANCED = 0.1;
	
}
