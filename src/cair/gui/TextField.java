package cair.gui;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class TextField extends JTextField {

	private static final long serialVersionUID = -4767104603573604738L;

	public TextField() {
		setOpaque(true);
		setBackground(Color.white);
		setBorder(LineBorder.createGrayLineBorder());
		setHorizontalAlignment(JLabel.CENTER);
	}

}
