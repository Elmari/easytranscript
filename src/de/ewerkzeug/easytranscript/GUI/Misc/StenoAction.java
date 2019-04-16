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
package de.ewerkzeug.easytranscript.GUI.Misc;

import de.ewerkzeug.easytranscript.Core.V;
import static de.ewerkzeug.easytranscript.Core.V.currentAttributeSet;
import static de.ewerkzeug.easytranscript.Core.V.easytranscript;
import static de.ewerkzeug.easytranscript.Core.V.logger;
import static de.ewerkzeug.easytranscript.Core.V.orientationRT;
import static de.ewerkzeug.easytranscript.Core.V.prop;
import static de.ewerkzeug.easytranscript.Core.V.steno;
import static de.ewerkzeug.easytranscript.Core.V.suggestionList;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class StenoAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                de.ewerkzeug.easytranscript.Core.V.insertionMode = V.Mode.INSERT;
                easytranscript.getMainCenterEditorEditorPane().replaceSelection("");

                StyledDocument doc = (StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument();
                int caret = easytranscript.getMainCenterEditorEditorPane().getCaretPosition();
                String s;

                try {
                    String[] sA;
                    if (orientationRT == true) {
                        sA = doc.getText(caret, doc.getLength() - caret).split("\\s+");
                        s = sA[0];

                    } else {
                        sA = doc.getText(0, caret).split("\\s+");
                        s = sA[sA.length - 1];
                    }

                    easytranscript.getMainCenterEditorEditorPane().setSelectionStart(caret - s.length());
                    easytranscript.getMainCenterEditorEditorPane().setSelectionEnd(caret);

                    String pattern = easytranscript.getMainCenterEditorEditorPane().getSelectedText();

                    String value = null;

                    if (prop.getProperty("ConfigStenoActivate").equals("true")) {
                        value = (String) steno.get(pattern);
                    }

                   
                    
                    if (value != null) {

                        final String value2 = value;
                        final int caret2 = caret;
                        final String pattern2 = pattern;
                        final String s2 = s;
                        javax.swing.SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                easytranscript.getMainCenterEditorEditorPane().setSelectionStart(caret2 - s2.length());
                                easytranscript.getMainCenterEditorEditorPane().setSelectionEnd(caret2);
                                easytranscript.getMainCenterEditorEditorPane().replaceSelection(value2);

                            }

                        });

                        javax.swing.SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {

                                easytranscript.getMainCenterEditorEditorPane().setCaretPosition(caret2 + (value2.length() - pattern2.length()));
                                easytranscript.getMainCenterEditorEditorPane().setSelectionStart(caret2 + (value2.length() - pattern2.length()));
                                easytranscript.getMainCenterEditorEditorPane().setSelectionEnd(caret2 + (value2.length() - pattern2.length()));
                            }
                        });

                        // Fügt das Stenographie wort der Suggestion-List hinzu und sortiert die Liste alphabetisch.
                        if (prop.getBoolProperty("suggestions") == true) {
                            de.ewerkzeug.easytranscript.IO.Data.Suggestions.add(value);
                            Collections.sort(suggestionList);
                        }

                    } else {

                        // Falls kein Stenowort gefunden wurde, füge das Wort der Liste hinzu und sortiere.
                        if (prop.getBoolProperty("suggestions") == true) {
                            de.ewerkzeug.easytranscript.IO.Data.Suggestions.add(easytranscript.getMainCenterEditorEditorPane().getSelectedText());
                            Collections.sort(suggestionList);

                            easytranscript.getMainCenterEditorEditorPane().setSelectionStart(caret);
                            easytranscript.getMainCenterEditorEditorPane().setSelectionEnd(caret);
                            if (prop.getIntProperty("ConfigStenoTrigger") == 0) {
                                easytranscript.getMainCenterEditorEditorPane().replaceSelection("\t");
                            }
                        }else{
                             easytranscript.getMainCenterEditorEditorPane().setSelectionStart(caret);
                            easytranscript.getMainCenterEditorEditorPane().setSelectionEnd(caret);
                        }
                         javax.swing.SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    easytranscript.getMainCenterEditorEditorPane().getDocument().insertString(easytranscript.getMainCenterEditorEditorPane().getCaretPosition(), " ", ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(easytranscript.getMainCenterEditorEditorPane().getCaretPosition()-1).getAttributes());
                                } catch (BadLocationException ex) {
                                    Logger.getLogger(StenoAction.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                         });
                    }

                } catch (BadLocationException ex) {
                    logger.log(Level.WARNING, "Bad Location {0}", new Object[]{ex.getLocalizedMessage()});
                }
            }

        });
    }
}
