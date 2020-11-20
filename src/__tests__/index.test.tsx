jest.mock('react-native', () => ({
  NativeModules: {
    PiwikProSdk: {
      init: jest.fn(),
      trackScreen: jest.fn(),
      trackEvent: jest.fn(),
      setCustomDimension: jest.fn(),
      dispatch: jest.fn(),
    },
  },
}));

import { NativeModules } from 'react-native';

import * as PiwikProSdk from '../';
import { CustomDimensionScope } from '../';

describe('react-native-piwik-pro-sdk', () => {
  describe('init', () => {
    it('adds missing parameters', async () => {
      await PiwikProSdk.init('baseUrl', 'siteId');

      expect(NativeModules.PiwikProSdk.init).toHaveBeenCalledWith(
        'baseUrl',
        'siteId',
        {}
      );
    });
  });

  describe('trackEvent', () => {
    it('adds missing parameters', async () => {
      await PiwikProSdk.trackEvent('category', 'action');

      expect(NativeModules.PiwikProSdk.trackEvent).toHaveBeenCalledWith(
        'category',
        'action',
        {
          name: undefined,
          value: undefined,
        }
      );
    });
  });

  describe('setCustomDimension', () => {
    it('Set custom dimension', async () => {
      await PiwikProSdk.setCustomDimension(
        0,
        'test',
        CustomDimensionScope.visit
      );

      expect(NativeModules.PiwikProSdk.setCustomDimension).toHaveBeenCalledWith(
        0,
        'test'
      );
    });
  });
});
