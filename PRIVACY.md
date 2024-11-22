# Privacy Policy

**Last Updated:** November 1, 2024

## Introduction

**Igatha** is committed to protecting your privacy. This Privacy Policy outlines how the app functions and the measures we take to ensure your information remains secure.

## How Igatha Works

### 1. Sensor Data
- **Accelerometer Readings:** Used locally for detecting sudden movements to identify potential emergencies.
- **Gyroscope Data:** Utilized locally to determine device orientation, aiding in emergency detection.
- **Barometer Readings:** Monitors pressure changes locally to assist in disaster detection.
- **Data Handling:** All sensor data is processed locally on your device and immediately discarded. No data is transmitted off your device.

### 2. Bluetooth
- **Bluetooth Low Energy (BLE) Signals:** Used locally for broadcasting SOS beacons and scanning for nearby signals.
- **Signal Strength:** Assessed locally to estimate the distance of detected signals.
- **Temporary Device Identifiers:** Uses the first 8 characters of UUIDs for temporary identification purposes.
- **Data Handling:** No persistent storage of detected devices. All Bluetooth interactions are handled locally without data retention or transmission.

### 3. Location Services
- **Background Location Permission:** Required to enable sensor monitoring for automated disaster detection on iOS.
- **Bluetooth Scanning Permission:** Required to enable bluetooth scanning for nearby devices on Android 11  and lower.
- **Data Handling:** We do **not** transmit or store GPS coordinates or perform location tracking. Location permissions are solely used to facilitate sensor-based emergency detection locally on your device.

### 4. Foreground Services
- **Background Permission:** Required to enable sensor monitoring for automated disaster detection and signaling SOS in the background on Android.

## Data Privacy

- **Local Processing:** All data processing occurs on your device. No data is transmitted over the internet.
- **No Data Collection:** Data is not transmitted off your device or persisted between app sessions. Once processed, it is immediately discarded.
- **No Third-Party Integration:** Igatha does not use any analytics tools, tracking mechanisms, or third-party SDKs.
- **No Cloud Services:** There is no cloud integration or external servers involved.

## Permissions Used

- **Bluetooth:** Enables local SOS beacon transmission and detection.
- **Location:** Required for background sensor operations to facilitate automated emergency detection on iOS. Required for bluetooth scanning on Android 11 and lower.
- **Motion/Sensors:** Access to device motion and sensors for local disaster detection.
- **Foreground Services:** Required to enable sensor monitoring for automated disaster detection and signaling SOS in the background on Android.

## Open Source Commitment

Igatha is open-sourced under the [GNU General Public License v3.0](https://github.com/nizarmah/igatha/blob/main/LICENSE). You can review, modify, and contribute to the source code at [github.com/nizarmah/igatha](https://github.com/nizarmah/igatha).

## Contact Us

If you have any questions or concerns about this Privacy Policy, please contact us at [nizarmah@hotmail.com](mailto:nizarmah@hotmail.com).
