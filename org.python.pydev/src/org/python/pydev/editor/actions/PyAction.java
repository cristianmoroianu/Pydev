/*
 * @author: fabioz
 * Created: January 2004
 * License: Common Public License v1.0
 */
 
package org.python.pydev.editor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Fabio Zadrozny
 * 
 * Superclass of all our actions. Contains utility functions.
 * 
 * Subclasses should implement run(IAction action) method.
 */
public abstract class PyAction implements IEditorActionDelegate {

	// Always points to the current editor
	protected IEditorPart targetEditor;

	public void setEditor(IEditorPart targetEditor) {
		this.targetEditor = targetEditor;
	}
	
	/**
	 * This is an IEditorActionDelegate override
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		setEditor(targetEditor);
	}

	/**
	 * Activate action  (if we are getting text)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TextSelection) {
			action.setEnabled(true);
			return;
		}
		action.setEnabled( targetEditor instanceof ITextEditor);
	}

	/**
	 * This method returns the delimiter for the document
	 * @param doc
	 * @param startLineIndex
	 * @return
	 * @throws BadLocationException
	 */
	protected String getDelimiter(IDocument doc, int startLineIndex)
		throws BadLocationException {
		String endLineDelim = doc.getLineDelimiter(startLineIndex);
		if (endLineDelim == null) {
			endLineDelim = doc.getLegalLineDelimiters()[0];
		}
		return endLineDelim;
	}

	/**
	 * This function returns the text editor.
	 * @return
	 */
	protected ITextEditor getTextEditor() {
		if (targetEditor instanceof ITextEditor) {
			return (ITextEditor) targetEditor;
		} else {
			throw new RuntimeException("Expecting text editor. Found:"+targetEditor.getClass().getName());
		}
	}

	/**
	 * Helper for setting caret
	 * @param pos
	 * @throws BadLocationException
	 */
	protected void setCaretPosition(int pos) throws BadLocationException {
		getTextEditor().selectAndReveal(pos, 0);
	}

	/**
	 * Are we in the first char of the line with the offset passed?
	 * @param doc
	 * @param cursorOffset
	 */
	protected void isInFirstVisibleChar(IDocument doc, int cursorOffset) {
		try {
			IRegion region = doc.getLineInformationOfOffset(cursorOffset);
			int offset = region.getOffset();
			String src = doc.get(offset, region.getLength());
			if ("".equals(src))
				return;
			int i = 0;
			while (i < src.length()) {
				if (!Character.isWhitespace(src.charAt(i++))) {
					break;
				}
			}
			setCaretPosition(offset + i - 1);
		} catch (BadLocationException e) {
			beep(e);
			return;
		}
	}


	/**
	 * Returns the position of the first char.
	 * @param doc
	 * @param cursorOffset
	 * @return
	 * @throws BadLocationException
	 */
	protected int getFirstCharPosition(IDocument doc, int cursorOffset)
		throws BadLocationException {
		IRegion region;
		region = doc.getLineInformationOfOffset(cursorOffset);
		int offset = region.getOffset();
		String src = doc.get(offset, region.getLength());
		if ("".equals(src))
			return 0;
		int i = 0;
		while (i < src.length()) {
			if (!Character.isWhitespace(src.charAt(i++))) {
				break;
			}
		}
		return (offset + i - 1);
	}

	/**
	 * Goes to first char of the line.
	 * @param doc
	 * @param cursorOffset
	 */
	protected void gotoFirstChar(IDocument doc, int cursorOffset) {
		try {
			IRegion region = doc.getLineInformationOfOffset(cursorOffset);
			int offset = region.getOffset();
			setCaretPosition(offset);
		} catch (BadLocationException e) {
			beep(e);
		}
	}

	/**
	 * Goes to the first visible char.
	 * @param doc
	 * @param cursorOffset
	 */
	protected void gotoFirstVisibleChar(IDocument doc, int cursorOffset) {
		try {
			setCaretPosition(getFirstCharPosition(doc, cursorOffset));
		} catch (BadLocationException e) {
			beep(e);
		}
	}

	/**
	 * Goes to the first visible char.
	 * @param doc
	 * @param cursorOffset
	 */
	protected boolean isAtFirstVisibleChar(IDocument doc, int cursorOffset) {
		try {
			return getFirstCharPosition(doc, cursorOffset) == cursorOffset;
		} catch (BadLocationException e) {
			return false;
		}
	}

	//================================================================
	// HELPER FOR DEBBUGING... 
	//================================================================

	/**
	 * Beep...humm... yeah....beep....ehehheheh
	 */
	protected void beep(Exception e) {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().beep();
		e.printStackTrace();
	}

	protected void print(Object o) {
		System.out.println(o);
	}

	protected void print(boolean b) {
		System.out.println(b);
	}
	private void print(int i) {
		System.out.println(i);
	}
}
