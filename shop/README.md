# shop

### Flutter installation 

- Download and install Flutter by following the instructions on the official website: https://flutter.dev/docs/get-started/install.

- Open a terminal and verify that Flutter is installed by typing : `flutter doctor`. This command will tell you if all the necessary dependencies are present on your computer and if everything is ready to develop with Flutter.

---------------------------------------------------------------

### Project installation

- Make sure you have Git installed. You can check if it's installed by typing : `git --version` in the terminal. If it's not installed, you can download it from the official website: https://git-scm.com/.

- Clone the repository to your local machine, open a terminal at the place where you want to install the project and typing :  
`git clone https://github.com/EpitechMscProPromo2024/T-DEV-700-NAN_5.git`

- Then go to the shop directory with the following command :   
`cd ./T-DEV-700/shop`

- To run the app, type :   
`flutter run --dart-define=SHOP_USERNAME='' --dart-define=SHOP_PASSWORD='' --dart-define=API_URL=''`  
Don't forget to fill in the variables (inside the quotes) according to your environment variables.  
This will run the app on the emulator or on a physical device connected to your computer.

Note: If you want to run the app on a physical device, you must first enable USB debugging on your device and connect it to your computer with a USB cable.