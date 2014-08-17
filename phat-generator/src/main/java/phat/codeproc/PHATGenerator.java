/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.codeproc;

import ingenias.editor.Log;
import ingenias.editor.ModelJGraph;
import ingenias.editor.ProjectProperty;
import ingenias.exception.NotFound;
import ingenias.exception.NullEntity;
import ingenias.generator.browser.Browser;
import ingenias.generator.browser.Graph;
import ingenias.generator.browser.GraphAttribute;
import ingenias.generator.browser.GraphEntity;
import ingenias.generator.browser.GraphRelationship;
import ingenias.generator.browser.GraphRole;
import ingenias.generator.datatemplate.Repeat;
import ingenias.generator.datatemplate.Sequences;
import ingenias.generator.datatemplate.Var;
import ingenias.generator.interpreter.SplitHandler;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;

import phat.codeproc.pd.PDGenerator;

import org.bouncycastle.jce.provider.JDKMessageDigest.MD5;

class FileUtils {
	public static byte[] readFileAsBytes(String filename)
			throws FileNotFoundException, IOException {
		FileInputStream fis=new FileInputStream(filename);
		Vector<Byte> sb=new Vector<Byte>();
		int read=0;
		while (read!=-1){
			read=fis.read();
			if (read!=-1)
				sb.add((byte)read);
		}
		fis.close();
		byte[] array=new byte[sb.size()];
		for (int k=0;k<array.length;k++)
			array[k]=sb.elementAt(k);
		return array;
	}

	public static StringBuffer readFile(String filename)
			throws FileNotFoundException, IOException {
		FileInputStream fis=new FileInputStream(filename);
		StringBuffer sb=new StringBuffer();
		int read=0;
		byte[] buffer =new byte[1000];
		while (read!=-1){
			read=fis.read(buffer);
			if (read!=-1){
				for (int k=0;k<read;k++)
					sb.append((char)buffer[k]);
			}
		}
		fis.close();
		return sb;
	}
}

public class PHATGenerator extends
ingenias.editor.extension.BasicCodeGeneratorImp {

	static final String HUMAN_PROFILE_SPEC_DIAGRAM = "HumanProfileSpecDiagram";
	static final String ADLProfile_SPEC_DIAGRAM = "ADLProfile";

	public PHATGenerator(String file) throws Exception {
		super(file);
		this.addTemplate("templates/agents2.xml");
		this.addTemplate("templates/scenario2.xml");
		this.addTemplate("templates/timeinterval.xml");
		this.addTemplate("templates/activities.xml");
		this.addTemplate("templates/tasks.xml");
		this.addTemplate("templates/disease_profile.xml");
		this.addTemplate("templates/buildext.xml");

	}

	public PHATGenerator(Browser browser) throws Exception {
		super(browser);
		this.addTemplate("templates/agents2.xml");
		this.addTemplate("templates/scenario2.xml");
		this.addTemplate("templates/timeinterval.xml");
		this.addTemplate("templates/activities.xml");
		this.addTemplate("templates/tasks.xml");
		this.addTemplate("templates/disease_profile.xml");
		this.addTemplate("templates/buildext.xml");
	}

	public String getVersion() {
		return "@modhtmldoc.ver@";
	}

	public static void main(String[] args) throws Exception {
		System.out
		.println("PHAT Generator by Pablo Campillo based on INGENIAS Code Generator by Jorge Gomez");
		System.out
		.println("This program comes with ABSOLUTELY NO WARRANTY; for details check www.gnu.org/copyleft/gpl.html.");
		System.out
		.println("This is free software, and you are welcome to redistribute it under certain conditions;; for details check www.gnu.org/copyleft/gpl.html.");

		if (args.length == 0) {
			System.err
			.println("The first argument (mandatory) has to be the specification file and the second "
					+ "the outputfolder folder");
		} else {

			if (args.length >= 2) {
				String prefix=new File(args[0]).getAbsolutePath().replace("/", "").replace("\\", "");
				boolean allFilesExist=checkFiles(prefix);
				StringBuffer sb =  FileUtils.readFile(args[0]);			
				byte[] checksum =getCheckSum(sb.toString());		
				if (!java.util.Arrays.equals(getLastCheckSum(new File(args[0]).getAbsolutePath()),checksum)
						|| !allFilesExist){ 
					ingenias.editor.Log.initInstance(new PrintWriter(System.out));
					ModelJGraph.disableAllListeners(); // this disable layout
					// listeners that slow down
					// code generation
					// it is a bug of the platform which will be addressed in the
					// future

					ingenias.editor.Log.initInstance(new PrintWriter(System.out));
					PHATGenerator generator = new PHATGenerator(args[0]);
					Properties props = generator.getBrowser().getState().prop;
					new File(args[1]).mkdirs();
					generator.setProperty("output", args[1]);
					HashSet<File> files=new HashSet<File>();
					Vector<SplitHandler> handlers = generator.runWithoutWriting();					
					for (SplitHandler sh:handlers){
						 files.addAll(sh.filesToBeWritten());
						 sh.writeFiles();
						 
					}					
					if (ingenias.editor.Log.getInstance().areThereErrors()) {
						for (Frame f : Frame.getFrames()) {
							f.dispose();

						}
						throw new RuntimeException(
								"There are the following code generation errors: "
										+ Log.getInstance().getErrors());
					}
					storeFiles(prefix,files);
					storeChecksum(new File(args[0]).getAbsolutePath(),checksum);
				}
			} else {
				System.err
				.println("The first argument (mandatory) has to be the specification file and the second  "
						+ "the outputfolder");
			}

		}
		for (Frame f : Frame.getFrames()) {
			f.dispose();
		}

	}

	public void generateActivities(Graph activitiesSpec, Sequences seq)
			throws NotFound, NullEntity {
		GraphEntity ge = Utils.getFirstEntity(activitiesSpec);

		if (ge.getType().equals("BActivity")) {
			GraphEntity nActivity = Utils.getTargetEntity(ge, "NextActivity");
			if (nActivity == null) {
				return;
			}
			GraphAttribute ga = ge.getAttributeByName("SeqTaskDiagramField");
			Graph taskSpecDiagram = Utils.getGraphByName(ga.getSimpleValue(),
					getBrowser());

			/*if (taskSpecDiagram != null) {
				TaskGenerator taskGenerator = new TaskGenerator(getBrowser(),
						seq);
				taskGenerator.generate("", taskSpecDiagram);
			}*/
		}
	}

	/**
	 * Generates HTML code
	 * 
	 * @exception Exception
	 *                XML exception
	 */
	public Sequences generate() {
		Sequences seq = new Sequences();

		seq.addVar(new Var("output", this.getProperty("output").value));
		try {
			new AgentsGenerator(browser).generateAgents(seq);
			new TaskGenerator(getBrowser(), seq).generateAllSeqTasks();
			new ActivityGenerator(getBrowser()).generateTimeIntervals(seq);
			new SimulationGenerator(browser).generateSimulations(seq);
			new PDGenerator(browser).generatePD(seq);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		return seq;
	}

	public String getName() {
		return "phatgenerator";
	}

	public String getDescription() {
		return "It generates PHAT instantiation";
	}

	public static byte[] getCheckSum(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		return  MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
	}
	
	public static void storeFiles(String prefix, HashSet<File> files) throws FileNotFoundException, IOException{
		StringBuffer filesString=new StringBuffer();
		for (File f:files){
			filesString.append(f.getAbsolutePath()+"\n");
		}		
		File homeidkfolder=new File(System.getProperty("user.home")+"/.phat");
		if (!homeidkfolder.exists())
			homeidkfolder.mkdirs();
		File lastCheckSum=new File(System.getProperty("user.home")+"/.phat/"+prefix+"checkfiles");
		new FileOutputStream(lastCheckSum).write(filesString.toString().getBytes());
	}
	
	public static boolean checkFiles(String prefix) throws FileNotFoundException, IOException{
		boolean existAll=true;
		File homeidkfolder=new File(System.getProperty("user.home")+"/.phat");
		if (!homeidkfolder.exists())
			homeidkfolder.mkdirs();
		File lastCheckSum=new File(System.getProperty("user.home")+"/.phat/"+prefix+"checkfiles");
		if (lastCheckSum.exists()){
			StringBuffer fileString=FileUtils.readFile(lastCheckSum.getAbsolutePath());
			String[] filenames = fileString.toString().split("\n");
			int k=0;
			while (existAll && k<filenames.length){
				existAll=existAll && new File(filenames[k]).exists();
				k++;
			}
		} else
			return false;
		return existAll;			
		
	}
	
	

	public static byte[] getLastCheckSum(String checksumprefix) throws FileNotFoundException, IOException {
		File homeidkfolder=new File(System.getProperty("user.home")+"/.phat");
		if (!homeidkfolder.exists())
			homeidkfolder.mkdirs();
		File lastCheckSum=new File(System.getProperty("user.home")+"/.phat/"+checksumprefix.replace("/", "").replace("\\","")+"lastchecksum");
		if (lastCheckSum.exists()){
			return FileUtils.readFileAsBytes(lastCheckSum.getAbsolutePath());

		}			
		return new byte[0];

	}
	public static void storeChecksum(String checksumprefix, byte[] checksum) throws FileNotFoundException, IOException {
		File homeidkfolder=new File(System.getProperty("user.home")+"/.phat");
		if (!homeidkfolder.exists())
			homeidkfolder.mkdirs();
		File lastCheckSum=new File(System.getProperty("user.home")+"/.phat/"+checksumprefix.replace("/", "").replace("\\","")+"lastchecksum");
		FileOutputStream fos=new FileOutputStream(lastCheckSum);
		fos.write(checksum);
		fos.close();

	}

	public Vector<ProjectProperty> defaultProperties() {
		Vector<ProjectProperty> result = new Vector<ProjectProperty>();
		Properties p = new Properties();
		result.add(new ingenias.editor.ProjectProperty(this.getName(),
				"output", "PHAT output folder", "target",
				"The folder that will contain the output"));

		/*
		 * result.add( new
		 * ingenias.editor.ProjectProperty(this.getName(),"htmldoc",
		 * "HTML document folder", "html",
		 * "The document folder that will contain HTML version of this specification"
		 * ));
		 */
		return result;
	}

}
