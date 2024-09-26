import 'dart:io';

import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class FileProcessor {
  FileProcessor._internal();

  static final FileProcessor _instance = FileProcessor._internal();

  factory FileProcessor() => _instance;

  Future<String?> saveAssetsToFile(String assetName, String filename) async {
    final dir = await getApplicationCacheDirectory();
    final file = File('${dir.path}/$filename');

    try {
      if (await file.exists()) {
        // 删除文件
        await file.delete();
      }

      // 创建文件
      await file.create();

      final bytes = await rootBundle.load(assetName);
      final data = bytes.buffer.asUint8List();

      // 写入文件
      await file.writeAsBytes(data);

      return file.path;
    } catch(e) {
      // Error Catch
    }

    return null;
  }
}