# ToDoList - Android Task Management App

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**ToDoList** is a simple Android application designed to help you manage your daily tasks. Built with Jetpack Compose for its UI and Retrofit for API communication.

## Features

*   **Task Management:**
    *   Create new tasks.
    *   Update existing tasks.
    *   Delete tasks.
*   **Real-time Synchronization:**
    *   Tasks can be synced with a remote server.
*   **Loading and Error Handling:**
    *   Displays a loading indicator while tasks are being fetched.
    *   Shows clear error messages if any operation fails.
*   **Empty State:**
    *   Displays a "No tasks yet!" message when the list is empty.
*   **Modern UI:**
    *   Utilizes Material Design 3 components for UI.

## Tech Stack

*   **Kotlin:** The primary programming language for the app.
*   **Jetpack Compose:** A modern toolkit for building native Android UIs.
*   **Material Design 3:** A comprehensive design system for creating digital interfaces.
*   **ViewModel:** For managing UI-related data and lifecycle awareness.
*   **Coroutines:** For managing asynchronous tasks.
*   **Clean Architecture:** A software design pattern for separation of concerns.

## Getting Started

### Prerequisites

*   Android Studio installed on your machine.
*   An Android emulator or a physical Android device.

### Installation

1.  Clone the repository:
````git clone https://github.com/Danielbclr/ToDo-List-App.git````
2.  Open the project in Android Studio.
3.  Build and run the app on an emulator or device.

## Usage

1. **Adding Tasks:** Tap the "+" button (floating action button) to create a new task.
2. **Updating Tasks:**  Click on the task you want to update.
3. **Deleting Tasks**: Click on the delete button in the task you want to delete.
4. **Sync Tasks**: Click on the sync button in the top bar to synchronize with the server.
5. **Offline capability**: Add, delete and update tasks even when offline. The tasks will be synched automatically when the internet connection is back.

## Project Structure

*   **`app/src/main/java/com/danbramos/todolist`:**
    *   **`model`**: Data classes.
    *   **`view`**: Composable functions.
    *   **`viewmodel`**: ViewModel classes.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

* [Daniel Ramos](danielbclramos@gmail.com)

## Screenshots
* TODO: Add Screenshot