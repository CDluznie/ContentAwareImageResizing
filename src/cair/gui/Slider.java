package cair.gui;

import javax.swing.JSlider;

public class Slider extends JSlider {

	private static final long serialVersionUID = 6883515406832806896L;

	public Slider() {
		setMinimum(0);
		setMaximum(50);
		setValue(10);
		setPaintTicks(false);
		setPaintLabels(false);
		setMinorTickSpacing(1);
		setMajorTickSpacing(10);
	}
	
}
