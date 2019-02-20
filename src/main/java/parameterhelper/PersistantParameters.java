
package parameterhelper;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.Cloneable;

public class PersistantParameters implements Cloneable {

  private Properties m_properties = new Properties();

  private String m_fileName;

  private Object m_properties_mutex = new Object();

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
   * public PersistantParameters m_parameters = new PersistantParameters("parameters.ini");
   * 
   * In mySubsystem.java (or myCommand.java): 
   * 
   * double testCalibration = Robot.m_parameters.getDouble("testCalibration", 0.0);
   * 
   * int testInteger = Robot.m_parameters.getInt("testInteger", 0);
   * 
   * 
   * Here's an example of storing a new calibration and writing it to a permanent
   * file on roborio so it can be read later.
   *
   * In Robot.java (only do this once! for efficiency):
   * 
   * public PersistantParameters m_parameters = new PersistantParameters("parameters.ini");
   * 
   * In mySubsystem.java (or myCommand.java):
   * 
   * Robot.m_parameters.put("testCalibration", 3.1415);
   * 
   * Robot.m_parameters.writeParameters();
   * 
   * writeParameters() is a special function. It will start writing the
   * file in a background task so the task that called it won't stop
   * while the file is being written.
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
    new Thread(() -> {
      try (FileOutputStream file = new FileOutputStream(m_fileName)) {

        Properties propertiesCopy;

        synchronized (m_properties_mutex) {
          propertiesCopy = (Properties) m_properties.clone();
        }

        propertiesCopy.store(file, "Robot parameters");
        file.close();

      } catch (Exception e) {
        System.out.println(e.toString());
      }
    }).start();

  }

  /*
   * Replace the in-memory copy of the parameters with the data that's in this
   * file from the file system.
   */
  public void readParameters(String fileName) {

    try (FileInputStream file = new FileInputStream(fileName)) {
      synchronized (m_properties_mutex) {
        m_properties.load(file);
      }

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
    synchronized (m_properties_mutex) {
      m_properties.put(keyName, value);
    }
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
    synchronized (m_properties_mutex) {
      m_properties.put(keyName, new Double(value).toString());
    }
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
    synchronized (m_properties_mutex) {
      m_properties.put(keyName, new Integer(value).toString());
    }
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

    String strData;
    synchronized (m_properties_mutex) {
      strData = m_properties.getProperty(keyName);
    }

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

    String strData;
    synchronized (m_properties_mutex) {
      strData = m_properties.getProperty(keyName);
    }

    try {
      returnValue = Double.parseDouble(strData);
    } catch (NullPointerException e) {
      System.out
          .println(String.format("Couldn't find parameter %s. Default value returned. %s", keyName, e.toString()));
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

    String strData;
    synchronized (m_properties_mutex) {
      strData = m_properties.getProperty(keyName);
    }

    try {
      returnValue = Integer.parseInt(strData);
    } catch (NullPointerException e) {
      System.out
          .println(String.format("Couldn't find parameter %s. Default value returned. %s", keyName, e.toString()));
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