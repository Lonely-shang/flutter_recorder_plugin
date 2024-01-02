import 'flutter_recorder_platform_interface.dart';

class FlutterRecorder {
  Future<String?> getPlatformVersion() {
    return FlutterRecorderPlatform.instance.getPlatformVersion();
  }

  Future<String?> startRecord() {
    return FlutterRecorderPlatform.instance.startRecord();
  }

  Future<String?> stopRecord() {
    return FlutterRecorderPlatform.instance.stopRecord();
  }
}
