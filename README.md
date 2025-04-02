# Igatha

Igatha is an open-source SOS signaling and recovery app designed for war zones and disaster areas, enabling offline emergency communication when traditional networks fail.

## Status

- **iOS**: [v1.0](https://apps.apple.com/us/app/igatha/id6737691452)
- **Android**: [v1.0](https://play.google.com/store/apps/details?id=com.nizarmah.igatha)

## Quickstart

1. Install the app using the links above.
1. Open the app and grant the necessary permissions.

## How to use Igatha

### Sending SOS signals (distress mode)

#### Manual signaling

1. Open Igatha.
1. Ensure bluetooth is enabled.
1. Tap "_Send SOS_".

#### Automatic signaling

1. Open Igatha.
1. Tap the gear icon (top right).
1. Enable "_Disaster Detection_".

With disaster detection, Igatha will now run in the background, monitoring your device's sensors.
When a potential disaster is detected, you'll receive an "_Are you okay?_" notification:
* If you respond with "_Need help_" or don't respond in 2 minutes, it will automatically broadcast an SOS.
* If you respond with "_I'm okay_", it will ignore the event.

### Helping others (recovery mode)

If you're safe and want to help others:

1. Open Igatha.
1. Ensure bluetooth is enabled (on Android 11 or lower, also enable Location).
1. Check "_People seeking help_".
1. Move towards locations where displayed distances decrease.
1. Listen carefully for audible sirens.

## How Igatha works

### Bluetooth low energy (BLE)

Igatha uses Bluetooth Low Energy (BLE) to:
1. Broadcast SOS signals.
1. Scan for nearby SOS broadcasts.
1. Estimate approximate distance to the signal source based on signal strength.

No internet or GPS is required, preventing signal jamming or manipulation.

### SOS signal composition

The SOS signal combines:
1. BLE advertisement: broadcasts a pseudonymized identifier.
1. Audible siren: generated via device speakers to help responders locate you.

Responders can toggle additional signals, like flashlight or vibration, remotely. (planned feature)

### Disaster detection sensors

Igatha detects disasters using device sensors:
1. Accelerometer: measures sudden motion changes.
1. Gyroscope: detect orientation and rotation shifts.
1. Barometer (if available): detects atmospheric pressure changes, reducing false positives.

Disaster detection triggers when multiple sensors simultaneously detect abrupt changes.

Location permissions are required for "_Disaster Detection_".

## Battery usage

Igatha minimizes battery use by leveraging BLE and optimized sensor monitoring.

The app can continuously broadcast for extended periods during emergencies.

## Limitations

### Early stage

* This is a Minimum Viable Product (MVP) with significant room for improvement
* Testing has been limited to controlled environments
* While not guaranteed to work in all scenarios, it provides a potential lifeline where no alternatives exist

### Signal range

* BLE range: typically 10-30 meters indoors, further outdoors, limited by rubble and building materials.
* Optional extensions: Third-party BLE receivers can extend range significantly.

## Why open source?

Igatha is open-sourced for:

1. **Transparency**: In crisis situations, people need to trust the tools they use. Open source allows anyone to verify the app's security and privacy measures.

2. **Accessibility**: Making the code freely available ensures the technology can be used, studied, and adapted by anyone who needs it.

3. **Community Impact**: War and disaster response tools should be community resources, not commercial products. Open sourcing enables collaborative improvement and adaptation for different crisis scenarios.

## Contributing

Contributions are vital for improving Igatha:
* Testing and bug reports
* Documentation
* Translations
* Feature enhancements
* Code optimization
* Security reviews
* Distribution

To contribute, open an issue or submit a pull request.

## Privacy & security

* Completely offline; no data collection or internet connectivity.
* Uses pseudonymized identifiers for privacy.

## Contact

For questions, suggestions, or feedback, please open an issue in the repository.
