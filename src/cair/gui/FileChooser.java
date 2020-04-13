package cair.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cair.graph.SeamCarving;

public class FileChooser extends JFileChooser {

	private static final long serialVersionUID = 3965775110383077442L;

	public FileChooser() {
		setMultiSelectionEnabled(false);
		setCurrentDirectory(new File(System.getProperty("user.dir")));
		setFileFilter(
			new FileFilter() {
				@Override
				public String getDescription() {
					return "*." + SeamCarving.EXTENSION;
				}
				@Override
				public boolean accept(File pathname) {
					if(pathname != null) {
						if(pathname.isDirectory()) {
							return true;
						}
						return pathname.getName().endsWith("." + SeamCarving.EXTENSION);
					}
					return false;
				}
			}
		);
		setAcceptAllFileFilterUsed(false);
	}
	
}
