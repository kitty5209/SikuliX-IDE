/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide.util;

import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.*;
import org.sikuli.script.Debug;
import org.sikuli.script.FileManager;
import org.sikuli.script.Settings;

public class Utils {
//TODO consolidate with FileManager and Settings
  
	public static int stopRecorder() {
		try {
			String cmd[] = {"sh", "-c", "ps aux | grep MacRecorder | awk '{print $2}' | xargs kill"};
			Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			return p.exitValue();
		} catch (Exception e) {
			return -1;
		}
	}

	public static int runRecorder() {
		try {
			String cmd[] = {"MacRecorder.app/Contents/MacOS/MacRecorder", "no-play"};
			Process p = Runtime.getRuntime().exec(cmd);
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
			p.waitFor();
			return p.exitValue();
		} catch (Exception e) {
			return -1;
		}
	}

	public static void zip(String path, String outZip)
					throws IOException, FileNotFoundException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outZip));
		zipDir(path, zos);
		zos.close();
	}

	public static void unzip(String zip, String path)
					throws IOException, FileNotFoundException {
		final int BUF_SIZE = 2048;
		FileInputStream fis = new FileInputStream(zip);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte data[] = new byte[BUF_SIZE];
			FileOutputStream fos = new FileOutputStream(
							new File(path, entry.getName()));
			BufferedOutputStream dest = new BufferedOutputStream(fos, BUF_SIZE);
			while ((count = zis.read(data, 0, BUF_SIZE)) != -1) {
				dest.write(data, 0, count);
			}
			dest.close();
		}
		zis.close();
	}

	public static void zipDir(String dir, ZipOutputStream zos) throws IOException {
		File zipDir = new File(dir);
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[1024];
		int bytesIn;
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			/*
			 if(f.isDirectory()) {
			 String filePath = f.getPath();
			 zipDir(filePath, zos);
			 continue;
			 }
			 */
			if (f.isFile()) {
				FileInputStream fis = new FileInputStream(f);
				ZipEntry anEntry = new ZipEntry(f.getName());
				zos.putNextEntry(anEntry);
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				fis.close();
			}
		}
	}

	public static String slashify(String path, boolean isDirectory) {
		return FileManager.slashify(path, isDirectory);
	}

	public static boolean rename(String oldFile, String newFile) {
		File old = new File(oldFile);
		return old.renameTo(new File(newFile));
	}

	public static String saveImage(BufferedImage img, String filename, String bundlePath) {
		final int MAX_ALT_NUM = 3;
		String fullpath = bundlePath;
		File path = new File(fullpath);
		if (!path.exists()) {
			path.mkdir();
		}
		if (!filename.endsWith(".png")) {
			filename += ".png";
		}
		File f = new File(path, filename);
		int count = 0;
		String msg = f.getName() + " exists - using ";
		while (count < MAX_ALT_NUM) {
			if (f.exists()) {
				f = new File(path, FileManager.getAltFilename(f.getName()));
			} else {
				if (count > 0) {
					Debug.log(msg + f.getName() + " (Utils.saveImage)");
				}
				break;
			}
			count++;
		}
		if (count >= MAX_ALT_NUM) {
			f = new File(path, Settings.getTimestamp() + ".png");
			Debug.log(msg + f.getName() + " (Utils.saveImage)");
		}
		fullpath = f.getAbsolutePath();
		fullpath = fullpath.replaceAll("\\\\", "/");
		try {
			ImageIO.write(img, "png", new File(fullpath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return fullpath;
	}

	public static String convertKeyToText(int code, int mod) {
		String txtMod = KeyEvent.getKeyModifiersText(mod);
		String txtCode = KeyEvent.getKeyText(code);
		String ret;
		if (txtCode.equals("Ctrl") || txtCode.equals("Alt")
						|| txtCode.equals("Windows") || txtCode.equals("Shift")
						|| txtCode.equals("\u2303") || txtCode.equals("\u2325")
						|| txtCode.equals("\u2318") || txtCode.equals("\u21E7")) {
			ret = txtMod;
		} else {
			ret = txtMod + " " + txtCode;
		}
		return ret;
	}
}
