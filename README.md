# react-native-serial-port

Only for android platform based on [Android-SerialPort-API](https://github.com/licheedev/Android-SerialPort-API).

It is available in AndroidX

## Getting started

`$ npm install react-native-serial-port --save`

### Mostly automatic installation

`$ npx react-native link react-native-serial-port`

## Usage

```javascript
import SerialPortAPI from 'react-native-serial-port';

async function example() {
  const serialPort = await SerialPortAPI.open("/dev/ttyS4", { baudRate: 38400 });

  // subscribe received data
  const sub = serialPort.onReceived(buff => {
    console.log(buff.toString('hex').toUpperCase());
  })

  // unsubscribe
  // sub.remove();

  // send data with hex format
  await serialPort.send('00FF');

  // close
  serialPort.close();
}
```
