import * as React from 'react';
import { StyleSheet, View, Text, Button } from 'react-native';
import * as PiwikProSdk from 'react-native-piwik-pro-sdk';

export default function App() {
  const [result, setResult] = React.useState<{ type: string; error?: Error }>({
    type: 'Loading',
  });
  const [initialized, setInitialized] = React.useState(false);

  const sendTestEvents = React.useCallback(async () => {
    const firstCustomDimension: PiwikProSdk.CustomDimension = {
      index: 1,
      value: 'first',
    };

    const secondCustomDimension: PiwikProSdk.CustomDimension = {
      index: 2,
      value: 'second',
    };

    // Track screen view with custom dimension
    await PiwikProSdk.trackScreen('main/list', [firstCustomDimension]);
    // Track custom event with a custom dimension
    await PiwikProSdk.trackEvent('app', 'launch', 'notification', 1.04, [
      firstCustomDimension,
    ]);
    // Track custom event with multiple custom dimensions
    await PiwikProSdk.trackEvent('app', 'test1', undefined, undefined, [
      firstCustomDimension,
      secondCustomDimension,
    ]);
    // Track custom event withouth custom dimensions
    await PiwikProSdk.trackEvent('app', 'test3');
    // Immediately dispatch all events
    await PiwikProSdk.dispatch();
  }, []);

  React.useEffect(() => {
    (async () => {
      if (initialized) {
        return;
      }
      // Initialize the SDK
      await PiwikProSdk.init(
        'https://demoaccess.piwik.pro/',
        '3e7e6ab9-d605-42b0-ac1b-cdf5bb5e216f'
      );
      setInitialized(true);
    })().then(
      () => setResult({ type: 'Success' }),
      (error) => setResult({ type: 'Error', error })
    );
  }, [initialized]);

  return (
    <View style={styles.container}>
      <Text>Status: {result.type}</Text>
      {result.error && <Text>{result.error.message}</Text>}
      <Button title="Send test events" onPress={sendTestEvents} />
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
