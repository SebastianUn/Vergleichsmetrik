package org.processmining.plugins.vergleichsmetrik.einstellungen;

/**
 * Speichert Hilfstexte, die beim Start des Plugins angezeigt werden koennen.
 * @author Sebastian Reiners
 *
 */
public class VergleichsmetrikHilftext {
	
	public static final String GEWICHTUNGGEWICHTETEPRAEZISION = "<html>Diese Gewichtung wird eingesetzt um beim Endgueltigen berechnen der Guete die gewichtete Praezision "  
			+ " zu gewichten. <li> Der Default-Wert ist " + VergleichsmetrikKonstanten.GEWICHTUNGGEWICHTETEPRAEZISION + ". Ist der neue Wert groeﬂer, so steigt auch die Wichtigkeit "
			+ "der Gewichteten Praezision. <li> Ein sehr negativer Wert hat somit auch einen sehr starken Einfluss auf die Guete. Umgekehrt fuehrt ein kleinerer Wert als " 
			+ VergleichsmetrikKonstanten.GEWICHTUNGGEWICHTETEPRAEZISION + " dazu, <li> dass die Wichtigkeit sinkt. Bei einem Wert von 0 hat der Wert keinen Einfluss mehr auf "
			+ "die Guete. Es sind nur Werte > 0 erlaubt. Werte < 0 werden automatisch zu 0.</html>"; 
	
	public static final String GEWICHTUNGSTRUKTUR = "<html>Diese Gewichtung wird eingesetzt um beim Endgueltigen berechnen der Guete die Struktur "
			+ " zu gewichten. <li>Der Default-Wert ist " + VergleichsmetrikKonstanten.GEWICHTUNGSTRUKTUR + ". Ist der neue Wert groeﬂer, so steigt auch die Wichtigkeit "
			+ "der Struktur. <li>Ein sehr negativer Wert hat somit auch einen sehr starken Einfluss auf die Guete. Umgekehrt fuehrt ein kleinerer Wert als " 
			+ VergleichsmetrikKonstanten.GEWICHTUNGSTRUKTUR + " dazu, <li>dass die Wichtigkeit sinkt. Bei einem Wert von 0 hat der Wert keinen Einfluss mehr auf "
			+ "die Guete. Es sind nur Werte > 0 erlaubt. Werte < 0 werden automatisch zu 0.</html>";  
	
	public static final String GEWICHTUNGGENERALISIERUNG = "<html>Diese Gewichtung wird eingesetzt um beim berechnen der gewichteten Praezision die Generalisierung " 
			+ " zu gewichten. <li>Der Default-Wert ist " + VergleichsmetrikKonstanten.GEWICHTUNGGENERALISIERUNG + ". Ist der neue Wert groeﬂer, so steigt auch die Wichtigkeit "
			+ "der Generalisierung. <li>Ein sehr negativer Wert hat somit auch einen sehr starken Einfluss auf die gewichtete Praezision. Umgekehrt fuehrt ein kleinerer Wert als " 
			+ VergleichsmetrikKonstanten.GEWICHTUNGGENERALISIERUNG + " dazu, <li>dass die Wichtigkeit sinkt. Bei einem Wert von 0 hat der Wert keinen Einfluss mehr auf "
			+ "die gewichete Praezision. Es sind nur Werte > 0 erlaubt. Werte < 0 werden automatisch zu 0.</html>"; 

	public static final String GEWICHTUNGSIMPLEADVANCED = "<html>Diese Gewichtung wird eingesetzt um beim berechnen der Struktur die simple zur advanced structural appropriateness " 
			+ " zu gewichten. <li>Der Default-Wert ist " + VergleichsmetrikKonstanten.GEWICHTUNGSIMPLEADVANCED + ". Ist der neue Wert groeﬂer, so steigt auch die Wichtigkeit "
			+ "der simple structural appropriatness. <li>Ein sehr negativer Wert hat somit auch einen sehr starken Einfluss auf die Struktur. Umgekehrt fuehrt"
			+ " ein kleinerer Wert als " + VergleichsmetrikKonstanten.GEWICHTUNGSIMPLEADVANCED + " dazu, <li>dass die Wichtigkeit sinkt. Bei einem Wert von 0 hat der Wert "
			+ "keinen Einfluss mehr auf " + "die gewichete Praezision. Es sind nur Werte > 0 erlaubt. Werte < 0 werden automatisch zu 0. </html>";

}
