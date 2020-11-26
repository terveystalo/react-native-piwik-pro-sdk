import { TrackerOptions, CustomDimensions } from './src';

declare module 'react-native-piwik-pro-sdk' {
  namespace PiwikProSdk {
    export function init(
      baseUrl: string,
      siteId: string,
      options?: TrackerOptions
    ): Promise<void>;
    export function trackScreen(
      path: string,
      customDimensions?: CustomDimensions
    ): Promise<void>;
    export function trackEvent(
      category: string,
      action: string,
      name?: string,
      value?: number,
      customDimensions?: CustomDimensions
    ): Promise<void>;
    export function dispatch(): Promise<void>;
  }

  export default PiwikProSdk;
}
