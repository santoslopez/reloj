public class FileFragment {
    private byte[] data;
    private int position;

    public FileFragment(byte[] data, int position) {
        this.data = data;
        this.position = position;
    }

    public byte[] getData() {
        return data;
    }

    public int getPosition() {
        return position;
    }
}
