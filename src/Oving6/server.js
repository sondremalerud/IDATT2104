const net = require('net');
var crypto = require('crypto');

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

// Incomplete WebSocket server
const wsServer = net.createServer((connection) => {
  console.log('Client connected');
  var isHandshake = true

  connection.on('data', (data) => {
    var key = ""
    //TODO: finn key fra klient, og bruk sha + base64endoding (done(?))

    if (isHandshake) {

      const arr = data.toString().split("\n")
      for (const line in arr) {
        if (line.includes("Sec-WebSocket-Key")) {
          key = line.split(" ")[1];
        }        
      }
      var shasum = crypto.createHash('sha1')
      combined = key+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
      shasum.update(combined)
      encoded = shasum.digest('base64')

      connection.write("HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade\nSec-WebSocket-Accept: " + encoded)
      console.log("HTTP/1.1 101 Switching Protocols\nUpgrade: websocket\nConnection: Upgrade\nSec-WebSocket-Accept: " + encoded)
      isHandshake = false
    } 

    console.log('Data received from client: ', data.toString());

    // TODO: tolk dataen og console log det dekrypterte





  });

  connection.on('end', () => {
    console.log('Client disconnected');
  });

});

wsServer.on('error', (error) => {
  console.error('Error: ', error);
});

wsServer.listen(3001, () => {
  console.log('WebSocket server listening on port 3001');
});



let bytes = Buffer.from([0x81, 0x83, 0xb4, 0xb5, 0x03, 0x2a, 0xdc, 0xd0, 0x6a]);
let length = bytes[1] & 127;
let maskStart = 2;
let dataStart = maskStart + 4;
for (let i = dataStart; i < dataStart + length; i++) {
  let byte = bytes[i] ^ bytes[maskStart + ((i - dataStart) % 4)];
  console.log(String.fromCharCode(byte));
}