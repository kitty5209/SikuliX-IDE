/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.sikuli.script.Settings;


public class SikuliIDEFileChooser {

	static final int FILES = JFileChooser.FILES_ONLY;
	static final int DIRS = JFileChooser.DIRECTORIES_ONLY;
	static final int SAVE = FileDialog.SAVE;
	static final int LOAD = FileDialog.LOAD;
	Frame _parent;

	public SikuliIDEFileChooser(Frame parent) {
		_parent = parent;
	}

	private File showFileChooser(String msg, int mode, GeneralFileFilter[] filters, int selectionMode) {
		if (Settings.isMac()) {
			if (Settings.isJava7() && selectionMode == DIRS) {
				return showJFileChooser(msg, mode, filters, FILES);
			} else {
//TODO Mac Java7: FileDialog not taking bundles as files
				FileDialog fd = new FileDialog(_parent, msg, mode);
				for (GeneralFileFilter filter : filters) {
					fd.setFilenameFilter(filter);
				}
				fd.setVisible(true);
				if (fd.getFile() == null) {
					return null;
				}
				return new File(fd.getDirectory(), fd.getFile());
			}
		}
		return showJFileChooser(msg, mode, filters, selectionMode);
	}

	private File showJFileChooser(String msg, int mode, GeneralFileFilter[] filters, int selectionMode) {
		JFileChooser fchooser = new JFileChooser();
		if (mode == FileDialog.SAVE) {
			fchooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		PreferencesUser pref = PreferencesUser.getInstance();
		String last_dir = pref.get("LAST_OPEN_DIR", "");
		if (!last_dir.equals("")) {
			fchooser.setCurrentDirectory(new File(last_dir));
		}
		fchooser.setAcceptAllFileFilterUsed(false);
		for (GeneralFileFilter filter : filters) {
			fchooser.setFileFilter(filter);
		}
		fchooser.setFileSelectionMode(selectionMode);
		fchooser.setSelectedFile(null);
		if (fchooser.showDialog(_parent, null) != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		File ret = fchooser.getSelectedFile();
		String dir = ret.getParent();
		PreferencesUser.getInstance().put("LAST_OPEN_DIR", dir);
		return ret;
	}

	public File loadImage() {
		return showFileChooser("Open a Image File", LOAD,
						new GeneralFileFilter[]{
							new GeneralFileFilter("png", "PNG Image Files (*.png)"),
							new GeneralFileFilter("jpg", "JPEG Image Files (*.jpg)")
						}, FILES);
	}

	public File load() {
		return showFileChooser("Open a Sikuli Source File", LOAD,
						new GeneralFileFilter[]{
							new GeneralFileFilter("sikuli", "Sikuli source files (*.sikuli)")
						}, DIRS);
	}

	public File save() {
		return showFileChooser("Save a Sikuli Source File", SAVE,
						new GeneralFileFilter[]{
							new GeneralFileFilter("sikuli", "Sikuli source files (*.sikuli)")
						}, DIRS);
	}

	public File export() {
		return showFileChooser("Export a Sikuli Executable File", SAVE,
						new GeneralFileFilter[]{
							new GeneralFileFilter("skl", "Sikuli executable files (*.skl)")
						}, FILES);
	}
}
class GeneralFileFilter extends FileFilter implements FilenameFilter {

	private String _ext, _desc;

	public GeneralFileFilter(String ext, String desc) {
		_ext = ext;
		_desc = desc;
	}

	@Override
	public boolean accept(File dir, String fname) {
		int i = fname.lastIndexOf('.');
		if (i > 0 && i < fname.length() - 1) {
			String ext = fname.substring(i + 1).toLowerCase();
			if (ext.equals(_ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 && i < s.length() - 1) {
			String ext = s.substring(i + 1).toLowerCase();
			if (ext.equals(_ext)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return _desc;
	}
}
