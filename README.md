# Igatha

Igatha is an open-source SOS signaling and recovery app designed for war zones and disaster areas, enabling offline emergency communication when traditional networks fail.

## Status

- **iOS**: [v1.0](https://apps.apple.com/us/app/igatha/id6737691452)
- **Android**: [v1.0](https://play.google.com/store/apps/details?id=com.nizarmah.igatha)

## Usage

### Signaling

#### Manual

1. Open the app
1. Grant permissions
1. Enable bluetooth
1. Click "Send SOS"

#### Automatic

1. Open the app
1. Grant permissions
1. Enable bluetooth
1. Click the gear icon in the app's top right corner
1. Enable "Disaster Detection"

The app will now run in the background to detect disasters.
If it does, you'll be notified with an "Are you okay?" notification.

If you respond with "Need help" or don't respond in 2 minutes, it will signal SOS.
If you respond with "I'm okay", it will ignore the disaster.

### Recovery

1. Open the app
1. Grant permissions
1. Enable bluetooth
1. If you're on Android 11 or lower, enable location
1. Check "People seeking help"
1. Walk in the direction where the distance gets lower
1. Stay attentive for any siren playing

## How It Works

Igatha uses Bluetooth Low Energy (BLE) technology to:
1. Broadcast SOS signals
2. Scan for nearby signals
3. Estimate approximate distance

The app works completely offline to prevent manipulation and uses BLE instead of GPS to avoid signal jamming. Location permissions are required to enable background sensor monitoring for automated disaster detection.

## Important Notes

- This is a Minimum Viable Product (MVP) with significant room for improvement
- Testing has been limited to controlled environments
- While not guaranteed to work in all scenarios, it provides a potential lifeline where no alternatives exist

## Why Open Source?

Igatha is open-sourced under the [GNU General Public License v3.0](LICENSE) because:

1. **Transparency**: In crisis situations, people need to trust the tools they use. Open source allows anyone to verify the app's security and privacy measures.

2. **Accessibility**: Making the code freely available ensures the technology can be used, studied, and adapted by anyone who needs it.

3. **Community Impact**: War and disaster response tools should be community resources, not commercial products. Open sourcing enables collaborative improvement and adaptation for different crisis scenarios.

## Contributing

We welcome contributions to improve Igatha's effectiveness:

- Testing and bug reports
- Documentation
- Translations
- Feature enhancements
- Code optimization
- Security reviews

To contribute, open an issue or submit a pull request.

## Privacy & Security

- No data collection
- Offline-only operation
- Pseudonymized identifiers
- No internet connectivity required

## Contact

For questions or suggestions, please open an issue in the repository.
