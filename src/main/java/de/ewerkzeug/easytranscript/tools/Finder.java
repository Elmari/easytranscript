/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.tools;


import static de.ewerkzeug.easytranscript.core.Variables.easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.logger;
import static de.ewerkzeug.easytranscript.core.Variables.messages;
import static de.ewerkzeug.easytranscript.core.Variables.searchFrame;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;

/**
 * Such Methoden für den dazugehörigen Dialog
 */
public class Finder {

    /**
     * Sucht nach einer Übereinstimmung des Sucheintrags mit dem Textfeld.
     *
     * @param field JTextfield
     * @param searchPhrase Suchphrase
     * @param caseSensitive Gross-kleinschreibung beachten?
     * @param onlyWholeWords Nur ganze Wörter?
     * @param downwards runter zu suchen oder hochzu?
     * @return Stelle an der es gefunden wurde
     */
    public static int searchForMatch(JTextField field, String searchPhrase, boolean caseSensitive, boolean onlyWholeWords, boolean downwards) {

        int index = -1;

        if (downwards) {

            try {

                String text = field.getDocument().getText(field.getCaretPosition(), field.getDocument().getLength() - field.getCaretPosition());

                if (caseSensitive == false) {
                    text = text.toLowerCase();
                    searchPhrase = searchPhrase.toLowerCase();
                }

                if (onlyWholeWords == false) {
                    index = text.indexOf(searchPhrase);

                } else {

                    Pattern pattern = Pattern.compile("\\b" + searchPhrase + "\\b");

                    Matcher matcher = pattern.matcher(text);

                    if (matcher.find()) {
                        index = matcher.start();

                    } else {
                        index = -1;
                    }

                }

                if (index > -1) {
                    index = index + field.getCaretPosition();
                    field.requestFocus();
                    field.setCaretPosition(index);
                    field.moveCaretPosition(index + searchPhrase.length());
                }
            } catch (BadLocationException e) {
                logger.log(Level.WARNING, "Fehler", e);
            }
        } else {
            try {

                if (field.getSelectedText() != null) {
                    field.setCaretPosition(field.getCaretPosition() - field.getSelectedText().length());
                }

                String text = field.getDocument().getText(0, field.getCaretPosition());

                if (caseSensitive == false) {
                    text = text.toLowerCase();
                    searchPhrase = searchPhrase.toLowerCase();
                }

                index = text.lastIndexOf(searchPhrase);

                if (index != -1) {

                    field.requestFocus();
                    field.setCaretPosition(index);
                    field.moveCaretPosition(index - searchPhrase.length());
                }
            } catch (BadLocationException e) {
                logger.log(Level.WARNING, "Fehler", e);
            }
        }

        if (index == -1) {
            java.awt.Toolkit.getDefaultToolkit().beep();

        }

        return index;
    }

    /**
     * Ersetzt eine gefundene Suchphrase.
     *
     * @param field JTextfield
     * @param searchPhrase Suchphrase
     * @param replacePhrase Ersetzphrase
     * @param caseSensitive Gross und Kleinschreibung beachten?
     * @param onlyWholeWords Nur ganze Wörter?
     * @param downwards runterzu oder hochzu?
     * @return index
     */
    public static int replace(JTextField field, String searchPhrase, String replacePhrase, boolean caseSensitive, boolean onlyWholeWords, boolean downwards) {
        if (field.getSelectedText() != null) {
            field.setCaretPosition(field.getCaretPosition() - field.getSelectedText().length());
        }

        int index = searchForMatch(field, searchPhrase, caseSensitive, onlyWholeWords, downwards);
        if (index > -1) {
            field.replaceSelection(replacePhrase);
        }
        return index;
    }

    /**
     * Sucht nach einer Übereinstimmung des Sucheintrags mit dem Textfeld.
     *
     * @return index
     */
    public static int searchForMatch() {

        String searchPhrase = searchFrame.getSearchSearchphraseTextfield().getText();

        int index = -1;

        if (searchFrame.getSearchDownwardsRadiobutton().isSelected()) {

            try {

int end=0, start =0;
                String text = easytranscript.getMainCenterEditorEditorPane().getDocument().getText(easytranscript.getMainCenterEditorEditorPane().getCaretPosition(), easytranscript.getMainCenterEditorEditorPane().getDocument().getLength() - easytranscript.getMainCenterEditorEditorPane().getCaretPosition());

                if (!searchFrame.getSearchCaseSensitiveCheckbox().isSelected()) {
                    text = text.toLowerCase();
                    searchPhrase = searchPhrase.toLowerCase();
                }

               if (!searchFrame.getRegexSearchCheckbox().isSelected()){
                    searchPhrase=searchPhrase.replace(".", "\\.");
                    searchPhrase=searchPhrase.replace("*","\\*");
                    searchPhrase=searchPhrase.replace(")", "\\)");             
                    searchPhrase=searchPhrase.replace("(", "\\(");
                    
                    searchPhrase=searchPhrase.replace("{", "\\{");
                    searchPhrase=searchPhrase.replace("}", "\\}");
                    searchPhrase=searchPhrase.replace("]", "\\[");
                    searchPhrase=searchPhrase.replace("]", "\\]");
                    searchPhrase=searchPhrase.replace("\\", "\\\\");
                    searchPhrase=searchPhrase.replace("+", "\\+");
                    searchPhrase=searchPhrase.replace("-", "\\-");
                    searchPhrase=searchPhrase.replace("^", "\\^");
                    searchPhrase=searchPhrase.replace("+", "\\+");
                    searchPhrase=searchPhrase.replace("?", "\\?");
               }
                String wordB="";
                if (searchFrame.getSearchOnlyWholeWordsCheckbox().isSelected()){
                    wordB = "\\b";
                }
                    Pattern pattern = Pattern.compile(wordB + searchPhrase + wordB);

                    Matcher matcher = pattern.matcher(text);

                    if (matcher.find()) {
                        index = matcher.start();
                        start=index;
                       end= matcher.end();
                    } else {
                        index = -1;
                    }

                

                if (index > -1) {
                     
                    index = index + easytranscript.getMainCenterEditorEditorPane().getCaretPosition();
                    easytranscript.getMainCenterEditorEditorPane().requestFocus();
                    easytranscript.getMainCenterEditorEditorPane().setCaretPosition(index);
                    easytranscript.getMainCenterEditorEditorPane().moveCaretPosition(index + (end - start));
                }
            } catch (BadLocationException e) {
                logger.log(Level.WARNING, "Fehler", e);
            }catch (java.util.regex.PatternSyntaxException ex){
                index = -1;
                logger.log(Level.WARNING,"Ungültige regex", ex);
            }
        } else {
            try {
int start=0, end = 0;
                if (easytranscript.getMainCenterEditorEditorPane().getSelectedText() != null) {
                    easytranscript.getMainCenterEditorEditorPane().setCaretPosition(easytranscript.getMainCenterEditorEditorPane().getCaretPosition() - easytranscript.getMainCenterEditorEditorPane().getSelectedText().length());
                }

                String text = easytranscript.getMainCenterEditorEditorPane().getDocument().getText(0, easytranscript.getMainCenterEditorEditorPane().getCaretPosition());

                if (!searchFrame.getSearchCaseSensitiveCheckbox().isSelected()) {
                    text = text.toLowerCase();
                    searchPhrase = searchPhrase.toLowerCase();
                }

               if (!searchFrame.getRegexSearchCheckbox().isSelected()){
                    searchPhrase=searchPhrase.replace(".", "\\.");
                    searchPhrase=searchPhrase.replace(")", "\\)");             
                    searchPhrase=searchPhrase.replace("(", "\\(");
                    searchPhrase=searchPhrase.replace("*","\\*");
                    searchPhrase=searchPhrase.replace("{", "\\{");
                    searchPhrase=searchPhrase.replace("}", "\\}");
                    searchPhrase=searchPhrase.replace("]", "\\[");
                    searchPhrase=searchPhrase.replace("]", "\\]");
                    searchPhrase=searchPhrase.replace("\\", "\\\\");
                    searchPhrase=searchPhrase.replace("+", "\\+");
                    searchPhrase=searchPhrase.replace("-", "\\-");
                    searchPhrase=searchPhrase.replace("^", "\\^");
                    searchPhrase=searchPhrase.replace("+", "\\+");
                    searchPhrase=searchPhrase.replace("?", "\\?");
               }
                String wordB="";
                if (searchFrame.getSearchOnlyWholeWordsCheckbox().isSelected()){
                    wordB = "\\b";
                }
                    Pattern pattern = Pattern.compile(wordB + searchPhrase + wordB);

                    Matcher matcher = pattern.matcher(text);

                    index=-1;
                    while (matcher.find()) {
                        index = matcher.start();
                        start=index;
                       end= matcher.end();
                    }

                if (index != -1) {
                    //           index = index + Textfield.getCaretPosition();
                    easytranscript.getMainCenterEditorEditorPane().requestFocus();
                    easytranscript.getMainCenterEditorEditorPane().setCaretPosition(index);
                    easytranscript.getMainCenterEditorEditorPane().moveCaretPosition(index + (end-start));
                }
            } catch (BadLocationException e) {
                logger.log(Level.WARNING, "Fehler", e);
            } catch (java.util.regex.PatternSyntaxException ex){
                index = -1;
                logger.log(Level.WARNING,"Ungültige regex", ex);
            }
        }

        if (index == -1) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            searchFrame.getSearchStatus1Label().setVisible(true);
            searchFrame.getSearchStatus2Label().setVisible(true);
            searchFrame.getSearchStatus2Label().setText(messages.getString("searchMessage"));
        } else {
            searchFrame.getSearchStatus1Label().setVisible(false);
            searchFrame.getSearchStatus2Label().setVisible(false);
        }
        
        
      
        searchFrame.requestFocusInWindow();
        searchFrame.toFront();
        return index;
    }

    /**
     * Ersetzt eine gefundene Suchphrase.
     *
     * @return index
     */
    public static int replace() {
        if (easytranscript.getMainCenterEditorEditorPane().getSelectedText() != null) {
            easytranscript.getMainCenterEditorEditorPane().setCaretPosition(easytranscript.getMainCenterEditorEditorPane().getCaretPosition() - easytranscript.getMainCenterEditorEditorPane().getSelectedText().length());
        }

        int index = searchForMatch();
        if (index > -1) {
            easytranscript.getMainCenterEditorEditorPane().replaceSelection(searchFrame.getSearchReplaceTextfield().getText());
        }
        return index;
    }

}
