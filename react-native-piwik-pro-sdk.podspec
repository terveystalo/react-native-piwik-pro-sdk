require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-piwik-pro-sdk"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/terveystalo/react-native-piwik-pro-sdk.git", :tag => "#{s.version}" }

  
  s.source_files = "ios/**/*.{h,m,mm}"
  

  s.dependency "React"
  # Cannot upgrade to latest versions
  # See: https://github.com/PiwikPRO/piwik-pro-sdk-framework-ios/issues/2
  s.dependency 'PiwikPROSDK', '1.0.1'
end
