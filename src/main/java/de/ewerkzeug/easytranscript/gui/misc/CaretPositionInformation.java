/**
 *
 *
 * easytranscript Copyright (C) 2013 e-werkzeug
 *
 * Dieses Programm ist freie Software. Sie können es unter den Bedingungen der
 * GNU General Public License, wie von der Free Software Foundation
 * veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß Version 3
 * der Lizenz oder (nach Ihrer Option) jeder späteren Version. Die
 * Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von
 * Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite
 * Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK.
 * Details finden Sie in der GNU General Public License.
 *
 * Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 *
 */
package de.ewerkzeug.easytranscript.gui.misc;

import static de.ewerkzeug.easytranscript.core.Easytranscript.MainToolbarBoldButton;
import static de.ewerkzeug.easytranscript.core.Easytranscript.MainToolbarFontsizeCombobox;
import static de.ewerkzeug.easytranscript.core.Easytranscript.MainToolbarItalicButton;
import static de.ewerkzeug.easytranscript.core.Easytranscript.MainToolbarUnderlinedButton;
import de.ewerkzeug.easytranscript.core.Variables;
import static de.ewerkzeug.easytranscript.core.Variables.currentAttributeSet;
import static de.ewerkzeug.easytranscript.core.Variables.easytranscript;
import static de.ewerkzeug.easytranscript.core.Variables.fontDropDown;
import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * SwingWorker. Gibt Aufschluss über die aktuelle Formatierung an der
 * cursor-Position und interagiert mit der GUI
 */
public class CaretPositionInformation extends SwingWorker<String, Object> {

    String fontFamily = "";
    int fontSize = 0;
    boolean Bold = false, Italic = false, Underlined = false;

    @Override
    public String doInBackground() {

        AttributeSet attr;
        
  
        
        attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(easytranscript.getMainCenterEditorEditorPane().getCaretPosition() - 1).getAttributes();
        
        if (currentAttributeSet != null ) {
           if (!Variables.casChanged){
          /*  if (!attr.getAttribute(StyleConstants.FontSize).equals(currentAttributeSet.getAttribute(StyleConstants.FontSize))) {
                currentAttributeSet.removeAttribute(StyleConstants.FontSize);
                currentAttributeSet.addAttribute(StyleConstants.FontSize, Integer.parseInt(attr.getAttribute(StyleConstants.FontSize).toString()));
                System.out.println(Integer.parseInt(attr.getAttribute(StyleConstants.FontSize).toString()));
              
            }
            if (!attr.getAttribute(StyleConstants.FontFamily).equals(currentAttributeSet.getAttribute(StyleConstants.FontFamily))) {
                currentAttributeSet.removeAttribute(StyleConstants.FontFamily);
                currentAttributeSet.addAttribute(StyleConstants.FontFamily, attr.getAttribute(StyleConstants.FontFamily));
             
            }*/
           }
        } else {
            currentAttributeSet = new SimpleAttributeSet();
            currentAttributeSet.addAttribute(StyleConstants.FontSize, attr.getAttribute(StyleConstants.FontSize));
            currentAttributeSet.addAttribute(StyleConstants.FontFamily, attr.getAttribute(StyleConstants.FontFamily));
            currentAttributeSet.addAttribute(StyleConstants.Bold, attr.getAttribute(StyleConstants.Bold));
            currentAttributeSet.addAttribute(StyleConstants.Italic, attr.getAttribute(StyleConstants.Italic));
            currentAttributeSet.addAttribute(StyleConstants.Underline, attr.getAttribute(StyleConstants.Underline));

        }

        fontFamily = attr.getAttribute(StyleConstants.FontFamily).toString();
        fontSize = Integer.parseInt(attr.getAttribute(StyleConstants.FontSize).toString()) - Variables.performanceModeFontSizeIncrease;
        if (fontSize<8){
            fontSize=12;
            StyleConstants.setFontSize(currentAttributeSet, 12+ Variables.performanceModeFontSizeIncrease);
          //  V.casChanged=true;
        }
        if (attr.getAttribute(StyleConstants.Underline) != null) {
            Underlined = (Boolean) attr.getAttribute(StyleConstants.Underline);
        }
        if (attr.getAttribute(StyleConstants.Bold) != null) {
            Bold = (Boolean) attr.getAttribute(StyleConstants.Bold);
        }

        if (attr.getAttribute(StyleConstants.Italic) != null) {
            Italic = (Boolean) attr.getAttribute(StyleConstants.Italic);
        }

        MainToolbarBoldButton.setSelected(Bold);
        MainToolbarItalicButton.setSelected(Italic);
        MainToolbarUnderlinedButton.setSelected(Underlined);
        fontDropDown.setSelectedItem(fontFamily);
        MainToolbarFontsizeCombobox.setSelectedItem(String.valueOf(fontSize));

        for (int i = easytranscript.getMainCenterEditorEditorPane().getSelectionStart(); i < easytranscript.getMainCenterEditorEditorPane().getSelectionEnd() - 1; i++) {

            attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(i).getAttributes();

            int fontSizeEnd_N = Integer.parseInt(attr.getAttribute(StyleConstants.FontSize).toString()) - Variables.performanceModeFontSizeIncrease;

            if (fontSize != 0) {
                if (fontSizeEnd_N != fontSize) {
                    MainToolbarFontsizeCombobox.setSelectedIndex(-1);
                    break;
                }
            }
        }

        for (int i = easytranscript.getMainCenterEditorEditorPane().getSelectionStart(); i < (easytranscript.getMainCenterEditorEditorPane().getSelectionEnd() - 1); i++) {
            attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(i).getAttributes();

            boolean boldEnd_N = ((Boolean) attr.getAttribute(StyleConstants.Bold));
            if (boldEnd_N != Bold) {
                MainToolbarBoldButton.setSelected(false);
                break;
            }

        }

        for (int i = easytranscript.getMainCenterEditorEditorPane().getSelectionStart(); i < (easytranscript.getMainCenterEditorEditorPane().getSelectionEnd() - 1); i++) {
            attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(i).getAttributes();
            boolean italicEnd_N = ((Boolean) attr.getAttribute(StyleConstants.Italic));
            if (italicEnd_N != Italic) {
                MainToolbarItalicButton.setSelected(false);
                break;
            }

        }
        for (int i = easytranscript.getMainCenterEditorEditorPane().getSelectionStart(); i < (easytranscript.getMainCenterEditorEditorPane().getSelectionEnd() - 1); i++) {
            attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(i).getAttributes();
            try {
                boolean underEnd_N = ((Boolean) attr.getAttribute(StyleConstants.Underline));
                if (underEnd_N != Underlined) {
                    MainToolbarUnderlinedButton.setSelected(false);
                    break;
                }
            } catch (NullPointerException e) {
    // catch exception 31.10.2014
            }
        }

        for (int i = easytranscript.getMainCenterEditorEditorPane().getSelectionStart(); i < easytranscript.getMainCenterEditorEditorPane().getSelectionEnd(); i++) {
            attr = ((StyledDocument) easytranscript.getMainCenterEditorEditorPane().getDocument()).getCharacterElement(i).getAttributes();
            String fontFamilyEnd_N = attr.getAttribute(StyleConstants.FontFamily).toString();
            if (!fontFamily.equals("")) {
                if (!fontFamilyEnd_N.equals(fontFamily)) {
                    fontDropDown.setSelectedIndex(-1);

                    break;
                }
            }
        }

        //     }
        //});
        return null;

    }

}
