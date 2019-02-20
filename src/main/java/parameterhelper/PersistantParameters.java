
package parameterhelper;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.Cloneable;
import java.io.PrintWriter;
import java.io.OutputStream;

public class PersistantParameters implements Cloneable {

	private Properties m_properties = new Properties();

	private String m_fileName;

	/*
	 * Create a new PersistantParameters object and attempt to open a file with the
	 * parameters. Attempt to parse that file and store the parameters into the
	 * working RAM copy of the parameters (properties) in that file.
	 * 
	 * If the file doesn't exist, which is normal for the first time this file is
	 * used, this object will emit a harmless warning.
	 * 
	 * When the parameters (the properties) are written with the writeParameters()
	 * method, this file will be created and you won't see this warning again.
	 * 
	 * Here's an example that reads two calibrations and puts them in variables so
	 * you can use them right away:
	 * 
	 * In Robot.java (only do this once! for efficiency):
	 * 
	 * public PersistantParameters m_parameters = new
	 * PersistantParameters("parameters.ini");
	 * 
	 * In mySubsystem.java (or myCommand.java):
	 * 
	 * double testCalibration = Robot.m_parameters.getDouble("testCalibration",
	 * 0.0);
	 * 
	 * int testInteger = Robot.m_parameters.getInt("testInteger", 0);
	 * 
	 * 
	 * Here's an example of storing a new calibration and writing it to a permanent
	 * file on roborio so it can be read later.
	 *
	 * In Robot.java (only do this once! for efficiency):
	 * 
	 * public PersistantParameters m_parameters = new
	 * PersistantParameters("parameters.ini");
	 * 
	 * In mySubsystem.java (or myCommand.java):
	 * 
	 * Robot.m_parameters.put("testCalibration", 3.1415);
	 * 
	 * Robot.m_parameters.writeParameters();
	 * 
	 * writeParameters() is a special function. It will start writing the file in a
	 * background task so the task that called it won't stop while the file is being
	 * written.
	 */
	public PersistantParameters(String fileName) {

		m_fileName = fileName;

		readParameters(m_fileName);
	}

	public PersistantParameters() {
	}

	public void writeParameters() {

		/*
		 * Update the parameters file to the local filesystem with the latest data from
		 * the m_properties object. Write the file in a background thread to keep the
		 * writeParameters() function from blocking in e.g. a periodic execute() task.
		 */

		/*
		 * Clone the properties memory so we don't risk modifying the version of the
		 * properties that everyone is using while writing the file in the background at
		 * the same time.
		 * 
		 * This keeps the file from being corrupted with data that changes in the middle
		 * of the file write.
		 */
		Properties propertiesCopy = (Properties) m_properties.clone();

		/*
		 * Create a background thread that will store the contents of the Properties to
		 * a file "file".
		 * 
		 * Note that the code in the braces doesn't run until the thread is started with
		 * the .start() method.
		 * 
		 * This just sets up the code to run in a new thread called fileWriting.
		 */
		Thread fileWriting = new Thread(() -> {
			try (FileOutputStream file = new FileOutputStream(m_fileName)) {

				propertiesCopy.store(file, "Robot parameters");
				file.close();

			} catch (Exception e) {
				System.out.println("Couldn't write to parameters file " + m_fileName
						+ ". Is the existing file read-only? Is the filesystem full? Can you write to this directory? "
						+ e.toString());
			}
		});

		/*
		 * Set this thread's priority below normal so it doesn't impact the robot's
		 * functionality.
		 * 
		 * Then start the thread.
		 * 
		 * The work of writing the file is all done in this background thread and this
		 * function returns right away as the background thread toils away, slowly, in
		 * the background.
		 * 
		 * When the thread exits, no one notices and no one cares. The Java garbage
		 * collector will eventually come by and recycle its unused husk.
		 */
		fileWriting.setPriority(Thread.MIN_PRIORITY);
		fileWriting.start();

	}

	/*
	 * Replace the in-memory copy of the parameters with the data that's in this
	 * file from the file system.
	 */
	public void readParameters(String fileName) {

		try (FileInputStream file = new FileInputStream(fileName)) {

			m_properties.load(file);

			file.close();

		} catch (FileNotFoundException e) {
			System.out.println(String.format(
					"Couldn't find parameters file %s. All parameters are empty. (This is ok the first time this file is used!)",
					fileName));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/*
	 * Print out all the properties and their values.
	 * 
	 * For example, this will print everything to the riolog:
	 * 
	 * public PersistantParameters m_parameters;
	 * 
	 * m_parameters = new PersistantParameters("parameters.ini");
	 * 
	 * m_parameters.listProperties(System.out);
	 * 
	 */
	public void listProperties(OutputStream out) {
		PrintWriter writer = new PrintWriter(out);
		m_properties.list(writer);

		// flush the stream
		writer.flush();

	}

	/*
	 * Store the value of the key keyName to the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.put("calibration","This Is A String");
	 * 
	 * would ultimately write
	 * 
	 * calibration=This Is A String
	 * 
	 * to the file. The file must be written with the writeParameters() method.
	 */
	public void put(String keyName, String value) {

		m_properties.put(keyName, value);

	}

	/*
	 * Store the value of the key keyName to the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.put("calibration",1.573);
	 * 
	 * would ultimately write
	 * 
	 * calibration=1.573
	 * 
	 * to the file. The file must be written with the writeParameters() method.
	 */
	public void put(String keyName, double value) {

		m_properties.put(keyName, new Double(value).toString());

	}

	/*
	 * Store the value of the key keyName to the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.put("calibration",1056);
	 * 
	 * would ultimately write
	 * 
	 * calibration=1056
	 * 
	 * to the file. The file must be written with the writeParameters() method.
	 */
	public void put(String keyName, int value) {

		m_properties.put(keyName, new Integer(value).toString());

	}

	/*
	 * Read the value of the key keyName from the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.getString("calibration","");
	 * 
	 * would ultimately return a string This Is A String if the parameters file had
	 * an entry that looked like:
	 * 
	 * calibration=This Is A String
	 */
	public String getString(String keyName, String defaultValue) {
		String returnValue = defaultValue;

		String strData = m_properties.getProperty(keyName);

		if (strData != null) {
			returnValue = strData;
		}

		return returnValue;
	}

	/*
	 * Read the value of the key keyName from the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.getString("calibration",0.0);
	 * 
	 * would ultimately return a double equal to 1.774 if the parameters file had an
	 * entry that looked like:
	 * 
	 * calibration=1.774
	 */
	public double getDouble(String keyName, double defaultValue) {
		double returnValue = defaultValue;

		String strData = m_properties.getProperty(keyName);

		try {
			returnValue = Double.parseDouble(strData);
		} catch (NullPointerException e) {
			System.out.println(String.format("Couldn't find parameter %s. Default value returned. %s", keyName, e.toString()));
		} catch (NumberFormatException e) {
			System.out.println(String.format("Couldn't convert parameter %s value %s to a double. Default value returned. %s",
					keyName, strData, e.toString()));
		}

		return returnValue;
	}

	/*
	 * Read the value of the key keyName from the in-memory copy of the parameters.
	 * 
	 * For instance, the call
	 * 
	 * parameters.getString("calibration",0);
	 * 
	 * would ultimately return a double equal to 1056 if the parameters file had an
	 * entry that looked like:
	 * 
	 * calibration=1056
	 */
	public int getInt(String keyName, int defaultValue) {
		int returnValue = defaultValue;

		String strData = m_properties.getProperty(keyName);

		try {
			returnValue = Integer.parseInt(strData);
		} catch (NullPointerException e) {
			System.out.println(String.format("Couldn't find parameter %s. Default value returned. %s", keyName, e.toString()));
		} catch (NumberFormatException e) {
			System.out.println(String.format("Couldn't convert parameter %s value %s to an int. Default value returned. %s",
					keyName, strData, e.toString()));
		}

		return returnValue;
	}

	/*
	 * Create a shallow clone of an existing PersistantParameters object without
	 * having to parse the file again. This is useful if you want to make some test
	 * parameter changes only in memory, try them out for a bit, then later commit
	 * them to the file system or not. For example:
	 * 
	 * PersistantParameters newParameters = m_parameters.clone();
	 * 
	 * System.out.println("Cloned object's output " +
	 * newParameters.getDouble("testCalibration", 0.0));
	 * 
	 * 
	 * newParameters.set("testCalibration",3.1415);
	 * 
	 * // I like this parameter set. Let's commit it to the file system.
	 * 
	 * newParameters.writeParameters();
	 */

	@Override
	public PersistantParameters clone() {
		try {
			return (PersistantParameters) super.clone();
		} catch (Exception e) {
			System.out.println(e + " Couldn't clone a PersistantParameters object");
			return new PersistantParameters();

		}
	}

}