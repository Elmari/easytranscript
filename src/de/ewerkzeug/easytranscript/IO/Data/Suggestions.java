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
package de.ewerkzeug.easytranscript.IO.Data;

import static de.ewerkzeug.easytranscript.Core.V.logger;
import static de.ewerkzeug.easytranscript.Core.V.suggestionList;
import static de.ewerkzeug.easytranscript.Core.V.suggestionNeededLength;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * 
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
public class Suggestions {

    public static void build(final Document doc) {
        
       
        if (de.ewerkzeug.easytranscript.Core.V.prop.getBoolProperty("suggestions")==true){
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {

                try {
                    
                    if (doc.getLength()==0) return "";
                    
                
                    String[] sA = doc.getText(0, doc.getLength() - 1).split("\\s+");

                    for (String word : sA) {

                        add(word);
                    }

                    Collections.sort(suggestionList);

                } catch (BadLocationException ex) {
                    Logger.getLogger(Suggestions.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void done() {
                logger.log(Level.INFO, "Done creating initial Suggestions-List.");
               toConsole();
     

            }

        };

        worker.execute();
        }
    }

    public static void add(String value) {
        if (de.ewerkzeug.easytranscript.Core.V.prop.getBoolProperty("suggestions")==true){
      

    
        if (value.length() >= suggestionNeededLength) {
            value = value.toLowerCase();
            if (!value.startsWith("#") && !value.endsWith("#")) {

                if (value.endsWith(".") || value.endsWith("?") || value.endsWith(",") || value.endsWith("!") || value.endsWith(";") || value.endsWith(":")) {
                    value = value.substring(0, value.length() - 1);
                }

                if (!de.ewerkzeug.easytranscript.Core.V.suggestionList.contains(value)) {

                    de.ewerkzeug.easytranscript.Core.V.suggestionList.add(value);
                    
                }
            }
        }
        }
    }


 
    public static void toConsole() {

        Iterator iterator = de.ewerkzeug.easytranscript.Core.V.suggestionList.iterator();

        while (iterator.hasNext()) {
            logger.log(Level.FINE,(String)iterator.next());
        }

    }

    public static void save(File file) {
        try {

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(suggestionList); // write MenuArray to ObjectOutputStream
            oos.close();
        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.getLocalizedMessage());
        }
    }

    public static void load(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(file));

        suggestionList = (ArrayList) input.readObject();

        input.close();
    }
}
