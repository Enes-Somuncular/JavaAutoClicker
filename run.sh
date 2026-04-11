#!/bin/bash
# ============================================================
# Java AutoClicker — macOS Başlatma Scripti
# ============================================================
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# ── 1. Java kontrolü ────────────────────────────────────────
if ! command -v java &>/dev/null; then
    osascript -e 'display dialog "Java bulunamadı!\n\nhttps://www.oracle.com/java/technologies/downloads/ adresinden Java 17+ indir." buttons {"Tamam"} default button "Tamam" with icon caution'
    exit 1
fi

# ── 2. Mimari otomatik algıla → doğru dylib seç ─────────────
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    NATIVE_LIB="$SCRIPT_DIR/lib/libJNativeHook-2.2.2.arm64.dylib"
else
    NATIVE_LIB="$SCRIPT_DIR/lib/libJNativeHook-2.2.2.x86_64.dylib"
fi

if [ ! -f "$NATIVE_LIB" ]; then
    echo "Uyarı: Native kütüphane bulunamadı: $NATIVE_LIB"
    echo "JNativeHook otomatik yüklemeyi deneyecek..."
fi

# ── 3. Accessibility izni ön kontrol (macOS) ─────────────────
if [ "$(uname)" = "Darwin" ]; then
    # tccutil ya da osascript ile kontrol mümkün değil — uygulama zaten güzel hata gösterecek
    # Ama kullanıcıyı önceden uyaralım (Sistem Tercihleri açık değilse)
    :
fi

# ── 4. Uygulamayı başlat ─────────────────────────────────────
java \
  --enable-native-access=ALL-UNNAMED \
  -Djava.library.path="$SCRIPT_DIR/lib" \
  -Dapple.awt.application.name="Java AutoClicker" \
  -Dapple.laf.useScreenMenuBar=true \
  -cp "$SCRIPT_DIR/src/main/java:$SCRIPT_DIR/lib/jnativehook-2.2.2.jar:$SCRIPT_DIR/lib/flatlaf-3.4.jar" \
  com.autoclicker.Main
