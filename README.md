# 📺 Live TV Tower Android

**Java Android live TV app. 800+ channels. Search, tap, watch.**

Live TV Tower Android is a lightweight native Android app for browsing and watching live TV channels on mobile. It is built with **Java**, **XML layouts**, **RecyclerView**, and **Android Media3 ExoPlayer**.

No Kotlin.  
No Jetpack Compose.  
No web wrapper.

---

## ✨ Features

### 📡 800+ TV Channels

Browse a large live TV catalogue directly inside the app.

- 800+ available channels
- Channels from many countries
- Channel names, country codes, flags, and categories
- Searchable mobile-friendly list
- Runtime loading instead of manually hardcoding every channel into the app

---

### 🔎 Channel Search

Quickly search for channels by:

- Channel name
- Country code
- Category

Examples:

```txt
US
TN
sports
music
movies
france
```

---

### ▶️ Fullscreen Live Playback

Tap any channel to open the fullscreen player.

Playback is powered by **AndroidX Media3 ExoPlayer** and supports common live streaming formats, including HLS `.m3u8` and DASH `.mpd`.

---

### 🔁 Automatic Stream Fallback

Some channels include more than one playable stream.

If the first stream fails, the app automatically tries the next available stream before showing an error.

This makes playback more reliable when some live streams are offline, slow, expired, or region-blocked.

---

### ☕ Java-Only Android Project

This app is intentionally built using traditional Android development tools.

| Area | Technology |
|------|------------|
| Language | Java |
| UI | XML Views |
| Player | AndroidX Media3 ExoPlayer |
| List UI | RecyclerView |
| Networking | Java networking |
| JSON Parsing | org.json |

---

## 📸 Screenshots

Add screenshots here after running the app:

```md
![Channel List](screenshots/channel-list.png)
![Player Screen](screenshots/player-screen.png)
```

Recommended screenshots:

1. Main channel list
2. Search results
3. Fullscreen player
4. Loading state

---

## 🚀 Getting Started

### Requirements

- Android Studio
- Android SDK
- Java support
- Internet connection

Recommended minimum SDK:

```txt
API 23+
```

---

## 🛠 Build Instructions

Clone the repository:

```bash
git clone https://github.com/namerami/LiveTVTower-Android.git
cd LiveTVTower-Android
```

Open it in Android Studio:

```txt
File → Open → Select the LiveTVTower-Android folder
```

Wait for Gradle sync to finish.

Then run:

```txt
Run ▶
```

Choose an Android emulator or a physical Android device.

---

## 📦 Export APK

To build a quick debug APK:

```txt
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

The APK will be generated around:

```txt
app/build/outputs/apk/debug/app-debug.apk
```

For a proper release APK:

```txt
Build → Generate Signed Bundle / APK → APK → Create or choose keystore → release
```

The signed APK will usually be generated around:

```txt
app/build/outputs/apk/release/app-release.apk
```

Rename it before uploading to GitHub Releases:

```txt
LiveTVTower-Android-v0.1.apk
```

---

## 🗂 Project Structure

```txt
LiveTVTower-Android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/namerami/livetvtower/
│   │       │   ├── MainActivity.java
│   │       │   ├── PlayerActivity.java
│   │       │   ├── adapter/
│   │       │   │   └── ChannelAdapter.java
│   │       │   ├── data/
│   │       │   │   └── ChannelCatalogLoader.java
│   │       │   └── model/
│   │       │       └── Channel.java
│   │       ├── res/layout/
│   │       │   ├── activity_main.xml
│   │       │   ├── activity_player.xml
│   │       │   └── item_channel.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── gradle.properties
├── README.md
├── LICENSE
└── .gitignore
```

---

## 📱 Current Version

### v0.1

Current features:

- Java Android app
- XML-based interface
- 800+ channel catalogue
- Searchable TV channel list
- Fullscreen player
- Live stream playback
- Automatic fallback when a stream fails
- Simple mobile-first UI

---

## 🧭 Roadmap

Planned features:

- Favorites
- Country filter
- Category filter
- Channel logos in the list
- Custom M3U import
- Recently watched channels
- Last watched channel restore
- Picture-in-picture mode
- Android TV layout
- Better offline and loading states
- Darker polished TV-style UI

---

## ⚠️ Disclaimer

Live TV Tower Android does not host, store, own, or redistribute any video content.

The app is a player and browser for live TV streams. Stream availability is not guaranteed. Some streams may be offline, region-blocked, expired, or unsupported on certain devices.

This project is intended for educational and personal use.

---

## 📄 License

MIT — see [LICENSE](LICENSE).
