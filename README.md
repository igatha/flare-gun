# Igatha

Igatha is an open-source SOS signaling and recovery app designed for war zones and disaster areas, enabling offline emergency communication when traditional networks fail.

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

## Status

- **iOS**: Version 1.0 pending App Store approval
- **Android**: Under active development

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
