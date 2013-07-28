package echoes.act8;

public class EchoesMessage {

    byte mark;

    int length;

    byte[] data;

    public EchoesMessage(byte mark_, int length_, byte[] data_)
    {
        mark = mark_;
        length = length_;
        data = data_;
    }

    public byte getMark()
    {
        return mark;
    }

    public void setMark(byte mark)
    {
        this.mark = mark;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] data)
    {
        this.data = data;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("mark=[");
        sb.append(mark);
        sb.append("],len=[");
        sb.append(length);
        sb.append("],data=[");
        for (int i = 0; i < 10 && i < data.length; i++) {
            sb.append(data[i]);
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }
}
