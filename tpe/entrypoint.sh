#!/bin/bash

flutter pub get && flutter doctor && flutter build apk --release
mv build/app/outputs/apk/release/app-release.apk /tpe/apk/tpe.apk
tail -f /dev/null