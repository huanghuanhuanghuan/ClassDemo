import java.io.*;

public class MainDescriptor {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        File file = new File("src/data/Main.class");
        System.out.println("file path = " + file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[4096];
        fis.read(buf);
//        System.out.println("data = " + new String(buf  ));
        annylize(buf);
    }

    private static void annylize(byte[] buf) {
        BufferHelper helper = new BufferHelper(buf);
        System.out.println("magic = " +  u4To8Char(helper.popU4()));
        System.out.println("minor_version = " + uToInt(helper.popU2()));
        System.out.println("major_version = " + uToInt(helper.popU2()));
        int constant_pool_count = uToInt(helper.popU2());
        System.out.println("constant_pool_count = " + constant_pool_count);
        for (int i = 0; i < constant_pool_count; i++) {
            System.out.println("constant = " + helper.popCpInfo());
        }
    }

    private static String uToString(byte[] buf, int count) {
        if (buf == null || buf.length != count) {
            return "";
        }
        char[] strs = new char[count];
        for (int i = 0; i < count; i ++) {
            strs[i] = (char) buf[i];
        }
        return new String(strs);
    }

    private static int uToInt(byte[] buf) {
        if (buf == null || buf.length > 4) {
            return 0;
        }
        int value = 0;
        for (byte b : buf) {
            value = value * 256 + b;
        }
        return value;
    }

    private static String u4To8Char(byte[] buf) {
        if (buf == null || buf.length != 4) {
            return "";
        }
        char[] strs = new char[8];
        for (int i = 0; i < 4; i ++) {
            String high = Integer.toHexString((buf[i] & 0b11110000) >>> 4 & 0b00001111);
            String low = Integer.toHexString(buf[i] & 0b00001111);
            strs[2 * i] =  high.charAt(0);
            strs[2 * i + 1] = low.charAt(0);
        }
        return new String(strs);
    }

    static class BufferHelper {
        private byte[] mBuf;
        private int index = 0;
        public BufferHelper(byte[] buf) {
            mBuf = buf;
        }

        private byte popU1() {
            byte tmp = mBuf[index];
            index += 1;
            return tmp;
        }

        private byte[] popU2() {
            byte[] tmp = new byte[2];
            tmp[0] = mBuf[index];
            tmp[1] = mBuf[index + 1];
            index += 2;
            return tmp;
        }

        private byte[] popU4() {
            byte[] tmp = new byte[4];
            tmp[0] = mBuf[index];
            tmp[1] = mBuf[index + 1];
            tmp[2] = mBuf[index + 2];
            tmp[3] = mBuf[index + 3];
            index += 4;
            return tmp;
        }

        private CpInfo popCpInfo() {
            CpInfo cpInfo = new CpInfo();
            cpInfo.tag = popU1();
            byte[] buf = new byte[2048];
            int index = 0;
            byte b;
            while ((b = popU1()) != '\0') {
                buf[index] = b;
                index ++;
            }
            cpInfo.info = new String(buf);
            return cpInfo;
        }
    }

    static class CpInfo {
        byte tag; // 常量池类型
        String info; // 常量内容

        @Override
        public String toString() {
            return "CpInfo{" +
                    "tag=" + tag +
                    ", info='" + info + '\'' +
                    '}';
        }
    }
}
