
package parameterhelper;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class PersistantParameters {

    private Properties m_properties = new Properties();

    private String m_fileName;

    public PersistantParameters(String fileName) {

        m_fileName = fileName;

        try {
            FileInputStream file = new FileInputStream(m_fileName);

            m_properties.load(file);
            file.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    public String get(String keyName, String defaultValue) {

        String strData = m_properties.getProperty(keyName);

        if (strData != null) {
            return strData;
        }

        return defaultValue;
    }

    private void writeParameters() {

        try {
            FileOutputStream file = new FileOutputStream(m_fileName);

            m_properties.store(file, "Parameters and their data");
            file.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private void put(String keyName, String value) {

        m_properties.put(keyName, value);
    }

}