/*
 * Corneast
 * Copyright (C) 2025 Alioth Null
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.net.Socket
import org.apache.commons.io.FileUtils

String host = "127.0.0.1"
int port = 8088

// Change this!
// Place the binary file into a jmeter-readable place.
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
