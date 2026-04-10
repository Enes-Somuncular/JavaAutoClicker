<h1 align="center">🤖 Mükemmel Java AutoClicker & Makro Düzenleyici (AI Yapımı)</h1>

<p align="center">
  <em>Uygulama baştan aşağıya, mimari kararlarından grafik arayüzüne kadar tamamen detaylarına kadar <strong>Yapay Zeka (Antigravity AI)</strong> tarafından kodlanmış ve geliştirilmiştir.</em>
</p>

---

## 🌟 Neden Bu Uygulama?

Piyasadaki klasik makro programlarının aksine; bu proje **sadece kayıt alıp oynatmaz**. Profesyonel kullanıcılar, oyuncular ve geliştiriciler için **"Görsel Kurucu (Visual Builder)"** sunar. 

Tıpkı legoları birleştirir gibi makronuzu sıfırdan yaratabilir, sürükle-bırak ile olayların yerini değiştirebilir, anlık farenin nerede olduğunu dert etmeden **"Teleport"** ya da **"Kayarak Git"** seçeneklerini kullanabilirsiniz!

### ✨ Öne Çıkan Özellikler

- **🤖 Tamamen AI Tarafından Kodlandı:** Kusursuz temiz kod mimarisi, thread yönetimi ve arayüz yapısı.
- **🧱 Atomik (Lego) Eylem Sistemi:** Farenin "tuşuna basılı tutması", "x saniye beklemesi", sonra "tuşu bırakması" gibi birbirinden tamamen bağımsız atomik komutlar tek tek sıralanıp mükemmel döngüler yaratılabilir.
- **🎨 Görsel Kurucu (Excel Tarzı Tablo):** Makronuzu oluşturup satır içi düzenleme ile X ve Y koordinatlarına çift tıklayarak düzeltebilir, "Seçiliyi hızlandır" butonlarıyla detaylı rötuşlar yapabilirsiniz.
- **🔄 Akıllı Döngü ve Canlı Sayaç:** Döngü Sayısına `-1` yazdığınızda siz durdurana kadar devam eder. Ayrıca durdurduğunuz an makronuzun arka planda başarıyla tamamladığı tur sayısını ekranda kalıcı olarak gösterir.
- **💾 Kapanma Hafızası (Autosave):** Elektrik gitse de, programı hızla kapatsanız da son yazdığınız makro ve son döngü sayılarınız her zaman program başlatıldığında sizin için hazırdır! Tek tuşla **"Öncekini Kurtar"** diyerek anında geri yüklenebilir.
- **🧊 Donanım Serbest Bırakıcı (Hardware Release):** Makro aniden durdurulduğunda cihaz tuşlarınızı (CTRL, SHIFT, Mouse Sağ/Sol) serbest bırakır. "Tuş takılı kaldı" korkusu olmadan güvenle makroyu durdurabilirsiniz!
- **🌐 Çapraz Platform Mükemmelliği:** Aynı şekilde hem **Mac/Linux (.sh)** hem de **Windows (.bat)** ortamında derlenip sorunsuz çalışır!


---

## 🚀 Hızlı Başlangıç

### Windows 🪟
1. Depoyu bilgisayarınıza indirin (ya da Clone'layın).
2. Klasör içerisindeki **`build_and_run.bat`** dosyasına **iki kere tıklayın**. (Eksik bağımlılıkları çeker ve `target/classes` klasörüne temiz bir kurulum yapar).
3. Sonraki açılışlarınızda direkt **`run.bat`** ile açabilirsiniz!

### macOS / Linux 🍎
1. Terminalinizi klasör içerisinde açın.
2. Yetkileri ayarlamak için `chmod +x build.sh run.sh` çalıştırın.
3. Derlemek ve başlatmak için `./build.sh` komutunu çalıştırın. (Erişilebilirlik izni vermeyi unutmayın).
4. İlerleyen açılışlarda anında `./run.sh` ile başlayabilirsiniz.

*(Not: Ortamınızda Java 11 ve üzeri yüklü olmalıdır)*

---

## ⌨️ Klavye Kısayolları (Tamamen Tek Tuş!)

Ara yüz kalabalığı tamamen devreden çıkarılmış, Başlat ve Durdurma fonksiyonları akıllandırılarak "Tek Tuş (Toggle)" haline getirilmiştir:

- **Kaydı Başlat / Durdur:** `F7`
- **Makroyu Oynat / Durdur:** `F9`

*Tüm bu kısayolları `Ayarlar > Kısayol Ayarları` menüsüne girerek istediğiniz klavye tuşuna veya harfine eşitleyebilirsiniz!*

---

## 🛠️ Görsel Kurucu İle Neler Yapılır?

- **Sadece Fareyi Oraya Kaydır:** Mouse'unuzu seçtiğiniz bir X / Y pikseline 2 saniye içerisinde (kayarak) gitmesini sağlayabilirsiniz.
- **Bulunduğu Yere Tıkla:** Kamera açısının sapmasının istenmediği oyunlarda (Minecraft, vs.) makroya `-` koordinatlı tık verirseniz, fareyi ne tarafa çevirirseniz makronuz oraya doğru kazı yapar / saldırı yapar! Cihazı kilitlemez.
- **Sürükle-Bırak:** Tablodaki verilerin sırası mı karıştı? Excel tablosunda olduğu gibi satırın solundan fareyle tutarak olayları zincirde istediğiniz yere çekebilirsiniz.

---

## 🔒 Güvenlik Uyarıları ve Hatalar (False / Positives)
Uygulama `JNativeHook` adında global bir tuş dinleyicisi içerir. Makronuzu Minecraft'ta arka planda çalışırken bile durdurabilmeniz için klavyeyi derinden dinlemesi gerekir. 
Eğer Windows Defender veya Antivirüsler bir uyarısı verirse, bu tamamen global dinleme sebebiyledir. Başka bir deyişle **yanlış alarmdır**. Depo tamamen açık kaynaklıdır ve arka planda asla veri transferi yapmaz.

**Mac Kullanıcıları İçin:** Çalıştırmak istediğiniz terminal yazılımının **Erişilebilirlik** izinleri olduğundan emin olun (Ayarlar -> Gizlilik ve Güvenlik -> Erişilebilirlik).

---

> 🎉 **Teşekkürler:** *"Bu uygulama insan isteklerinin sınırlarını aşan bir hız ve titizlikle Yapay Zeka tarafından oluşturulmuştur. Makrolayabileceğiniz sınır sadece hayal gücünüzdür!"*