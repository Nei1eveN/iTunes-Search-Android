# iTunes-Search-Android
Implementing 
- Kotlin Coroutines, MvRx, Realm Database, Retrofit, and Glide as core
- Epoxy for recyclerview / UI complexities
- Navigation Jetpack

Creating this application requires a knowledge on handling screen complexities, because a record can be of different wrapper types and kinds of tracks. To make this happen, I decided to meet the objectives with the libraries mentioned above. The app requirements are the following:

General:

The list in the app can show the following details from the API:

- Track Name
- Artwork
- Price
- Genre

Also, the long description for the selected item

Placeholder image for unloaded image is also applied for better user experience.

Persistence:

1. Functions of the app can be explored without a hassle since I used Navigation Jetpack Components
2. Using Realm as database, I'm able to persist the response from the API and also able to show the updated list using its listener

The suggestions are also applied:
1. A date when the user previously visited, shown in the list header
2. Save the last screen that the user was on. When the app restores, it should present the user with that screen. 

I have also added some attributes to the objects to meet my needs

Architecture:

The design pattern used is MVI, also known as Model-View-Intent. But the catch in implementing the pattern is by using the MvRx which made it easier.

There are a few advantages to this design. Firstly, because the flow of data is unidirectional, it becomes fairly straight-forward to debug. You can easily follow where the data is going and track down where it goes awry. In addition, you are specifying specific intents which you will process. This means that we don’t have to worry about undefined behaviors, because as long as no exception occurs, no action will be performed. Lastly, the state acts as a “single source of truth” which can interact cleanly with multiple modules if needed, and can be easily restored if processes are killed in the background.

MvRx (Mavericks) is an Android Framework from AirBnB. It uses Kotlin, RxJava, and base Android Architecture components and has a state management and reactive design similar to React.js. They recommend using it alongside their other Library, Epoxy, to easily build/rebuild your views.

Documentation:

The documentation of variables and functions are documented inside the source code. Please feel free to review.
