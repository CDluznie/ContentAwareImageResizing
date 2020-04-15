package cair.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cair.image.Image;

public class FileChooser extends JFileChooser {

	private static final long serialVersionUID = 3965775110383077442L;

	public FileChooser() {
		setMultiSelectionEnabled(false);
		setCurrentDirectory(new File(System.getProperty("user.dir")));
		setFileFilter(
			new FileFilter() {
				@Override
				public String getDescription() {
					return "*." + Image.EXTENSION;
				}
				@Override
				public boolean accept(File pathname) {
					if(pathname != null) {
						if(pathname.isDirectory()) {
							return true;
						}
						return pathname.getName().endsWith("." + Image.EXTENSION);
					}
					return false;
				}
			}
		);
		setAcceptAllFileFilterUsed(false);
	}
	
}
