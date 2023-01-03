# T-DEV-700


# Overview

This project is a course project for Epitech school and is a complete system for managing a shop. It includes a server application coded with Java and SpringBoot, a mobile shop application built with Flutter, and a payment terminal also built with Flutter.

## Table of Contents
- [Architecture](#architecture)
- [Get Started](#get-started)
  - [Server application and services](#server-application-and-services)
  - [Shop and Payment Terminal applications](#shop-and-payment-terminal-applications)
- [Server Application](#server-application)
- [Shop Mobile Application](#shop-mobile-application)
- [Payment Terminal](#payment-terminal)

## Architecture

The architecture of the application is as follows: when an order is completed in the shop mobile application, it opens a request for a transaction and sends it to the server. The server opens a websocket connection with the shop mobile application and the payment terminal, if available. The server then initiates the payment process, which can be completed using either a credit or debit card, or a check, on the payment terminal. Both the shop mobile application and the payment terminal are designed to react to messages received via websocket from the bank regarding the current transaction.

---------------------------------------------------------------

## Get Started

### Server application and services

To get started with the project using `docker-compose`, you will need to set up the following:

1. Clone the repository to your local machine
4. Start the server and services with `docker-compose up --build`

### Shop and Payment Terminal applications

#### Flutter installation 

- Download and install Flutter by following the instructions on the official website: https://flutter.dev/docs/get-started/install.

- Open a terminal and verify that Flutter is installed by typing : `flutter doctor`. This command will tell you if all the necessary dependencies are present on your computer and if everything is ready to develop with Flutter.

---------------------------------------------------------------

#### Shop

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

#### Payment Terminal

- Go to the Payment Terminal directory with the following command :   
`cd ./T-DEV-700/tpe`

- To run the app, type :   
`flutter run --dart-define=ENV='' --dart-define=TPE_REGISTER_SECRET_KEY='' --dart-define=TPE_REGISTER_SECRET_HEADER=''`  

Don't forget to fill in the variables (inside the quotes) according to your environment variables.  
This will run the app on the emulator or on a physical device connected to your computer.

##### Variables

ENV = 'local' or 'prod'


Note: If you want to run the app on a physical device, you must first enable USB debugging on your device and connect it to your computer with a USB cable.

#### Flutter Documentation

The mobile shop application and payment terminal are built with Flutter, an open-source mobile application development framework created by Google. 

To learn more about Flutter, you can refer to the following resources:

- [Flutter documentation](https://flutter.dev/docs)
- [Flutter API reference](https://api.flutter.dev/)
- [Flutter cookbook](https://flutter.dev/docs/cookbook)

---------------------------------------------------------------

## Server Application

The server application is a Java server built with SpringBoot that handles all of the logic for the shop system. It is responsible for handling requests from the mobile shop application and the payment terminal, as well as interacting with the database.

---------------------------------------------------------------

## Shop Mobile Application

The shop mobile application is a Flutter app that allows shop employees to scan items, view the shop's inventory, and process payments. When an order is completed, it opens a request for a transaction and sends it to the server via a websocket connection. It then listens for messages via websocket from the server and the bank regarding the status of the transaction.

---------------------------------------------------------------

## Payment Terminal

The payment terminal is a Flutter app that runs on a Android device and allows customers to make payments using their credit or debit cards. It communicates with the server application via a websocket connection to process payments and update the inventory. It listens for messages via websocket from the server and the bank regarding the status of the transaction.