package cair.gui;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class Label extends JLabel {

	private static final long serialVersionUID = -4212307196751744225L;

	public Label() {
		setOpaque(true);
		setBackground(Color.white);
		setBorder(LineBorder.createGrayLineBorder());
		setHorizontalAlignment(JLabel.CENTER);
	}
	
}
