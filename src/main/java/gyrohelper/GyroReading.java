
package gyrohelper;

class GyroReading {
    public double angle;
    public long timeStamp;

    public GyroReading(double newAngle, long newTimeStamp) {
        angle = newAngle;
        timeStamp = newTimeStamp;
    }

    public String toString() {
        return String.format("angle=%f,timeStamp=%d", angle, timeStamp);
    }
}