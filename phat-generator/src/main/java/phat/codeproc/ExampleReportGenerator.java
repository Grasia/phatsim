/*
 *  Copyright (C) 2002 Jorge Gomez Sanz
 *  This file is part of INGENIAS IDE, a support tool for the INGENIAS
 *  methodology, availabe at http://grasia.fdi.ucm.es/ingenias or
 *  http://ingenias.sourceforge.net
 *  INGENIAS IDE is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  INGENIAS IDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with INGENIAS IDE; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package phat.codeproc;

import ingenias.editor.ProjectProperty;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.*;
import java.util.*;
        
/**
 *
 * @author pablo
 */
public class ExampleReportGenerator extends ingenias.editor.extension.BasicToolImp {
	
	/**
	 *  Initialises the class with a file contianing a INGENIAS specification
	 *
	 *@param  file           Path to file containing INGENIAS specification
	 *@exception  Exception  Error accessing any file or malformed XML exception
	 */
	
	public ExampleReportGenerator(String file) throws Exception {
		super(file);
	}
	
	/**
	 *  Initialises the class giving access to diagrams in run-time
	 **/
	
	public ExampleReportGenerator(Browser browser) throws Exception {
		super(browser);
	}
	
	@Override
	public String getVersion() {
		return "@modexample.ver@";
	}
	
	
	/**
	 *  Gets the description of this module
	 *
	 *@return    The description
	 */
	public String getDescription() {
		return "This module generates an stats report using the standard output. " +
		"The report indicates what entities exist in each diagram";
	}
	
	
	/**
	 *  Gets the name of this module
	 *
	 *@return    The name
	 */
	public String getName() {
		return "Example stats report";
	}
	
	
	/**
	 *  It creates stats of usage by traversing diagrams of your specification.
	 *  Resulting report appears in standard output or in the IDE
	 */
	public void run() {
		try {
			StringBuffer sb = this.generateReport();
			ingenias.editor.Log.getInstance().log("Statistics of usage of meta-model entities and relationships");
			ingenias.editor.Log.getInstance().log("Name                                    Number of times it appears");
			ingenias.editor.Log.getInstance().log("----                                    --------------------------");
			ingenias.editor.Log.getInstance().log(sb.toString());
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	/**
	 *  This module defines no properties
	 *
	 *@return    Empty properties
	 */
	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result=new Vector<ProjectProperty>();
		
		return result;
	}
	
	
	/**
	 *  Generates the main report body
	 *
	 *@return                A stringbuffer with the report
	 *@exception  Exception  Sth. went wrong
	 */
	private StringBuffer generateReport() throws Exception {
		Graph[] gs = browser.getGraphs();
		StringBuffer result = new StringBuffer();
		Hashtable stats=new Hashtable();
		for (int k = 0; k < gs.length; k++) {
			Graph g = gs[k];
			this.generateADiagramReport(stats,g);
		}
		Object[] keys=new Vector(stats.keySet()).toArray();
		Arrays.sort(keys);
		StringBuffer report=new StringBuffer();
		for (int k=0;k<keys.length;k++){
			String key=keys[k].toString();
			report.append(key);
			for (int l=0;l<40-key.length();l++)
				report.append(" ");
			report.append(":"+stats.get(key)+"\n");
		}
		return report;
		
	}
	
	
	/**
	 *  Generates a report for each diagram type
	 *
	 *@param  g  The diagram to be studied
	 *@param  stats  Stats collected so far
	 */
	
	private void generateADiagramReport(Hashtable stats,Graph g) {
		GraphRelationship[] grels=g.getRelationships();
		
		for (int k=0;k<grels.length;k++){
			if (stats.containsKey(grels[k].getType())){
				Integer old=(Integer)stats.get(grels[k].getType());
				stats.put(grels[k].getType(),new Integer(old.intValue()+1));
				
			} else {
				stats.put(grels[k].getType(),new Integer(1));
			}
		}
		
		GraphEntity[] ges;
		try {
			ges = g.getEntities();
			
			for (int k=0;k<ges.length;k++){
				if (stats.containsKey(ges[k].getType())){
					Integer old=(Integer)stats.get(ges[k].getType());
					stats.put(ges[k].getType(),new Integer(old.intValue()+1));
				} else {
					stats.put(ges[k].getType(),new Integer(1));
				}
				
			}
		} catch (NullEntity e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 *  Generates an stats report from a INGENIAS specification file (1st param)
	 *
	 *@param  args           Arguments typed in the command line. Only first one is attended
	 *@exception  Exception  Sth went wrong
	 */
	public static void main(String args[]) throws Exception {
		ingenias.editor.Log.initInstance(new java.io.PrintWriter(System.err));
		ExampleReportGenerator erg = new ExampleReportGenerator(args[0]);
                erg.run();
		System.exit(0);
	}


	
}
