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

package de.ewerkzeug.easytranscript.Tools;

/**
 *
 * @author e-werkzeug <administrator@e-werkzeug.eu>
 */
 
import static de.ewerkzeug.easytranscript.Core.V.logger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
 
public class FileSearch {
 
  private String fileNameToSearch;
  private final List<String> result = new ArrayList<>();
 
  public String getFileNameToSearch() {
	return fileNameToSearch;
  }
 
  public void setFileNameToSearch(String fileNameToSearch) {
	this.fileNameToSearch = fileNameToSearch;
  }
 
  public List<String> getResult() {
	return result;
  }
 
 
  public void searchDirectory(File directory, String fileNameToSearch) {
 
      
    
      
	setFileNameToSearch(fileNameToSearch);
 
	if (directory.isDirectory()) {
	    search(directory);
	} else {
	    logger.log(Level.WARNING, "{0} is not a directory!", directory.getAbsoluteFile());
	}
 
  }
 
  private void search(File file) {
 
	if (file.isDirectory()) {
	//  System.out.println("Searching directory ... " + file.getAbsoluteFile());
 
            //do you have permission to read this directory?	
	    if (file.canRead()) {
		for (File temp : file.listFiles()) {
		    if (temp.isDirectory()) {
			search(temp);
		    } else {
			if (getFileNameToSearch().equals(temp.getName().toLowerCase())) {			
			    result.add(temp.getAbsoluteFile().toString());
		    }
 
		}
	    }
 
	 } else {
                logger.log(Level.WARNING, "Search: {0}Permission Denied", file.getAbsoluteFile());
		
	 }
      }
 
  }
 
}