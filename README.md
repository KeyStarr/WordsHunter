# WordsHunter

### Intro
This app is my second big project and it is built upon a fairly unique concept. I want to mention that I had to proccess lots of new info and that i had actually got this application released to the public. 

There is one more noticeable point - unfortunately, when i had almost finished the app, Google decided that the Accessibility Services [must only be used to aid users with disabilities](https://www.xda-developers.com/google-threatening-removal-accessibility-services-play-store/). Since my app uses such a service to analyze messages and thus to help all the users, the road to the Play Market for it was forever closed.

In spite of that obstacle i figured how to use my work and modified original app to the aid of the linguistic research which lasts in present.   

### Functionality
This app is the ultimate words's tracker or the "Words Hunter". 
#### Why use it?
- To eliminate so-called parasites (filler words) from your text speech, those as: like, you know, sort of, kind of..
- To track some other words. For example, did you ever wonder how many times do you write "I" per day? The use of this word, and of many others, can really tell something of your personality.
#### Actual functionality
- Set a list of words to track.
- See and analyze the stats of your usage of the tracked words for any particular day/week.
- Set a limiter on a certain word and get notified when your usage of it exceeds the limit.
- Get daily reports about the usage progress.

And, since it is a modification of the original app to the aid of the linguistic research, all the collected stats - once per day - are being sent to a remote server. (Although if that one delivery request fails, requests are starting to get sent every 2 hours until one completes).
### Development
#### Libs
This app features the use of the following libs:
- Dagger (2)
- Butterknife
- Retrofit
- Otto bus (now deprecated)
- Threetenapb
- MPChart
- Crashlytics

And some others which were used to make tutorials/UI components.

##### Android API
In this app I made a strong use of **Services, Accessibility Service, Broadcast Receivers, Notifications**. 

For the database SQLite was chosen, although the first versions of this app used Realm (change was made in value of the apk's size decrease).