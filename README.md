# 🗺️ React Native Mapbox Navigation

> **Be aware, this project is still in early development and not yet ready to use.**

> Smart Mapbox turn-by-turn routing based on real-time traffic for React Native.

## :star: Features

- Simple installation and usage
- Mutable speech announcements
- Route simulation

## :arrow_forward: Installation

```sh
npm i react-native-mapbox-navigation
cd ios && pod install --repo-update
```

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

## :arrow_forward: Usage

```jsx
<MapboxNavigation origin={[8.0, 46.0]} destination={[8.0, 46.0]} />
```

## :computer: Contributing

Contributions are very welcome. Please check out the [contributing document](CONTRIBUTING.md).

## :bookmark: License

This project is [Apache License 2.0](LICENSE) licensed.
