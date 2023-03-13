const net = require('net');
const crypto = require('crypto');

// Simple HTTP server responds with a simple WebSocket client test
const httpServer = net.createServer((connection) => {
  connection.on('data', () => {
    let content = `<!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Hei</title>
    
    </head>
    <body>
        <h2>Send en melding til server via websocket</h2>
        <textarea id="txt" cols="40" rows="5"></textarea>
        
        <button id="send-btn" onclick="sendMessage()">Send melding</button>
    
    
        <script>
            let btn = document.getElementById("send-btn");
            let textarea = document.getElementById("txt");
            var ws = new WebSocket('ws://localhost:3001');
    
            ws.onopen = () => console.log("Connection is open")
            ws.onmessage = event => alert('Message from server: ' + event.data);
    
            
    
            function sendMessage() {
                ws.send(textarea.value);
            }
    
        </script>
    
        <style>
    
            :root {
              font-family: Inter, system-ui, Avenir, Helvetica, Arial, sans-serif;
              line-height: 1.5;
              font-weight: 400;
      
              color-scheme: light dark;
              color: rgba(255, 255, 255, 0.87);
              background-color: #242424;
      
              font-synthesis: none;
              text-rendering: optimizeLegibility;
              -webkit-font-smoothing: antialiased;
              -moz-osx-font-smoothing: grayscale;
              -webkit-text-size-adjust: 100%;
            }
    
    
            body {
                display: flex;
                place-items: top;
                flex-direction: column;
                align-items: center;
                min-width: 320px;
                min-height: 100vh;          
            }
    
            :root {
                width: 500px;
                max-width: 1280px;
                margin: 0 auto;
                padding: 2rem;
                text-align: center;
            }
    
            h1 {
                font-size: 3.2em;
                line-height: 1.1;
            }
    
            button {
                border-radius: 8px;
                border: 1px solid transparent;
                padding: 0.6em 1.2em;
                font-size: 1em;
                font-weight: 500;
                font-family: inherit;
                background-color: #1a1a1a;
                cursor: pointer;
                transition: border-color 0.25s;
                width: 150px;
                margin-top: 10px;
            
            }
            button:hover {
                border-color: #646cff;
            }
            button:focus,
            button:focus-visible {
                outline: 4px auto -webkit-focus-ring-color;
            }
    
            @media (prefers-color-scheme: light) {
            :root {
                color: #213547;
                background-color: #ffffff;
            }
    
            button {
                background-color: #f9f9f9;
            }
            }
    
            textarea {
                resize: none;
            }
  
        </style>
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
// Documentation used: https://www.rfc-editor.org/rfc/rfc6455 
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
          //console.log("KEY: " + key);
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

    let bytes = Buffer.from(data)
    let length = bytes[1] & 127; //passer på at lengden ikke blir lenger enn 127
    let maskStart = 2;
    let dataStart = maskStart + 4;
    let msg = '';
    var myBuffer = [];
  
    for (let i = dataStart; i < dataStart + length; i++) {
      let byte = bytes[i] ^ bytes[maskStart + ((i - dataStart) % 4)];
      myBuffer.push(byte);
      msg += String.fromCharCode(byte);
    } 
    console.log("Message: " + msg);

    connection.write(Buffer.from([0x81, length, ...myBuffer]));
    return
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