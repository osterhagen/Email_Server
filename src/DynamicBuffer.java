/**
 * 4/14/16
 * DynamicBuffer
 */
public class DynamicBuffer {
    private Email[] buffer;
    private int initSize;
    private int counter;

    public DynamicBuffer(int initSize) {
        this.initSize = initSize;
        buffer = new Email[initSize];
        counter = 0;
    }

    public int numElements() {
        return counter;
    }

    public int getBufferSize() {
        return buffer.length;
    }

    public void add(Email email) {
        if (numElements() == getBufferSize()) {
            Email[] bufferDoubleSize = new Email[2 * getBufferSize()];
            System.arraycopy(buffer, 0, bufferDoubleSize, 0, getBufferSize());
            buffer = bufferDoubleSize;
        }
        buffer[numElements()] = email;
        counter++;
        if (numElements() == getBufferSize()) {
            Email[] bufferDoubleSize = new Email[2 * getBufferSize()];
            System.arraycopy(buffer, 0, bufferDoubleSize, 0, getBufferSize());
            buffer = bufferDoubleSize;
        }
    }

    public boolean remove(int index) {
        if (buffer[index] == null)
            return false;
        if (index > buffer.length && index < 0)
            return false;
        if (index > 0 && index < buffer.length) {
            for (int i = index; i < numElements() - 1; i++) {
                if (i <= numElements()) {
                    buffer[i] = buffer[i + 1];
                }
            }
        }
        counter--;
        if (numElements() <= (getBufferSize() / 4)) {
            Email[] bufferInitSize = new Email[initSize];
            Email[] bufferHalfSize = new Email[getBufferSize() / 2];
            if (getBufferSize() / 2 < initSize) {
                System.arraycopy(buffer, 0, bufferInitSize, 0, getBufferSize());
                buffer = bufferInitSize;
            } else {
                System.arraycopy(buffer, 0, bufferHalfSize, 0, bufferHalfSize.length);
                buffer = bufferHalfSize;
            }
        }

        return true;

    }

    public Email[] reversedBuffer(Email[] emailArray) {
        for (int i = 0; i < emailArray.length / 2; i++) {
            Email temp = emailArray[i];
            emailArray[i] = emailArray[emailArray.length - i - 1];
            emailArray[emailArray.length - i - 1] = temp;
        }
        return emailArray;
    }

    public Email[] getNewest(int n) {
        if (numElements() == 0 || n <= 0) {
            return null;
        }
        //Only the emails in Buffer:
        Email[] tempArray = new Email[numElements()];
        for (int i = 0; i < numElements(); i++) {
            tempArray[i] = buffer[i];
            i++;
        }
        if (n > numElements()) {
            return reversedBuffer(tempArray);
        }
        Email[] newEmails = new Email[n];
        int temp = 0;
        int increment = 1;
        for (temp = 0; temp < n; temp++) {
            newEmails[temp] = buffer[numElements() - increment];
            increment++;
        }
        return newEmails;
    }
}
