#!/bin/bash
set -e

PROJECT_DIR="/data/user/0/com.agy/files/.gemini/antigravity-app/scratch/kalkulator-android"
SDK_JAR="/data/user/0/com.agy/files/android-sdk/android.jar"
BUILD_DIR="$PROJECT_DIR/app/build"
SRC_DIR="$PROJECT_DIR/app/src/main"

echo "=== [1/6] Membersihkan direktori build ==="
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/gen" "$BUILD_DIR/obj" "$BUILD_DIR/bin"

echo "=== [2/6] Memproses resource dengan aapt ==="
aapt package -f -m \
    -J "$BUILD_DIR/gen" \
    -M "$SRC_DIR/AndroidManifest.xml" \
    -S "$SRC_DIR/res" \
    -I "$SDK_JAR"

echo "=== [3/6] Mengompilasi Java bytecode (javac) ==="
javac -source 1.8 -target 1.8 \
    -cp "$SDK_JAR" \
    -d "$BUILD_DIR/obj" \
    "$BUILD_DIR/gen/com/example/kalkulator/R.java" \
    "$SRC_DIR/java/com/example/kalkulator/MainActivity.java"

echo "=== [4/6] Mengonversi ke Dalvik Executable (d8) ==="
d8 --output "$BUILD_DIR/bin/" \
   --min-api 24 \
   --lib "$SDK_JAR" \
   "$BUILD_DIR"/obj/com/example/kalkulator/*.class

echo "=== [5/6] Mengemas APK dengan aapt ==="
aapt package -f \
    -M "$SRC_DIR/AndroidManifest.xml" \
    -S "$SRC_DIR/res" \
    -I "$SDK_JAR" \
    -F "$BUILD_DIR/bin/kalkulator.unsigned.apk"

cd "$BUILD_DIR/bin"
aapt add kalkulator.unsigned.apk classes.dex

echo "=== [6/6] Menandatangani APK (apksigner) ==="
KEYSTORE="$PROJECT_DIR/debug.keystore"
if [ ! -f "$KEYSTORE" ]; then
    keytool -genkeypair -v -keystore "$KEYSTORE" \
        -storepass android -alias androiddebugkey -keypass android \
        -keyalg RSA -keysize 2048 -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US"
fi

apksigner sign --ks "$KEYSTORE" \
    --ks-pass pass:android \
    --out "$BUILD_DIR/bin/kalkulator.apk" \
    "$BUILD_DIR/bin/kalkulator.unsigned.apk"

echo "=== Verifikasi APK ==="
apksigner verify "$BUILD_DIR/bin/kalkulator.apk"

echo "SUKSES! APK berhasil dibuat di:"
echo "$BUILD_DIR/bin/kalkulator.apk"
ls -lh "$BUILD_DIR/bin/kalkulator.apk"
