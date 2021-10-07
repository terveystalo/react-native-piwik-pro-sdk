import { NativeModules } from 'react-native';

type TrackerOptions = Partial<{
  /**
   * Domain part of screen view URL sent to Piwik.
   *
   * Defaults to package name on Android and bundle identifier on iOS.
   * Note that the default differs from Piwik iOS SDK default
   */
  applicationDomain: string;
  /**
   * Interval, in seconds, between automatic delivery of tracking events.
   *
   * See:
   * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#dispatching
   * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#dispatching
   */
  dispatchInterval: number;
  /**
   * If set to false, default URL path prefixes are disabled.
   *
   * See
   * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#tracking-screen-views
   * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#tracking-screen-views
   */
  isPrefixingEnabled: boolean;
}>;

export interface CustomDimensions {
  [index: number]: string;
}

interface CustomDimension {
  index: number;
  value: string;
}

type PiwikProSdkType = {
  init(baseUrl: string, siteId: string, options: TrackerOptions): Promise<void>;
  trackScreen(
    path: string,
    customDimensions?: CustomDimension[]
  ): Promise<void>;
  trackEvent(
    category: string,
    action: string,
    // Optional arguments need to be passed in map
    // since nullable numbers are not supported
    optionalArgs: {
      name?: string;
      value?: number;
    },
    customDimensions?: CustomDimension[]
  ): Promise<void>;
  dispatch(): Promise<void>;
  setUserId(userId?: string): Promise<void>;
  setUserEmail(email?: string): Promise<void>;
  sendApplicationDownload(): Promise<void>;
};

const PiwikProSdk: PiwikProSdkType = NativeModules.PiwikProSdk;

/**
 * Initialize the SDK. Needs to be called before calling tracking functions.
 * The tracker can only be initialized once, subsequent calls will fail.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#configuration
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#configuration
 */
export async function init(
  baseUrl: string,
  siteId: string,
  options: TrackerOptions = {}
): Promise<void> {
  return await PiwikProSdk.init(baseUrl, siteId, options);
}

/**
 * Track a screen view.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#tracking-screen-views
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#tracking-screen-views
 */
export async function trackScreen(
  path: string,
  customDimensions?: CustomDimensions
): Promise<void> {
  const customDimensionsArray = customDimensionsToArray(customDimensions);
  return await PiwikProSdk.trackScreen(path, customDimensionsArray);
}

/**
 * Track a custom event.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#tracking-custom-events
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#tracking-custom-events
 */
export async function trackEvent(
  category: string,
  action: string,
  name?: string,
  value?: number,
  customDimensions?: CustomDimensions
): Promise<void> {
  const customDimensionsArray = customDimensionsToArray(customDimensions);
  return await PiwikProSdk.trackEvent(
    category,
    action,
    {
      name,
      value,
    },
    customDimensionsArray
  );
}

/**
 * Set a custom user ID.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_Android.html#user-id
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_iOS.html#user-id
 */
export async function setUserId(userId?: string): Promise<void> {
  return PiwikProSdk.setUserId(userId);
}

/**
 * Set a custom User email address.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_Android.html#user-email-address
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_iOS.html#user-email-address
 */
export async function setUserEmail(userEmail?: string): Promise<void> {
  return PiwikProSdk.setUserEmail(userEmail);
}

/**
 * Track application installs
 *
 * See:
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_Android.html#tracking-application-installs
 * - https://developers.piwik.pro/en/latest/data_collection/mobile/Piwik_PRO_SDK_for_iOS.html#tracking-application-installs
 */
export async function sendApplicationDownload(): Promise<void> {
  return PiwikProSdk.sendApplicationDownload();
}

/**
 * Dispatch all queued tracking events.
 *
 * See:
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_Android.html#dispatching
 * - https://developers.piwik.pro/en/latest/sdk/Piwik_PRO_SDK_for_iOS.html#dispatching
 */
export const dispatch = PiwikProSdk.dispatch;

const customDimensionsToArray = (
  customDimensions?: CustomDimensions
): CustomDimension[] | undefined => {
  if (!customDimensions) {
    return customDimensions;
  }

  const customDimensionsArray = Object.entries(customDimensions).map(
    ([i, value]) => {
      const index = parseInt(i, 10);

      if (index <= 0) {
        throw new Error('Custom dimension index must be larger than 0');
      }

      return {
        index,
        value,
      };
    }
  );

  return customDimensionsArray;
};
