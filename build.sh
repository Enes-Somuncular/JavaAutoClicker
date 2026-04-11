#!/bin/bash
# ============================================================
# Java AutoClicker — macOS/Linux Build Scripti
# ============================================================
set -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LIB_DIR="$SCRIPT_DIR/lib"
SRC_DIR="$SCRIPT_DIR/src/main/java"

mkdir -p "$LIB_DIR"

# ── 1. JAR'ları indir ────────────────────────────────────────
if [ ! -f "$LIB_DIR/jnativehook-2.2.2.jar" ]; then
    echo "jnativehook indiriliyor..."
    curl -L -o "$LIB_DIR/jnativehook-2.2.2.jar" \
      "https://repo1.maven.org/maven2/com/github/kwhat/jnativehook/2.2.2/jnativehook-2.2.2.jar"
fi

if [ ! -f "$LIB_DIR/flatlaf-3.4.jar" ]; then
    echo "flatlaf indiriliyor..."
    curl -L -o "$LIB_DIR/flatlaf-3.4.jar" \
      "https://repo1.maven.org/maven2/com/formdev/flatlaf/3.4/flatlaf-3.4.jar"
fi

# ── 2. macOS native kütüphanesi (mimari otomatik) ─────────────
if [ "$(uname)" = "Darwin" ]; then
    ARCH=$(uname -m)
    if [ "$ARCH" = "arm64" ]; then
        DYLIB="libJNativeHook-2.2.2.arm64.dylib"
        DYLIB_URL="https://github.com/kwhat/jnativehook/releases/download/2.2.2/libJNativeHook-2.2.2-osx-aarch64.dylib"
    else
        DYLIB="libJNativeHook-2.2.2.x86_64.dylib"
        DYLIB_URL="https://github.com/kwhat/jnativehook/releases/download/2.2.2/libJNativeHook-2.2.2-osx-x86_64.dylib"
    fi

    if [ ! -f "$LIB_DIR/$DYLIB" ]; then
        echo "macOS native kütüphane indiriliyor ($ARCH)..."
        curl -L -o "$LIB_DIR/$DYLIB" "$DYLIB_URL" 2>/dev/null || echo "Uyarı: Native dylib indirilemedi, mevcut dosya kullanılacak."
    fi
fi

# ── 3. Derleme ────────────────────────────────────────────────
echo "Derleniyor..."
javac \
  -cp "$LIB_DIR/jnativehook-2.2.2.jar:$LIB_DIR/flatlaf-3.4.jar" \
  "$SRC_DIR/com/autoclicker/"*.java

echo ""
echo "✅ Derleme tamamlandı! Çalıştırmak için:"
echo "   bash run.sh"
