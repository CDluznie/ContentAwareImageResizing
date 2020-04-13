package cair.gui;

import java.awt.Graphics;
import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar {

	private static final long serialVersionUID = -2771357663850289869L;

	public ProgressBar() {
		setMinimum(0);
		setStringPainted(true);
	}
	
	public void increment() {
		setValue(getValue() + 1);
	}
	
	@Override
	public void paintComponent(Graphics g){
		setString((getValue() * 100)/getMaximum() + " %");
		super.paintComponent(g);
	}
	
}
