package org.processmining.plugins.guete.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikEinstellungen;
import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikHilftext;
import org.processmining.plugins.vergleichsmetrik.einstellungen.VergleichsmetrikKonstanten;

import com.fluxicon.slickerbox.factory.SlickerFactory;

@SuppressWarnings("serial")
/**
 * Erstellt ein Panel fuer ein Interface vor dem Laden des Haupt-Plugins. Einstellen von verschiedenen Werten und Gewichtungen ist so moeglich. 
 * @author Sebastian Reiners
 *
 */
public class VergleichsmetrikUIInputParameter extends JPanel {

	private final JPanel mainPanel = SlickerFactory.instance().createRoundedPanel();
	private final JPanel otherOptionsPanel = SlickerFactory.instance().createRoundedPanel();
	
	private JLabel gewichtungPraezisionLabel;
	private final JTextField gewichtungPraezision = new JTextField();
	
	private JLabel gewichtungStrukturLabel;
	private final JTextField gewichtungStruktur = new JTextField();
	
	private JLabel gewichtungGeneralisierungLabel;
	private final JTextField gewichtungGeneralisierung = new JTextField();
	
	private JLabel gewichtungSimpleAdvancedLabel;
	private final JTextField gewichtungSimpleAdvanced = new JTextField();
	
	// TODO z.B. Nur Werte zwischen 0.0 und 0.5 ausgewählen lassen? 
	
	// Konstruktoren \\
	
	/**
	 * Erstellt ein Panel. Ruft die {@link #jbInit()} Methode zur initalisierung auf. 
	 */
	public VergleichsmetrikUIInputParameter () {
		
		try {
			jbInit();
		} catch (Exception ex) {
			System.out.println("UI Fehler");
		}
	}
	
	// Methoden \\
	
	/**
	 * Initalisiert alle benoetigten Label und Text-Felder. Initalisiert das {@link #mainPanel}. 
	 * Wenn keine Eingaben erfolgen, werden Werte aus der Klasse {@link VergleichsmetrikKonstanten} genommen.
	 * @throws Exception
	 */
	private void jbInit() throws Exception {
		int x = 0;
		int y = 0;
		
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		gewichtungPraezisionLabel = SlickerFactory.instance().createLabel("Gewichtete Praezision");
		gewichtungPraezision.setPreferredSize(new Dimension(50, 21));
		gewichtungPraezision.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGGEWICHTETEPRAEZISION));
		
		gewichtungStrukturLabel = SlickerFactory.instance().createLabel("Gewichtete Struktur");
		gewichtungStruktur.setPreferredSize(new Dimension(50, 21));
		gewichtungStruktur.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGSTRUKTUR));
		
		gewichtungGeneralisierungLabel = SlickerFactory.instance().createLabel("Gewichtete Generalisierung");
		gewichtungGeneralisierung.setPreferredSize(new Dimension(50, 21));
		gewichtungGeneralisierung.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGGENERALISIERUNG));
		
		gewichtungSimpleAdvancedLabel = SlickerFactory.instance().createLabel("Gewichtung Simple zu Advanced");
		gewichtungSimpleAdvanced.setPreferredSize(new Dimension(50, 21));
		gewichtungSimpleAdvanced.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGSIMPLEADVANCED));
		
		otherOptionsPanel.setLayout(new GridBagLayout());
		
		y = 0;
		// Gewichtung Praezision
		otherOptionsPanel.add(gewichtungPraezisionLabel, new GridBagConstraints(0, x + (++y), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
				new Insets (0, 5, 0, 0), 0, 0));
		otherOptionsPanel.add(new HelpIcon(VergleichsmetrikHilftext.GEWICHTUNGGEWICHTETEPRAEZISION), new GridBagConstraints(1, x + y, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
		otherOptionsPanel.add(gewichtungPraezision, new GridBagConstraints(2, x + y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		
		// Gewichtung Struktur 
		otherOptionsPanel.add(gewichtungStrukturLabel, new GridBagConstraints(0, x + (++y), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
				new Insets (0, 5, 0, 0), 0, 0));
		otherOptionsPanel.add(new HelpIcon(VergleichsmetrikHilftext.GEWICHTUNGSTRUKTUR), new GridBagConstraints(1, x + y, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
		otherOptionsPanel.add(gewichtungStruktur, new GridBagConstraints(2, x + y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		
		// Gewichtung Generalisierung 
		otherOptionsPanel.add(gewichtungGeneralisierungLabel, new GridBagConstraints(0, x + (++y), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
				new Insets (0, 5, 0, 0), 0, 0));
		otherOptionsPanel.add(new HelpIcon(VergleichsmetrikHilftext.GEWICHTUNGGENERALISIERUNG), new GridBagConstraints(1, x + y, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
		otherOptionsPanel.add(gewichtungGeneralisierung, new GridBagConstraints(2, x + y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		
		// Gewichtung Simple zu Advanced
		otherOptionsPanel.add(gewichtungSimpleAdvancedLabel, new GridBagConstraints(0, x + (++y), 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
				new Insets (0, 5, 0, 0), 0, 0));
		otherOptionsPanel.add(new HelpIcon(VergleichsmetrikHilftext.GEWICHTUNGSIMPLEADVANCED), new GridBagConstraints(1, x + y, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 1, 0, 0), 0, 0));
		otherOptionsPanel.add(gewichtungSimpleAdvanced, new GridBagConstraints(2, x + y, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
		
		// Main panel erstellen
		mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		mainPanel.add(otherOptionsPanel, null);
		
		setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.NORTH);
	}
	
	// Getter und Setter \\
	
	private double getGewichtungPraezision () {
		try {
			Double.parseDouble(gewichtungPraezision.getText());
		} catch (Exception e) {
			gewichtungPraezision.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGGEWICHTETEPRAEZISION));
		}
		
		return Double.parseDouble(gewichtungPraezision.getText());
	}
	
	private double getGewichungStruktur () {
		try {
			Double.parseDouble(gewichtungStruktur.getText());
		} catch (Exception e) {
			gewichtungStruktur.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGSTRUKTUR));
		}
		
		return Double.parseDouble(gewichtungStruktur.getText());
	}
	
	private double getGewichtungGeneralisierung () {
		try {
			Double.parseDouble(gewichtungGeneralisierung.getText());
		} catch (Exception e) {
			gewichtungGeneralisierung.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGGENERALISIERUNG));
		}
		
		return Double.parseDouble(gewichtungGeneralisierung.getText());
	}
	
	private double getGewichtungSimpleAdvanced () {
		try {
			Double.parseDouble(gewichtungSimpleAdvanced.getText());
		} catch (Exception e) {
			gewichtungSimpleAdvanced.setText(Double.toString(VergleichsmetrikKonstanten.GEWICHTUNGSIMPLEADVANCED));
		}
		
		return Double.parseDouble(gewichtungSimpleAdvanced.getText());
	}
	
	/**
	 * Erstellt die Einstellungen der Vergleichsmetrik indem abgefragt wird welche Werte ueber das Interface eingegeben wurden. 
	 * Sind ungueltige oder keine Werte eingegeben worden werden Standard-Parameter genommen.
	 * @return Einstellungen der Vergleichsmetrik
	 */
	public VergleichsmetrikEinstellungen getEinstellungen () {
		return new VergleichsmetrikEinstellungen(getGewichtungPraezision(), getGewichtungGeneralisierung(), getGewichungStruktur(), getGewichtungSimpleAdvanced());
	}
}
