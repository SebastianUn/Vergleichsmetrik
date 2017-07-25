package org.processmining.plugins.guete.gui;

/***********************************************************
 * This software is part of the ProM package * http://www.processmining.org/ * *
 * Copyright (c) 2003-2006 TU/e Eindhoven * and is licensed under the * Common
 * Public License, Version 1.0 * by Eindhoven University of Technology *
 * Department of Information Systems * http://is.tm.tue.nl * *
 **********************************************************/

import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Ana Karla A. de Medeiros.
 * @version 1.0
 */
class HelpIcon extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	HelpIcon(String message) {
		super(UIManager.getIcon("OptionPane.questionIcon"));
		//addMouseListener(new HelpPopUp(message));
		setToolTipText(message);
	}

}
