# Kalkulator Android Native (Java/Kotlin + XML)

Aplikasi kalkulator native Android sederhana tanpa WebView, berukuran sangat ringan (~21 KB) dan hemat memori, dibuat dengan antarmuka Material modern (*dark sleek theme*).

## Fitur Utama

- **100% Native Android**: Tanpa WebView, dibangun menggunakan native Android Views dan Shape Ripple Drawables.
- **Operasi Aritmatika**: Penjumlahan (`+`), Pengurangan (`−`), Perkalian (`×`), Pembagian (`÷`), dan Persentase (`%`).
- **Fitur Tambahan**:
  - Tombol Plus/Minus (`±`) untuk mengubah tanda bilangan positif/negatif.
  - Tombol Backspace (`⌫`) untuk menghapus angka terakhir.
  - Tombol Clear (`C`) untuk mereset seluruh kalkulasi.
  - Evaluasi ekspresi otomatis yang mematuhi urutan operasi (KABATAKU: perkalian & pembagian dieksekusi lebih dahulu).
  - Penanganan pembagian dengan nol (*error safety*).
  - Format angka presisi tinggi tanpa trailing zero.

## Struktur Proyek

```
kalkulator-android/
├── build_apk.sh               # Script build otomatis on-device (aapt, ecj/javac, d8, apksigner)
├── debug.keystore             # Keystore debug untuk penandatanganan APK
├── app/
│   ├── build/bin/
│   │   └── kalkulator.apk     # APK hasil build (siap install)
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/kalkulator/
│           │   ├── MainActivity.java
│           │   └── MainActivity.kt
│           └── res/
│               ├── drawable/
│               │   ├── bg_btn_function.xml
│               │   ├── bg_btn_number.xml
│               │   └── bg_btn_operator.xml
│               ├── layout/
│               │   └── activity_main.xml
│               └── values/
│                   ├── colors.xml
│                   ├── strings.xml
│                   └── themes.xml
├── build.gradle.kts
└── settings.gradle.kts
```

## Cara Build Ulang APK

Jalankan script build langsung di terminal Android:
```bash
bash build_apk.sh
```

Hasil APK akan otomatis disimpan di:
- `app/build/bin/kalkulator.apk`
- Serta dapat disalin ke `/storage/emulated/0/Download/kalkulator.apk` untuk kemudahan instalasi di smartphone.
