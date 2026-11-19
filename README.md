# Kalkulator Android Native (Java + XML)

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
- **Otomatisasi CI/CD (GitHub Actions)**: Setiap kali Anda melakukan `git push` ke branch `main`, GitHub akan otomatis mengompilasi APK dan menyediakannya sebagai artefak unduhan.

## Struktur Proyek

```
kalkulator-android/
├── .github/workflows/
│   └── android-build.yml      # CI/CD: Otomatis build APK saat push ke GitHub
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── gradlew                    # Gradle wrapper script
├── build_apk.sh               # Script build mandiri on-device (aapt, javac, d8, apksigner)
├── debug.keystore             # Keystore debug untuk penandatanganan APK
├── app/
│   ├── build/bin/
│   │   └── kalkulator.apk     # APK hasil build lokal
│   ├── build.gradle.kts
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/example/kalkulator/
│           │   └── MainActivity.java
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

## Cara Kerja CI/CD GitHub Actions

1. Lakukan perubahan pada kode aplikasi.
2. Commit dan push ke GitHub:
   ```bash
   git add .
   git commit -m "Update fitur"
   git push
   ```
3. Buka tab **Actions** di repositori GitHub Anda.
4. Tunggu workflow **Build Android APK** selesai (~1-2 menit).
5. Unduh file `kalkulator-debug-apk.zip` pada bagian **Artifacts** di halaman detail run.

## Cara Build Lokal On-Device

Jalankan script build langsung di terminal Android:
```bash
bash build_apk.sh
```
File APK akan otomatis ter-generate di `app/build/bin/kalkulator.apk`.
