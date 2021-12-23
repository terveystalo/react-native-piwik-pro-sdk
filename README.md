# react-native-piwik-pro-sdk

React Native wrapper for the native [Piwik PRO SDKs](https://developers.piwik.pro/en/latest/sdk/index.html).

## Installation

```sh
npm install react-native-piwik-pro-sdk
```

## Usage

```js
import * as PiwikProSdk from 'react-native-piwik-pro-sdk';

// Initialize tracker
await PiwikProSdk.init(
  'https://demoaccess.piwik.pro/',
  '3e7e6ab9-d605-42b0-ac1b-cdf5bb5e216f'
);

// Track screen
await PiwikProSdk.trackScreen(
  'main/list', // Path
  {
    1: 'first',
  } // Custom dimensions { [index: number]: value: string } (optional)
);

// Track event
await PiwikProSdk.trackEvent(
  'app', // Category
  'launch', // Action
  'notification', // Name (optional)
  1.04, // Value (optional)
  {
    1: 'first',
    2: 'second',
  } // Custom dimensions { [index: number]: value: string } (optional)
);

// Track campaign
await PiwikProSdk.trackCampaign(
  'https://www.example.com/?pk_campaign=example_campaign&pk_keyword=example_keyword&pk_cid=1'
);
```

For more details, see the code in [`src/index.tsx`](src/index.tsx).

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
