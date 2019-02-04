
package gyrohelper;

class GyroReading {
    public float angle;
    public long timeStamp;

    public GyroReading(float newAngle, long newTimeStamp) {
        angle = newAngle;
        timeStamp = newTimeStamp;
    }

    public String toString() {
        return new String().format("angle=%f,timeStamp=%d", angle, timeStamp);
    }
}