/*
 * Copyright (C) 2014 e-werkzeug <administrator@e-werkzeug.eu>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.ewerkzeug.easytranscript.gui.misc;

import de.ewerkzeug.easytranscript.core.Variables;
import de.ewerkzeug.easytranscript.core.Variables.Mode;
import static de.ewerkzeug.easytranscript.core.Variables.casChanged;
import static de.ewerkzeug.easytranscript.core.Variables.currentAttributeSet;
import static de.ewerkzeug.easytranscript.core.Variables.easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.suggestionList;
import static de.ewerkzeug.easytranscript.core.Variables.workTime;
import static de.ewerkzeug.easytranscript.core.Variables.zeitFrame;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

/**
 * Startet bei Veränderungen einen neuen Worktime Eintrag und setzt die Werte
 * unsaved und backupNeeded auf true.
 */
public class TextfieldListener implements DocumentListener {

    @Override
    public void insertUpdate(final DocumentEvent ev) {
       /*
        int posC = easytranscript.getMainCenterEditorEditorPane().getCaretPosition() + ev.getLength();
        if (posC >= easytranscript.getMainCenterEditorEditorPane().getDocument().getLength()) {
            posC = easytranscript.getMainCenterEditorEditorPane().getDocument().getLength();
        }
        if (posC < 0) {
            posC = 0;
        }

        easytranscript.getMainCenterEditorEditorPane().setCaretPosition(posC);
*/

        if (workTime.getRecordingTime() == false && zeitFrame.getZeitActivateCheckbox().isSelected()) {
            workTime.startNewWorkTimeEntry();

        }
        action();
        
    
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (casChanged) {
                 
                   ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).setCharacterAttributes(easytranscript.getMainCenterEditorEditorPane().getCaretPosition() - 1, 1, currentAttributeSet, false);
                    easytranscript.getMainCenterEditorEditorPane().setCaretPosition(easytranscript.getMainCenterEditorEditorPane().getCaretPosition());
                    casChanged=false;
                //    new CaretPositionInformation().execute();
                }
                
                
                
            }
        });
        // Suggestion-List Teil
        if (Variables.prop.getBoolProperty("suggestions") == true) {
         
            if (ev.getLength() != 1) {
                return;
            }

            int pos = ev.getOffset();
            String content = null, content2 = "";
            try {

                content = easytranscript.getMainCenterEditorEditorPane().getDocument().getText(0, pos + 1);
                content2 = easytranscript.getMainCenterEditorEditorPane().getDocument().getText(easytranscript.getMainCenterEditorEditorPane().getCaretPosition(), 1);

                content2 = content2.replace(" ", "");

            } catch (BadLocationException e) {
                //    logger.log(Level.WARNING, e.getLocalizedMessage());
            }

            // Find where the word starts
            int w;

            for (w = pos; w >= 0; w--) {
                if (!Character.isLetter(content.charAt(w))) {
                    break;
                }
            }

            if (pos - w < 2) {
                // Too few chars
                return;
            }

            String prefix = content.substring(w + 1).toLowerCase();

            char first = '1';
            if (content2.length() > 0) {
                first = content2.toCharArray()[0];
            }

           // if (!Character.isLetter(first)) {
                 
                int n = Collections.binarySearch(suggestionList, prefix);

                if (n < 0 && -n <= suggestionList.size()) {
                    String match = suggestionList.get(-n - 1);
                    if (match.startsWith(prefix)) {
                        // A completion is found
                        String completion = match.substring(pos - w);
                        // We cannot modify Document from within notification,
                        // so we submit a task that does the change later
                        SwingUtilities.invokeLater(
                                new CompletionTask(completion, pos + 1));
                    }
                } else {

                    // Nothing found
                    Variables.insertionMode = Mode.INSERT;
                }
            //} else {

                // Nothing found
              //  de.ewerkzeug.easytranscript.core.V.insertionMode = Mode.INSERT;
           // }

        }

        if (Variables.undoRedo == -1) {
      //      easytranscript.getMainCenterEditorEditorPane().setCaretPosition(ev.getOffset());
            Variables.undoRedo = 0;
        } else if (Variables.undoRedo == 1) {
            int idx = ev.getOffset() + 1;
            if (idx >= easytranscript.getMainCenterEditorEditorPane().getDocument().getLength()) {
                idx = easytranscript.getMainCenterEditorEditorPane().getDocument().getLength() - 1;
            }
        //    easytranscript.getMainCenterEditorEditorPane().setCaretPosition(idx);
            Variables.undoRedo = 0;
        }
        
       

// !--- Suggestion List Teil
    }

    @Override
    public void removeUpdate(DocumentEvent e) {

        if (Variables.insertionMode != Mode.COMPLETION) {
/*
            int pos = easytranscript.getMainCenterEditorEditorPane().getCaretPosition() - e.getLength();
            if (pos < 0) {
                pos = 0;
            }

            if (pos >= easytranscript.getMainCenterEditorEditorPane().getDocument().getLength()) {

                pos = 0;
            } else {

                if (easytranscript.getMainCenterEditorEditorPane().getSelectionStart() == easytranscript.getMainCenterEditorEditorPane().getCaretPosition()
                        && easytranscript.getMainCenterEditorEditorPane().getSelectionStart() != easytranscript.getMainCenterEditorEditorPane().getSelectionEnd()) {
                    pos = easytranscript.getMainCenterEditorEditorPane().getCaretPosition();
                }
            }
            easytranscript.getMainCenterEditorEditorPane().setCaretPosition(pos);*/
        }

        if (workTime.getRecordingTime() == false && zeitFrame.getZeitActivateCheckbox().isSelected()) {
            workTime.startNewWorkTimeEntry();

        }

        if (Variables.undoRedo == -1) {
    //        easytranscript.getMainCenterEditorEditorPane().setCaretPosition(e.getOffset());
            Variables.undoRedo = 0;
        } else if (Variables.undoRedo == 1) {
            int idx = e.getOffset() + 1;
            if (idx >= easytranscript.getMainCenterEditorEditorPane().getDocument().getLength()) {
                idx = easytranscript.getMainCenterEditorEditorPane().getDocument().getLength() - 1;
            }
      //      easytranscript.getMainCenterEditorEditorPane().setCaretPosition(idx);
            Variables.undoRedo = 0;
        }

        action();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        action();
    }

    public void action() {
        de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.setUnsaved(true);
        de.ewerkzeug.easytranscript.IO.data.TranscriptHandler.setBackupNeeded(true);
      

    }

    /**
     * Zeigt den möglichen Rest des Wortes an.
     */
    private class CompletionTask implements Runnable {

        String completion;
        int position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        @Override
        public void run() {

            Document doc = easytranscript.getMainCenterEditorEditorPane().getDocument();

            try {
                doc.insertString(position, completion, Variables.currentAttributeSet);
                easytranscript.getMainCenterEditorEditorPane().setCaretPosition(position + completion.length());
                easytranscript.getMainCenterEditorEditorPane().moveCaretPosition(position);
                Variables.insertionMode = Mode.COMPLETION;
            } catch (BadLocationException ex) {
                Logger.getLogger(TextfieldListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
