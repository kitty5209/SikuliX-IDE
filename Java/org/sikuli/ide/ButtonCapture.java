/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.ide;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import org.sikuli.ide.util.Utils;
import org.sikuli.script.Debug;
import org.sikuli.script.EventObserver;
import org.sikuli.script.EventSubject;
import org.sikuli.script.OverlayCapturePrompt;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Settings;

class ButtonCapture extends ButtonOnToolbar implements ActionListener, Cloneable, EventObserver {

	protected Element _line;
	protected EditorPane _codePane;
	protected boolean _isCapturing;
  private EditorPatternLabel _lbl = null;

	public ButtonCapture() {
		super();
		URL imageURL = SikuliIDE.class.getResource("/icons/camera-icon.png");
		setIcon(new ImageIcon(imageURL));
		PreferencesUser pref = PreferencesUser.getInstance();
		String strHotkey = Utils.convertKeyToText(
						pref.getCaptureHotkey(), pref.getCaptureHotkeyModifiers());
		setToolTipText(SikuliIDE._I("btnCaptureHint", strHotkey));
		setText(SikuliIDE._I("btnCaptureLabel"));
		//setBorderPainted(false);
		//setMaximumSize(new Dimension(26,26));
		addActionListener(this);
		_line = null;
	}

	public ButtonCapture(EditorPane codePane, Element elmLine) {
		this();
		_line = elmLine;
		_codePane = codePane;
		setUI(UIManager.getUI(this));
		setBorderPainted(true);
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setText(null);
		URL imageURL = SikuliIDE.class.getResource("/icons/capture-small.png");
		setIcon(new ImageIcon(imageURL));
	}

  public ButtonCapture(EditorPatternLabel lbl) {
    // for internal use with the image label __CLICK-TO-CAPTURE__
    super();
    _line = null;
    _codePane = null;
    _lbl = lbl;
  }

	@Override
	public void actionPerformed(ActionEvent e) {
		Debug.log(2, "capture!");
		captureWithAutoDelay();
	}

	public void captureWithAutoDelay() {
		if (_isCapturing) {
			return;
		}
		PreferencesUser pref = PreferencesUser.getInstance();
		int delay = (int) (pref.getCaptureDelay() * 1000.0) + 1;
		capture(delay);
	}

	public void capture(final int delay) {
		if (_isCapturing) {
			return;
		}
		_isCapturing = true;
		Thread t = new Thread("capture") {
			@Override
			public void run() {
				SikuliIDE ide = SikuliIDE.getInstance();
				if (delay != 0) {
					ide.setVisible(false);
				}
				try {
					Thread.sleep(delay);
				} catch (Exception e) {
				}
				OverlayCapturePrompt p = new OverlayCapturePrompt(null, ButtonCapture.this);
				p.prompt("Select an image");
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
				if (delay != 0) {
					ide.setVisible(true);
				}
				ide.requestFocus();
			}
		};
		t.start();
	}

	//<editor-fold defaultstate="collapsed" desc="RaiMan not used">
	/*public boolean hasNext() {
	 * return false;
	 * }*/
	/*public CaptureButton getNextDiffButton() {
	 * return null;
	 * }*/
	/*public void setParentPane(SikuliPane parent) {
	 * _codePane = parent;
	 * }*/
	/*public void setDiffMode(boolean flag) {
	 * }*/
	/*public void setSrcElement(Element elmLine) {
	 * _line = elmLine;
	 * }*/
	//</editor-fold>

	@Override
	public void update(EventSubject s) {
		if (s instanceof OverlayCapturePrompt) {
			OverlayCapturePrompt cp = (OverlayCapturePrompt) s;
			ScreenImage simg = cp.getSelection();
			String filename = null;
			EditorPane pane = SikuliIDE.getInstance().getCurrentCodePane();

			if (simg != null) {
				int naming = PreferencesUser.getInstance().getAutoNamingMethod();
				if (naming == PreferencesUser.AUTO_NAMING_TIMESTAMP) {
					filename = Settings.getTimestamp();
				} else if (naming == PreferencesUser.AUTO_NAMING_OCR) {
          filename = PatternPaneNaming.getFilenameFromImage(simg.getImage());
					if (filename == null || filename.length() == 0) {
						filename = Settings.getTimestamp();
					}
				} else {
					filename = getFilenameFromUser(PatternPaneNaming.getFilenameFromImage(simg.getImage()));
				}

				if (filename != null) {
					String fullpath =
									Utils.saveImage(simg.getImage(), filename, pane.getSrcBundle());
					if (fullpath != null) {
						//String fullpath = pane.getFileInBundle(filename).getAbsolutePath();
						captureCompleted(Utils.slashify(fullpath, false), cp);
						return;
					}
				}
			}
			captureCompleted(null, cp);
		}
	}

	private String getFilenameFromUser(String hint) {
		return (String) JOptionPane.showInputDialog(
						_codePane,
						SikuliIDEI18N._I("msgEnterScreenshotFilename"),
						SikuliIDEI18N._I("dlgEnterScreenshotFilename"),
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						hint);
	}

	public void captureCompleted(String imgFullPath, OverlayCapturePrompt prompt) {
		prompt.close();

		Element src = getSrcElement();
		if (imgFullPath != null) {
			Debug.log(2, "captureCompleted: " + imgFullPath);
			if (src == null) {
				if (_codePane == null) {
          if (_lbl == null) {
            insertAtCursor(SikuliIDE.getInstance().getCurrentCodePane(), imgFullPath);
          } else {
            _lbl.setFile(imgFullPath);
          }
				} else {
					insertAtCursor(_codePane, imgFullPath);
				}
			} else {
        replaceButton(src, imgFullPath);
			}
		} else {
      Debug.log(2, "ButtonCapture: Capture cancelled");
      if (src != null) {
        replaceButton(src, "");
      }
    }
		_isCapturing = false;
	}

	private Element getSrcElement() {
		return _line;
	}

  private boolean replaceButton(Element src, String imgFullPath) {
        if ("".equals(imgFullPath) && _codePane.showThumbs ) {
          return true;
        }
				int start = src.getStartOffset();
				int end = src.getEndOffset();
				int old_sel_start = _codePane.getSelectionStart(),
								old_sel_end = _codePane.getSelectionEnd();
				try {
					StyledDocument doc = (StyledDocument) src.getDocument();
					String text = doc.getText(start, end - start);
					Debug.log(3, text);
					for (int i = start; i < end; i++) {
						Element elm = doc.getCharacterElement(i);
						if (elm.getName().equals(StyleConstants.ComponentElementName)) {
							AttributeSet attr = elm.getAttributes();
							Component com = StyleConstants.getComponent(attr);
							if (com instanceof ButtonCapture
                      || (com instanceof EditorPatternLabel)
                         && ((EditorPatternLabel) com).isCaptureButton()) {
								Debug.log(5, "button is at " + i);
								int oldCaretPos = _codePane.getCaretPosition();
								_codePane.select(i, i + 1);
                if (!_codePane.showThumbs) {
                  _codePane.insertString((new EditorPatternLabel(_codePane, imgFullPath)).toString());
                } else {
                  if (PreferencesUser.getInstance().getPrefMoreImageThumbs()) {
                    com = new EditorPatternButton(_codePane, imgFullPath);
                  } else {
                    com = new EditorPatternLabel(_codePane, imgFullPath, ((EditorPatternLabel) com).toString());
                  }
                 _codePane.insertComponent(com);
                }
								_codePane.setCaretPosition(oldCaretPos);
								break;
							}
						}
					}
				} catch (BadLocationException ble) {
					ble.printStackTrace();
				}
				_codePane.select(old_sel_start, old_sel_end);
				_codePane.requestFocus();
    return true;
  }

	protected void insertAtCursor(EditorPane pane, String imgFilename) {
    String img = "\"" + (new File(imgFilename)).getName() + "\"";
    if (! pane.showThumbs) {
      pane.insertString(img);
    } else {
      if (PreferencesUser.getInstance().getPrefMoreImageThumbs()) {
        pane.insertComponent(new EditorPatternButton(pane, imgFilename));
      } else {
        pane.insertComponent(new EditorPatternLabel(pane, imgFilename));
      }
    }
//TODO set Caret
    pane.requestFocus();
	}

	@Override
	public String toString() {
		return "\"__CLICK-TO-CAPTURE__\"";
	}
}
