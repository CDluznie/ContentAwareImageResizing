package cair.gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cair.graph.SeamCarving;
import cair.image.Image;

public class Frame extends JFrame {

	/**
	 * Smallest possible image width
	 **/
	public static int MIN_WIDTH = 60;

	/**
	 * Smallest possible image height
	 **/
	public static int MIN_HEIGHT = 60;
	
	private static final long serialVersionUID = 5054998776518514039L;

	private final Container contentPane;
	private final FileChooser fc;
	private final JButton chooseFile;
	private final Label input;
	private final TextField output;
	private final Label pixels;
	private final JButton add;
	private final JButton sub;
	private final Slider slider;
	private final JButton validate;
	private final ProgressBar progress;
	
	private String inputName;
	
	public Frame() {
		contentPane = getContentPane();
		progress = createProgressBar();
		chooseFile = createChooseFileButton();
		fc = createFileChoose();
		input = createInputFileNameLabel();
		output = createOutputFileNameTextField();
		pixels = createPixelsNumberLabel();
		add = createAddButton();
		sub = createSubButton();
		slider = createSlider();
		validate = createValidateButton();
		createContentPane();
		setTitle("Content aware image width resizing");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(320, 218));
		setVisible(true);
	}

	private void createContentPane() {
		contentPane.setLayout(null);
		input.setBounds(12, 12, 147, 30);
		chooseFile.setBounds(168, 12, 134, 30);
		output.setBounds(12, 52, 140, 30);
		validate.setBounds(162, 52, 140, 30);
		pixels.setBounds(120, 94, 71, 30);
		sub.setBounds(64, 94, 44, 30);
		add.setBounds(202, 94, 44, 30);
		slider.setBounds(12, 132, 290, 50);
		progress.setBounds(12, 190, 290, 26);
		contentPane.add(input);
		contentPane.add(chooseFile);
		contentPane.add(output);
		contentPane.add(validate);
		contentPane.add(pixels);
		contentPane.add(add);
		contentPane.add(sub);
		contentPane.add(slider);
		validate.setEnabled(false);
		validate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String name = output.getText();
				char c;
				if (name.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Invalid output file name (empty)", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				for (int i = 0; i < name.length(); i++) {
					c = name.charAt(i);
					if (!Character.isLetterOrDigit(c) && c != '_') {
						JOptionPane.showMessageDialog(null, "Invalid output file name (special character" + c + ")", "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				Image image;
				try {
					image = Image.read(Paths.get(inputName));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Invalid input image (can not find '" + inputName + ")", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (image.getWidth() < Frame.MIN_WIDTH || image.getHeight() < Frame.MIN_HEIGHT) {
					JOptionPane.showMessageDialog(null, "Invalid input image (size should be greater than " + Frame.MIN_HEIGHT + "x" + Frame.MIN_WIDTH + ")", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (slider.getValue() == 0) {
					return;
				}
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						int pixels = slider.getValue();
						setBusyState();
						progress.setValue(0);
						progress.setMaximum(pixels);
						Image resultImage = SeamCarving.contentAwareResizing(
								image,
								pixels,
								__ -> progress.increment()
						);
						try {
							resultImage.write(output.getText());
						} catch (IOException e) {
							e.printStackTrace();
						}
						setUsableState();
					}
				});
				thread.start();
			}
		});
		pixels.setText(String.valueOf(slider.getValue()));
	}
	
	private JButton createChooseFileButton() {
		JButton chooseFile = new JButton();
		chooseFile.setText("Choose file");
		chooseFile.setFocusPainted(false);
		chooseFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (fc.showOpenDialog(chooseFile) == JFileChooser.APPROVE_OPTION) {
					String toPrint;
					int textWidth, fieldWidth;
					inputName = fc.getSelectedFile().getAbsolutePath();
					toPrint = inputName;
					textWidth = input.getFontMetrics(input.getFont()).stringWidth(toPrint);
					fieldWidth = (int) input.getSize().getWidth() - input.getInsets().left - input.getInsets().right;
					if (textWidth >= fieldWidth) {
						while (textWidth >= fieldWidth) {
							toPrint = toPrint.substring(1);
							textWidth = input.getFontMetrics(input.getFont()).stringWidth(toPrint);
						}
						toPrint = toPrint.substring(3);
						if (toPrint.indexOf(File.separator) != -1) {
							toPrint = toPrint.substring(toPrint.indexOf(File.separator));
						}
						toPrint = "..." + toPrint;
					}
					input.setText(toPrint);
					validate.setEnabled(true);
				}
			}
		});
		return chooseFile;
	}
	
	private void setUsableState() {
		setSize(new Dimension(320, 218));
		contentPane.remove(progress);
		contentPane.repaint();
		output.setEditable(true);
		chooseFile.setEnabled(true);
		add.setEnabled(true);
		sub.setEnabled(true);
		slider.setEnabled(true);
		input.setText("");
		output.setText("");
	}
	
	private void setBusyState() {
		setSize(new Dimension(320, 256));
		contentPane.add(progress);
		contentPane.repaint();
		output.setEditable(false);
		chooseFile.setEnabled(false);
		add.setEnabled(false);
		sub.setEnabled(false);
		slider.setEnabled(false);
		validate.setEnabled(false);
	}
	
	private FileChooser createFileChoose() {
		FileChooser fc = new FileChooser();
		return fc;
	}
	
	private Label createInputFileNameLabel() {
		Label input = new Label();
		return input;
	}
	
	private TextField createOutputFileNameTextField() {
		TextField output = new TextField();
		return output;
	}
	
	private Label createPixelsNumberLabel() {
		Label pixels = new Label();
		return pixels;
	}
	
	private JButton createAddButton() {
		JButton add = new JButton();
		add.setText("+");
		add.setFocusPainted(false);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(slider.getValue() + 1);
			}
		});
		return add;
	}
	
	private JButton createSubButton() {
		JButton sub = new JButton();
		sub.setText("-");
		sub.setFocusPainted(false);
		sub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(slider.getValue() - 1);
			}
		});
		return sub;
	}

	private Slider createSlider() {
		Slider slider = new Slider();
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				pixels.setText(String.valueOf(slider.getValue()));
			}
		});
		return slider;
	}
	
	private JButton createValidateButton() {
		JButton validate = new JButton();
		validate.setText("Validate");
		validate.setFocusPainted(false);
		return validate;
	}
	
	private ProgressBar createProgressBar() {
		ProgressBar progress = new ProgressBar();
		return progress;
	}
	
}
