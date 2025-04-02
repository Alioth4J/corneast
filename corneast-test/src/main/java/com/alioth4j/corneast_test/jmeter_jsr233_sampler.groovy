import java.net.Socket
import org.apache.commons.io.FileUtils

String host = "127.0.0.1"
int port = 8088

String filePath = "/var/home/alioth4j/Documents/reduce.bin"
File requestFile = new File(filePath)

if (!requestFile.exists()) {
    throw new IllegalStateException("File does not exist: " + requestFile)
}

byte[] requestData = FileUtils.readFileToByteArray(requestFile)

Socket socket = null
try {
    socket = new Socket(host, port)

    socket.withCloseable { sock ->
        def out = sock.getOutputStream()
        out.write(requestData)
        out.flush()

        def in = sock.getInputStream()
        byte[] buffer = new byte[1024]
        int bytesRead = in.read(buffer)
        if (bytesRead > 0) {
            byte[] responseData = buffer[0..<bytesRead]
            String responseHex = responseData.encodeHex().toString()

            vars.put("responseHex", responseHex)
        }
    }
} catch (Exception e) {
    throw e
}
