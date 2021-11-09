# MyTube - A YouTube clone

MyTube is an app that was created to improve my overall skills in Android, and to showcase my immense love for an app like YouTube.

There is no sign-in with google involved in this app, so definitely do check this out for free, without any fear of privacy issues.

But for all those who don't have Android Studio on their PCs, I am attaching a video link to the overall working of the app.

[Overall working of Mytube](https://drive.google.com/file/d/1bu-MhOQtV_7ntj0mqCQZDe_tOmbYe3FM/view?usp=sharing "MyTube overall")


The following video just shows the comments and replies section that was implemented for the videos.

[Comments for a video](https://drive.google.com/file/d/1bw81cNB40TS0j6CRecYFdHdgyalu5Q7-/view?usp=sharing "MyTube extras")

The Home screen fragment by default shows a mix of trending videos in India and USA.

## Guide to create a Youtube API key.

Steps for creating your API_KEY and adding it to the project
* Go to Google and Search for Google Developers Console. You may be required to sign in, if you have not already done so.
* The landing page should show something like this.
![Google Cloud Homepage](./googleCloudHome.jpg)
 * Click on the region where I have added a red circle. You get a new Dialog Box that shows a list of projects that you have already created. Select the new project option.
![New Project Option](./newProjectOption.png)

 * Give the project any name you like and create it.
* Once you create the project, you can select it from the project list. You also get a notification on clicking which you can go to your newly created project.
* In the new project, click on the red circle( Go to APIs overview ).
![New project home](./googleCloudNewProject.jpg)

* Click on enable apis and services.
![APIs and Services Page](./enableAPIAndServices.png)

* Search for Youtube Data API and select it from the results. Once the API page is opened click on enable.
![Searching for API](./searchForAPI.png)
* Once the API is enabled you will see an option at the top of the API Page, which says that this project requires credentials.
* Click on API keys and create a new API key. All you have to do then is copy the API key.

## Where to paste the created API key

* Go the util package of MyTube -> Constants.kt
* Paste your api key over there with identifier named as API_KEY, else there may be errors in the app, when you run it.



