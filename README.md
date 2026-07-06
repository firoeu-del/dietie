# dietie 🍎

نسخه‌ی بومی اندروید (Kotlin + Jetpack Compose) از اپلیکیشن وب Dietrix — محاسبه‌گر هوشمند رژیم غذایی بر اساس وزن ایده‌آل، با طراحی **Material 3 Expressive**.

## امکانات

- محاسبه‌ی وزن ایده‌آل (IBW) و وزن تعدیل‌شده (AIBW)
- محاسبه‌ی کالری نگهدارنده (TEE) و کالری هدف روزانه (با کف ۱۰۰۰ کیلوکالری)
- درشت‌مغذی‌ها با درصدهای قابل تنظیم (پیش‌فرض ۵۳/۱۷/۳۰)
- برنامه‌ی وعده‌ها و واحدهای غذایی برای ۱۰ سطح کالری (۱۰۰۰ تا ۳۰۰۰)
- راهنمای معادل هر واحد غذایی
- تاریخچه‌ی ۱۵ محاسبه‌ی آخر با تاریخ شمسی (قابل حذف تکی/کلی)
- فیلتر نمایش بخش‌های نتایج (مانند نسخه‌ی وب)
- حالت روشن/تاریک با دکمه‌ی تغییر تم
- رابط کاملاً فارسی و راست‌به‌چپ (RTL) با اعداد فارسی
- ورودی اعداد فارسی و انگلیسی هر دو پشتیبانی می‌شود

## طراحی Material 3 Expressive

- `MaterialExpressiveTheme` با `MotionScheme.expressive()` (انیمیشن‌های فنری)
- `LargeFlexibleTopAppBar` با زیرعنوان
- گروه دکمه‌ی متصل (`ToggleButton` + `ButtonGroupDefaults.connected*ButtonShapes`) برای انتخاب جنسیت
- دکمه‌ی اصلی با `ButtonDefaults.shapes()` (تغییر شکل هنگام لمس)
- کارت‌ها و چیپ‌های گرد با پالت برند اصلی (بنفش #6A4BD4)

## ساخت و اجرا

1. پوشه را در **Android Studio** (نسخه‌ی Ladybug یا جدیدتر) باز کنید: `File > Open`
2. صبر کنید Gradle Sync تمام شود (نیاز به اینترنت برای دانلود وابستگی‌ها)
3. روی دکمه‌ی Run کلیک کنید یا از خط فرمان:

```bash
./gradlew assembleDebug
# خروجی: app/build/outputs/apk/debug/app-debug.apk
```

### پیش‌نیازها

- JDK 17
- Android SDK 36
- حداقل اندروید پشتیبانی‌شده: Android 8.0 (API 26)

## ساختار پروژه

```
app/src/main/java/com/firoeu/dietie/
├─ MainActivity.kt          # نقطه‌ی ورود، تم و RTL
├─ data/
│  ├─ Models.kt             # گروه‌های غذایی، وعده‌ها، برنامه‌های کالری
│  ├─ DietCalculator.kt     # فرمول‌های IBW/AIBW/TEE
│  └─ Prefs.kt              # ذخیره‌ی تاریخچه، درصدها و فیلترها
├─ ui/
│  ├─ DietViewModel.kt      # منطق و وضعیت صفحه
│  ├─ MainScreen.kt         # تمام UI (فرم، نتایج، وعده‌ها، تاریخچه)
│  └─ theme/                # تم Material 3 Expressive
└─ util/Format.kt           # اعداد فارسی و تاریخ شمسی
```
