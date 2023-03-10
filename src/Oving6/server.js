const net = require('net');
const crypto = require('crypto');

// Simple HTTP server responds with a simple WebSocket client test
const httpServer = net.createServer((connection) => {
  connection.on('data', () => {
    let content = `<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
  </head>
  <body>
    WebSocket test page
    <script>
      let ws = new WebSocket('ws://localhost:3001');
      ws.onmessage = event => alert('Message from server: ' + event.data);
      ws.onopen = () => ws.send('hello');
    </script>
  </body>
</html>
`;
    connection.write('HTTP/1.1 200 OK\r\nContent-Length: ' + content.length + '\r\n\r\n' + content);
  });
});

httpServer.listen(3000, () => {
  console.log('HTTP server listening on port 3000');
});

// WebSocket server
const wsServer = net.createServer((connection) => {
  let isHandshake = true
  console.log('Client connected');
  
  connection.on('data', (data) => {
    if (isHandshake) {
      let key;
      const arr = data.toString().split("\n");
      arr.forEach((line) => {
        if (line.includes("Sec-WebSocket-Key")) {
          key = line.split(" ")[1].trim();
          console.log("KEY: " + key);
        }

      })

      var shasum = crypto.createHash('sha1')
      combined = key+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
      shasum.update(combined)
      encoded = shasum.digest('base64')

      connection.write("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + encoded + "\r\n\r\n")
      console.log("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: " + encoded + "\r\n")

      isHandshake = false
      return
    } 

    //close frame from client
    if (data[0] === 0x8) {
      console.log("Close message received from client");
      connection.end()
      return
  }


    console.log("👍👍👍")
    let bytes = Buffer.from(data)
    let length = bytes[1] & 127;
    let maskStart = 2;
    let dataStart = maskStart + 4;
    let msg = '';
  
    for (let i = dataStart; i < dataStart + length; i++) {
      let byte = bytes[i] ^ bytes[maskStart + ((i - dataStart) % 4)];
      //console.log(String.fromCharCode(byte));
      msg += String.fromCharCode(byte);
    } 
    console.log("Message: " + msg);
    connection.write(msg);
    return

  });

  connection.on('end', () => {
    console.log('Client disconnected');
  });

});

wsServer.on('error', (error) => {
  console.error('Error: ', error);
});

wsServer.listen(3005, () => {
  console.log('WebSocket server listening on port 3001');
});