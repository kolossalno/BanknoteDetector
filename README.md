# Banknote Detector (Детектор купюр на Android) 💵📱

Android-приложение для детекции и распознавания номиналов купюр в режиме реального времени с использованием камеры смартфона и нейросети **YOLOv8** на базе **TensorFlow Lite**.

---

## 🚀 Особенности

- ⚡ **Детекция в реальном времени**: обработка видеопотока с камеры с минимальной задержкой через **CameraX**.
- 🧠 **Нейросеть YOLOv8 TFLite**: запуск оптимизированной модели `.tflite` с поддержкой тензоров NCHW.
- 🎯 **Стилизованный видоискатель**:
  - Четкие визирные уголки вокруг распознанной купюры.
  - Информативная темная плашка с номиналом (например, `1000 ГМИ`, `5 ГМИ`) и процентом уверенности модели (`98%`).
  - Цветовой статус-индикатор детекции.
- ⚙️ **Гибкое аппаратное ускорение**:
  - Поддержка выполнения на **GPU** (GPU Delegate / OpenCL).
  - Многопоточный режим инференса на **CPU** (4 потока).
- 📊 **Телеметрия**: отображение времени инференса (Inference Time) в миллисекундах.

---

## 🛠 Стек технологий

- **Язык**: Kotlin
- **Архитектура**: Single Activity, ViewBinding
- **Камера**: Android Jetpack CameraX (1.4.0)
- **ML / AI**: TensorFlow Lite 2.16.1, TensorFlow Lite Support, GPU Delegate
- **Модель**: YOLOv8 (экспорт в `.tflite`, размер входа `416x416`, NCHW)
- **Минимальная версия Android**: Android 8.0 (API level 26)
- **Целевая версия Android**: Android 14 (API level 34)
- **Java/Kotlin Target**: JDK 17

---

## 📂 Структура проекта

```text
BanknoteDetector/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   ├── best.tflite          # Модель детектора банкнот YOLOv8 TFLite
│   │   │   └── labels.txt           # Список классов (номиналов)
│   │   ├── java/com/surendramaran/yolov8tflite/
│   │   │   ├── MainActivity.kt      # Логика камеры, lifecycle и управление UI
│   │   │   ├── Detector.kt          # Подготовка входных тензоров, инференс и NMS
│   │   │   ├── OverlayView.kt       # Отрисовка рамок и бейджей с номиналом
│   │   │   ├── BoundingBox.kt       # Data class координат и класса объекта
│   │   │   └── Constants.kt         # Пути к файлам модели и меток
│   │   ├── res/                     # Разметка, стили и ресурсы приложения
│   │   └── AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 📲 Установка и запуск

### Предварительные требования:
1. **Android Studio** (версия Hedgehog 2023.1.1 или новее).
2. **JDK 17** (рекомендуется Temurin 17 или встроенный в Android Studio JBR-17).
3. Физическое устройство Android с поддержкой Camera2 API (на эмуляторе камера не всегда передает корректный видеопоток для детекции).

### Инструкция по сборке:
1. Клонируйте репозиторий:
   ```bash
   git clone <URL_ВАШЕГО_РЕПОЗИТОРИЯ>
   cd BanknoteDetector
   ```
2. Откройте проект в **Android Studio** (`File -> Open... -> папка BanknoteDetector`).
3. Дождитесь завершения синхронизации Gradle.
4. Подключите Android-смартфон по USB (с включенной отладкой по USB).
5. Нажмите **Run ▶** (Shift + F10) для сборки и установки APK.
6. При первом запуске предоставьте разрешение на использование камеры.

---

## 🔄 Использование собственной модели

Если вы хотите переобучить модель на другие купюры или объекты:
1. Экспортируйте модель YOLOv8 в формат TFLite:
   ```python
   from ultralytics import YOLO

   model = YOLO("your_model.pt")
   model.export(format="tflite", imgsz=416)
   ```
2. Поместите сгенерированный файл `*.tflite` в папку `app/src/main/assets/`.
3. Создайте/обновите файл `labels.txt` с названиями классов (по одному классу на строку).
4. При необходимости обновите имена файлов в [`Constants.kt`](app/src/main/java/com/surendramaran/yolov8tflite/Constants.kt):
   ```kotlin
   object Constants {
       const val MODEL_PATH = "best.tflite"
       const val LABELS_PATH = "labels.txt"
   }
   ```
5. В [`OverlayView.kt`](app/src/main/java/com/surendramaran/yolov8tflite/OverlayView.kt) настройте отображаемые названия в блоке `when (it.clsName)`.

---

## 📄 Лицензия

Распространяется под свободной лицензией. Основано на открытых реализациях YOLOv8 Object Detection для Android TFLite.
