import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import * as PiwikProSdk from 'react-native-piwik-pro-sdk';

export default function App() {
  const [result, setResult] = React.useState<{ type: string; error?: Error }>({
    type: 'Loading',
  });
  React.useEffect(() => {
    (async () => {
      // Initialize the SDK
      await PiwikProSdk.init(
        'https://demoaccess.piwik.pro/',
        '3e7e6ab9-d605-42b0-ac1b-cdf5bb5e216f'
      );
      // Track screen view
      await PiwikProSdk.trackScreen('main/list');
      // Track custom event
      await PiwikProSdk.trackEvent('app', 'launch', 'notification', 1.04);
      // Immediately dispatch all events
      await PiwikProSdk.dispatch();
    })().then(
      () => setResult({ type: 'Success' }),
      (error) => setResult({ type: 'Error', error })
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text>Sending test events</Text>
      <Text>Status: {result.type}</Text>
      {result.error && <Text>{result.error.message}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
