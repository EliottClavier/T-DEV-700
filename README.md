# T-DEV-700


# Overview

This project is a course project for Epitech school and is a complete system for managing a shop. It includes a server application coded with Java and SpringBoot and linked to a MariaDB server, a Redis server and a Mailhog server. It also includes a mobile shop application built with Flutter and a payment terminal also built with Flutter.

## Table of Contents
- [Contributors](#contributors)
- [Architecture](#architecture)
- [Server Application](#server-application)
- [Shop Mobile Application](#shop-mobile-application)
- [Payment Terminal](#payment-terminal)
- [Project Management](#project-management)
- [Get Started](#get-started)
  - [Use the production environment](#use-the-production-environment)
  - [Launch your own environment](#launch-your-own-environment)
    - [Server application and services](#server-application-and-services)
      - [Set environment variables](#set-environment-variables)
      - [Generate mailhog.auth](#generate-mailhogauth)
      - [Launch `docker-compose`](#launch-docker-compose)
  - [Shop and Payment Terminal applications](#shop-and-payment-terminal-applications)
    - [Flutter installation](#flutter-installation)
    - [Shop](#shop)
    - [Payment Terminal](#payment-terminal)
    - [Flutter documentation](#flutter-documentation)
- [Tests](#tests)
  - [Launch server application and services tests](#launch-server-application-and-services-tests)
  - [Launch flutter applications tests](#launch-flutter-applications-tests)

---------------------------------------------------------------

## Contributors

Our team is composed of:
- CLAVIER Eliott
- MARTIN Maxime
- PIGNON Nathan
- MATHÉ Clément
- RIPAULT Paul

---------------------------------------------------------------

## Architecture

The architecture of the application is as follows: when an order is completed in the shop mobile application, it opens a request for a transaction and sends it to the server. The server opens a websocket connection with the shop mobile application and the payment terminal, if available. The server then initiates the payment process, which can be completed using either a credit or debit card, or a check, on the payment terminal. Both the shop mobile application and the payment terminal are designed to react to messages received via websocket from the bank regarding the current transaction. Both applications are notified about the transaction result operated by the server, whether it's a success or a failure.

---------------------------------------------------------------

## Server Application

The server application is a Java server built with SpringBoot that handles all of the logic for the shop system. It is responsible for handling requests from the mobile shop application and the payment terminal, as well as interacting with the database.

---------------------------------------------------------------

## Shop Mobile Application

The shop mobile application is a Flutter app that allows shop employees to scan items, view the shop's inventory, and process payments. When an order is completed, it opens a request for a transaction and sends it to the server via a websocket connection. It then listens for messages via websocket from the server and the bank regarding the status of the transaction.

---------------------------------------------------------------

## Payment Terminal

The payment terminal is a Flutter app that runs on a Android device and allows customers to make payments using their credit or debit cards. It communicates with the server application via a websocket connection to process payments and update the inventory. It listens for messages via websocket from the server and the bank regarding the status of the transaction.

---------------------------------------------------------------

## Project Management
You can access our Trello by clicking on this link:
- [Our Trello board](https://trello.com/invite/b/b30uHQCj/ATTIa3135fb8477da058b9be864797bbffb5558079B9/cashmanager)

Our Trello board lists all tasks completed by the team in order to accomplish the project.

---------------------------------------------------------------

## Project Delivery

### Mockups

Here are the links to the different mobile application mockups, made on Figma.

#### Shop

- [Shop designs](https://www.figma.com/file/f0JXyglvKGQCILHpkgVcRb/Zoning-%2F-Maquettage-CashManager?t=Uqh87dhvWtoDJ6qy-1)

#### Payment Terminal

- [Payment Terminal designs](https://www.figma.com/file/593IOdR4OfIqVT1viiBbC1/Zoning-%2F-Maquettage-TPE?t=Uqh87dhvWtoDJ6qy-1)

### Diagrams and functional tests

The documents are in the Diagrams_and_tests package, at project's root. 

---------------------------------------------------------------

## Get Started

There are two ways you can test the whole stack of the application:
using the production environment or setting up your own environment.

### Use the production environment
First of all, you can download the latest versions of the shop application and the payment terminal application using these links:

- [Download the shop application](https://api.cash-manager.live/download/shop.apk)
- [Download the payment terminal application](https://api.cash-manager.live/download/tpe.apk)

Once you have downloaded the applications, install them on separate terminals such as mobile phones or emulators. It is recommended to use at least one mobile phone for the payment terminal application so that you can utilize its features, such as QR code and NFC scanning.

To be able to use your payment terminal, the server administrator will need to whitelist it on the administrator dashboard, which you can access using this link:
- [Administrator dashboard](https://api.cash-manager.live/admin/login)

__For evaluation purposes, you will be able to log in to the dashboard using the provided credentials__

You will also need the auto-generated password linked to your payment terminal. This password allows the payment terminal to connect to the websocket server and is sent via our own hosted mail server, which has an interface for viewing incoming mail. You can access this interface using the following link:
 - [Mail server interface](https://mail.cash-manager.live)

__For evalutation purposes, you will be able to connect to the mail server interface using the credentials provided__

Once you have retrieved your auto-generated password on the mail server interface, and you have whitelist your payment terminal on the administrator dashboard, you will finally be able to test the whole process of opening and completing transaction, from the shop to the bank using the payment terminal.

Notice that you will need to have a valid credit card already registered in database to use the NFC scan feature. You can use the QR Code scan of the payment terminal by scanning QR Code you have generated on the admin interfaces (look for the QR Code generator in the administrator interface).

__For evalutation purposes, you will be able to use the NFC scan feature using the credit card provided__

### Launch your own environment

Make sure you have Git installed. You can check if it's installed by typing : `git --version` in the terminal. If it's not installed, you can download it from the official website: https://git-scm.com/.

Clone the repository to your local machine, open a terminal at the place where you want to install the project and typing :  
`git clone https://github.com/EpitechMscProPromo2024/T-DEV-700-NAN_5.git`


Notice that you will also need to have Docker installed on your machine in order to launch your own environment. If you are not familiar with Docker,
we invite you to read the official docs, which helps to install everything you will need for this project:
- [Docker documentation](https://docs.docker.com)

#### Server application and services

To get started with the server application part of the project using `docker-compose` command, you will need to set up few things after __you have cloned the project__. The `docker-compose` file used to launch your own environment will set up all the services you will need to run the "Bank manager" part of the application, but also the "WebSocket server" linked to a Redis server and the "Authentication manager" which provides security on the API provided by the server application.

##### Set environment variables

The `docker-compose` file needs few environment variables files to configure services and provide environment variables to the running containers.
All the environment variables used in this project are listed in multiple `.example.env` files located in the env folder of the project. First of all, you need to copy all these files and rename each copy by removing the `.example` extension from the file name (ex: `api.example.env` becomes `api.env`).

Among these `.env` files, we have:
- `api.env`, which lists the environment variables proper to the api. Notice the `DEFAULT_MANAGER_USERNAME` and the `DEFAULT_MANAGER_PASSWORD` which corresponds to the administrator login to access the administrator dashboard
- `database.env`, which is used to set up the database and connect it with the server application
- `mail.env`, which is used to set up the mail server and connect it with the server application
- `redis.env`, which is used to set up the redis server and connect it to the server application
- `shop.env`, which defines the credentials used by the shop application to connect to the websocket server; `DEFAULT_SHOP_USERNAME` and `DEFAULT_SHOP_PASSWORD` will be used when running the shop application (see [Shop and Payment Terminal applications](#shop-and-payment-terminal-applications))
- `tpe.env`, which defines the `TPE_REGISTER_SECRET_HEADER` and the `TPE_REGISTER_SECRET_KEY` used to block payment terminal registring requests from sources other than our payment terminal application. These two variables will be used when running the payment terminal application (see [Shop and Payment Terminal applications](#shop-and-payment-terminal-applications)) 
- `.env` which repeats some variables in order to execute the `docker-compose` command. This `.env` is only used to build docker-compose stack

You will have to fill almost every environment variables described in these `.env` files to make the `docker-compose` and the services work (you don't have to fill variables marked like "ONLY FOR PRODUCTION MODE"). We advise you to read every environment variable description to understand their role and how to fill them correctly.

##### Generate mailhog.auth
In order to start the `mailhog` service inside the `docker-compose`, you will have to generate a `mailhog.auth` file
which activates a login prompt when you access the mail server interface.

`mailhog.auth` file is built as follow:
```
<username>:<bcrypt_encrypted_hash>
```
Here is one concrete example:
```
admin:$2a$12$nomMs/pcaCDSycqSsaLsE.XNxjrArD9wxfbY6zn4gIfK1e4oJvUd6
```
__In case you want to use the above string, know that the bcrypt encrypted hash stands for "admin". You can also generate your own bcrypt encrypted hash with tools like [Bcrypt-Generator](https://bcrypt-generator.com).__ 

Once generated, put the `mailhog.auth` file alongside the `.env` files inside the `env` folder.

##### Launch `docker-compose`

With the environment variables set and the `mailhog.auth` file generated and placed in the correct flder, you can now use the `docker-compose` command from the root of the project with the following command:
```
docker-compose -f .\docker-compose.yml --env-file ./env/.env up --build
```
The command parameters indicates that we want to use the `docker-compose.yml` file and build it with the environments variables from the `.env` file located in env folder. Once all the services are running, you will now be able to request the different features of the server application from the payment terminal application and the shop application. 

Notice that the `api` service is designed to restart its `Tomcat server` when Spring project is rebuild. You can rebuild the project by launching the `docker-compose` command again or by setting `IntelliJ` with JDK 17.

#### Shop and Payment Terminal applications

##### Flutter installation 

- Download and install Flutter by following the instructions on the official website: https://flutter.dev/docs/get-started/install.

- Open a terminal and verify that Flutter is installed by typing : `flutter doctor`. This command will tell you if all the necessary dependencies are present on your computer and if everything is ready to develop with Flutter.

---------------------------------------------------------------

##### Shop

- Go to the shop directory with the following command :   
```
cd ./T-DEV-700/shop
```

- To run the app, type :   
```
flutter run --dart-define=ENV='' --dart-define=SHOP_USERNAME='' --dart-define=SHOP_PASSWORD='' --dart-define=API_URL=''
```  
Don't forget to fill in the variables (inside the quotes) according to your environment variables (see `shop.example.env`).  
This will run the app on the emulator or on a physical device connected to your computer.

Note: If you want to run the app on a physical device, you must first enable USB debugging on your device and connect it to your computer with a USB cable.

For more details `--dart-define` about variables:

- **ENV** = 'local' or 'prod'

Note: For 'local' use, you must add the `API_URL` variable in run command.
This able the app to interact with server run on your computer on local use, on a same network. Your computer local IPv4 is usually like 192.168.x.x.

- **API_URL** = '***[YOUR_LOCAL_IPV4]***:8080'

Run it like this :

```
flutter run --dart-define=ENV='local' --dart-define=SHOP_USERNAME='' --dart-define=SHOP_PASSWORD='' --dart-define=API_URL='YOUR_LOCAL_IPV4:8080'
```  

- **SHOP_USERNAME** and **SHOP_PASSWORD** are both secret value, given by project's owners in production mode, and filled in `shop.env` in local environment.

##### Payment Terminal

- Go to the Payment Terminal directory with the following command :   
```
cd ./T-DEV-700/tpe`
```

- To run the app, type :   
```
flutter run --dart-define=ENV='' --dart-define=TPE_REGISTER_SECRET_KEY='' --dart-define=TPE_REGISTER_SECRET_HEADER=''`  
```

Don't forget to fill in the variables (inside the quotes) according to your environment variables (see `tpe.example.env`).  
This will run the app on the emulator or on a physical device connected to your computer.

For more details `--dart-define` about variables:

- **ENV** = 'local' or 'prod'

**Note: For 'local' use, you must add the API_URL variable in run command.
This able the app to interact with server run on your computer on local use, on a same network. Your computer local IPv4 is usually almost like 192.168.x.x.**

- **API_URL** = '***[YOUR_LOCAL_IPV4]***:8080'

Run the app like this :

```
flutter run --dart-define=API_URL='YOUR_LOCAL_IPV4:8080' --dart-define=ENV='local' --dart-define=TPE_REGISTER_SECRET_KEY='' --dart-define=TPE_REGISTER_SECRET_HEADER=''`
``` 

- **TPE_REGISTER_SECRET_HEADER** and **TPE_REGISTER_SECRET_KEY** are both secret value, given by project's owners in production mode, and filled in `tpe.env` in local environment.

#### Flutter Documentation

The mobile shop application and payment terminal are built with Flutter, an open-source mobile application development framework created by Google. 

To learn more about Flutter, you can refer to the following resources:

- [Flutter documentation](https://flutter.dev/docs)
- [Flutter API reference](https://api.flutter.dev/)
- [Flutter cookbook](https://flutter.dev/docs/cookbook)

---------------------------------------------------------------

## Tests

### Launch server application and services tests

You can launch tests written for the server and the services linked with the `docker-compose.test.yml` file using the following command:
```
docker-compose -f .\docker-compose.test.yml --env-file ./env/.env up --build
```

It will run a volume-free instance of all the services from the classic `docker-compose.yml` that are necessary in order to execute tests. 

Once the server has finished to run test, you will be able to find the results inside the `build` folder of the `api` folder. From the root of this project, the excepted path is the following:
```
./api/build/reports/tests/test/
```

Once inside this folder, you can open the `index.html` file which gives a visual interface indicating which tests are successful or not.

### Launch flutter applications tests

To run tests in Flutter, follow these steps:

  - Open a terminal window and navigate to the root directory of your Flutter project.
  - Run the flutter test command. This will automatically run all the tests in your project.