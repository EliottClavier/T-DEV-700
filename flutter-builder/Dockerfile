FROM amazoncorretto:17

# Update packages
RUN yum install & yum update

# Install packages
RUN yum install -y wget unzip tar xz git which

WORKDIR /opt

# Download Flutter SDK
ENV FLUTTER=/opt/flutter
RUN wget -P $FLUTTER -O flutter.tar.xz https://storage.googleapis.com/flutter_infra_release/releases/stable/linux/flutter_linux_3.3.8-stable.tar.xz \
    && tar -xf flutter.tar.xz \
    && rm -rf flutter.tar.xz

# Setup Flutter
ENV PATH "$PATH:$FLUTTER/bin"
RUN git config --global --add safe.directory /opt/flutter

# Download Android SDK
ENV SDK=/opt/android-sdk
WORKDIR $SDK/cmdline-tools
RUN wget -O sdk.zip https://dl.google.com/android/repository/commandlinetools-linux-9123335_latest.zip \
    && unzip sdk.zip \
    && rm -rf sdk.zip
RUN mv cmdline-tools latest

# Setup Android SDK
WORKDIR $SDK/cmdline-tools/latest/bin
RUN yes | ./sdkmanager --licenses
RUN ./sdkmanager "build-tools;30.0.3" "patcher;v4" "platform-tools" "platforms;android-31" "sources;android-33"

ENV ANDROID_HOME "$SDK"
ENV PATH "$PATH:$SDK/bin"
ENV PATH "$PATH:$SDK/platform-tools"