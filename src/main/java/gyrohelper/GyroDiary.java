package gyrohelper;

import java.util.LinkedList;

public class GyroDiary {

  /*
   * This is the minimum duration, in milliseconds, of records. Thus, 1000
   * guarantees we'll be able to look back at least 1000 ms to find the heading at
   * that time.
   */
  private long m_minimumRecordTime = 1000;
  private LinkedList<GyroReading> m_gyroData = new LinkedList<>();

  public void setMinimumRecordTime(long minimumRecordTime) {
    m_minimumRecordTime = minimumRecordTime;
  }

  /*
   * Add an entry to the diary. The current system time is used for the timeStamp.
   */
  public void add(double angle) {
    GyroReading gyroReading = new GyroReading(angle, System.currentTimeMillis());

    add(gyroReading);
  }

  /*
   * Add an entry to the diary. Use the GyroReading that the caller provided.
   * Presumably, they have older information available stored in GyroReading.
   */
  private void add(GyroReading gyroReading) {
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
      strDump = strDump + String.format("%f,%d; ", reading.angle, reading.timeStamp);
    }
    strDump = strDump + "]\n";
    return strDump;
  }

  /*
   * Get the heading at a point in time in the past. If the timeStamp occurred
   * between any of the timeStamps stored in m_gyroData, interpolate those
   * headings to get a good approximation of the heading at the timeStamp.
   * 
   * If the timeStamp is too young (it's too big), return the youngest heading.
   * 
   * If the timeStamp is too old (it's too small), return the oldest heading.
   * 
   * If we don't have any heading information at all, return NaN (Not a Number).
   */
  public double getHeadingAtTimeStamp(long timeStamp) {

    double heading = Double.NaN;

    try {
      if (timeStamp > m_gyroData.get(0).timeStamp) {

        heading = m_gyroData.get(0).angle;
        System.out.println(String.format(
            "Couldn't determine heading from timeStamp %d, it's too young. Returned youngest heading of %f.", timeStamp,
            heading));
        /*
         * We don't have data fresher than the timeStamp the caller is looking for; we
         * haven't acquired it yet. So just return the freshest heading we have.
         */
        return heading;
      }
    } catch (Exception e) {
      /*
       * We don't have ANY heading information. Just return NaN to the caller.
       */

      System.out.println(String.format(
          "Couldn't return ANY heading for timestamp %d. No heading data has been added. (Did you call GyroDiary::add() periodically?)",
          timeStamp));
      return Double.NaN;
    }

    try {
      if (timeStamp < m_gyroData.get(m_gyroData.size() - 1).timeStamp) {
        heading = m_gyroData.get(m_gyroData.size() - 1).angle;
        System.out.println(
            String.format("Couldn't determine heading from timeStamp %d, it's too old. Returned oldest heading of %f.",
                timeStamp, heading));
        return heading;
      }
    } catch (Exception e) {
      /*
       * We don't have ANY heading information. Just return NaN to the caller.
       */
      System.out.println(String.format(
          "Couldn't return ANY heading for timestamp %d. No heading data has been added. (Did you call GyroDiary::add() periodically?)",
          timeStamp));
      return Double.NaN;
    }

    for (int index = 0; index < m_gyroData.size() - 1; ++index) {

      if (timeStamp <= m_gyroData.get(index).timeStamp) {

        /*
         * We've found two headings that are bracketing the timeStamp the caller asked
         * for. Perform a linear interpolation between these two headings to get the
         * best heading information we can generate.
         */
        double y0 = m_gyroData.get(index).angle;
        double y1 = m_gyroData.get(index + 1).angle;

        long x0 = m_gyroData.get(index).timeStamp;
        long x1 = m_gyroData.get(index + 1).timeStamp;

        try {
          heading = (y0 + (timeStamp - x0) * (y1 - y0) / (x1 - x0));
        } catch (Exception e) {
          heading = y0;

          System.out.println(String.format("Couldn't interpolate heading for timeStamp %d. Returned %f instead. %s",
              timeStamp, heading, e.toString()));
        }

      }
    }
    return heading;
  }

}