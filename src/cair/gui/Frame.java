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

import cair.graph.Graph;
import cair.graph.SeamCarving;
import cair.image.Image;

public class Frame extends JFrame {

	private static final long serialVersionUID = 5054998776518514039L;

	private final ProgressBar progress;
	private final FileChooser fc;
	
	private String inputName;
	
	public Frame() {
		progress = createProgressBar();
		fc = createFileChoose();
		createContentPane();
		setTitle("Content aware image resizing");
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(new Dimension(320, 218));
		setVisible(true);
	}
	
	private int[][] contentAwareResizing(int[][] image, int numberColumn) {
		int[][] resultImage = image, interest;
		Graph graph;
		resultImage = image;
		for (int i = 0; i < numberColumn; i++) {
			interest = SeamCarving.interest(resultImage);
			graph = SeamCarving.toGraph(interest);
			resultImage = SeamCarving.removePixels(resultImage, SeamCarving.fordFulkerson(graph));
			// TODO LAMBDA UPDATE consumer iteration (observer ?)
			// MOVE FUNCTION IN SEAM CARVING
			progress.increment();
		}
		return resultImage;
	}

	private void createContentPane() {
		Container contentPane = getContentPane();
		contentPane.setLayout(null);
		
		
		Label input = new Label(), pixels = new Label();
		TextField output = new TextField();
		JButton chooseFile = createChooseFileButton();
		JButton validate = createValidateButton();
		JButton add = new JButton(), sub = new JButton();
		Slider slider = new Slider();
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
				int[][] image;
				try {
					image = Image.read(Paths.get(inputName));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Invalid input image (can not find '" + inputName + ")", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (Image.getWidth(image) < Image.MIN_WIDTH || Image.getHeight(image) < Image.MIN_HEIGHT) {
					JOptionPane.showMessageDialog(null, "Invalid input image (size should be greater than " + Image.MIN_HEIGHT + "x" + Image.MIN_WIDTH + ")", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (slider.getValue() == 0) {
					return;
				}
				
				
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						int pixels = slider.getValue();
						setSize(new Dimension(320, 256));
						contentPane.add(progress);
						contentPane.repaint();
						output.setEditable(false);
						chooseFile.setEnabled(false);
						add.setEnabled(false);
						sub.setEnabled(false);
						slider.setEnabled(false);
						validate.setEnabled(false);
						progress.setValue(0);
						progress.setMaximum(pixels);
						int[][] resultImage = contentAwareResizing(image, pixels);
						try {
							Image.write(resultImage, output.getText() + '.' + Image.EXTENSION);
						} catch (IOException e) {
							e.printStackTrace();
						}
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
				});
				thread.start();
			}
		});
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				pixels.setText(String.valueOf(slider.getValue()));
			}
		});
		add.setText("+");
		add.setFocusPainted(false);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(slider.getValue() + 1);
			}
		});
		sub.setText("-");
		sub.setFocusPainted(false);
		sub.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				slider.setValue(slider.getValue() - 1);
			}
		});
		pixels.setText(String.valueOf(slider.getValue()));
	}
	
	private JButton createChooseFileButton() {
		JButton chooseFile = new JButton();
		chooseFile.setText("Choose file");
		chooseFile.setFocusPainted(false);
		return chooseFile;
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
	
	private FileChooser createFileChoose() {
		FileChooser fc = new FileChooser();
		return fc;
	}
	
}
