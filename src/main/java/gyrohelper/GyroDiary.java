package gyrohelper;

import java.util.LinkedList;

public class GyroDiary {

    private long m_minimumRecordTime = 1000;
    private LinkedList<GyroReading> m_gyroData = new LinkedList<>();

    public void setMinimumRecordTime(long minimumRecordTime) {
        m_minimumRecordTime = minimumRecordTime;
    }

    public void add(float angle) {
        GyroReading gyroReading = new GyroReading(angle, System.currentTimeMillis());

        add(gyroReading);
    }

    public void add(float angle, long timeStamp) {
        GyroReading gyroReading = new GyroReading(angle, timeStamp);

        add(gyroReading);
    }

    public void add(GyroReading gyroReading) {
        m_gyroData.addFirst(gyroReading);

        try {
            while (gyroReading.timeStamp - (m_gyroData.get(m_gyroData.size() - 2).timeStamp) >= m_minimumRecordTime) {
                m_gyroData.removeLast();
            }
        } catch (Exception NoSuchElementException) {

        }
    }

    public void clear() {
        m_gyroData.clear();
    }

    public String dump() {
        String strDump = new String("[");
        for (GyroReading reading : m_gyroData) {
            strDump = strDump + new String().format("%f,%d; ", reading.angle, reading.timeStamp);
        }
        strDump = strDump + "]\n";
        return strDump;
    }

    public float getHeadingAtTimeStamp(long timeStamp) throws ArithmeticException {
        int index;
        for (index = 0; index < m_gyroData.size(); ++index) {

            if (timeStamp >= m_gyroData.get(index).timeStamp) {

                if (index == 0) {
                    return m_gyroData.get(0).angle;
                }

                float y0 = m_gyroData.get(index).angle;
                float y1 = m_gyroData.get(index + 1).angle;

                long x0 = m_gyroData.get(index).timeStamp;
                long x1 = m_gyroData.get(index + 1).timeStamp;

                float y = (y0 + (timeStamp - x0) * (y1 - y0) / (x1 - x0));

                return y;

            }
        }

        return m_gyroData.get(m_gyroData.size() - 1).angle;
    }

}