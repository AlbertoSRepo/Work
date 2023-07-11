import JavaEchoServers.EchoServerLine;

public class Run {
    public static void main(String[] args) {
        try {
            EchoServerLine echoServer = new EchoServerLine(8080);
            echoServer.start();

            // Here you can run the program that you wanted to start
            ProcessBuilder pb = new ProcessBuilder("cmd", "/C", "start", "C:\\Users\\Alberto.Scandurra\\Desktop\\VicatNET.lnk");
            Process p = pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
