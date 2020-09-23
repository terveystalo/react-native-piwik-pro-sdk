import { NativeModules } from 'react-native';

type PiwikProSdkType = {
  multiply(a: number, b: number): Promise<number>;
};

const { PiwikProSdk } = NativeModules;

export default PiwikProSdk as PiwikProSdkType;
