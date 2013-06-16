/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sikuli.ide.z_notused.sikuli_test;

import java.io.File;
import javax.swing.text.BadLocationException;
import org.sikuli.ide.SikuliIDE;
import org.sikuli.ide.EditorPane;
import org.sikuli.script.Debug;

/**
 *
 * @author rhocke
 */
public class RunTest {

		//FIXME: supports args
	public static void runUnitTest(String filename, String[] args) {
		TextUnitTestRunner tester = new TextUnitTestRunner(args);
		File file = new File(filename);
		filename = file.getAbsolutePath();
		if (filename.endsWith(".sikuli")) {
			try {
				boolean result = tester.testSikuli(filename);
				if (!result) {
					System.exit(1);
				}
				System.exit(0);
			} catch (Exception e) {
				Debug.error(e.getMessage());
				System.exit(2);
			}
		}
		System.exit(-1);
	}
}
