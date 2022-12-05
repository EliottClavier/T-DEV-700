#!/bin/bash

flutter clean && flutter pub get && flutter doctor && flutter build apk --release
mv build/app/outputs/apk/release/app-release.apk /shop/apk/shop.apk
tail -f /dev/null