# WildRank - Desktop App

[![Build Status](https://travis-ci.org/nwalters512/wildrank-desktop.png?branch=master)](https://travis-ci.org/nwalters512/wildrank-desktop)

The desktop companion to the WildRank mobile app.

##How to Run

With Java 7.0 installed, running Wildrank is as simple as double clicking the file "wildrank.jar", located in the folder "wildrank."

## Usage

The desktop app for WildRank serves several primary functions. First, it is used to download event data to be used by the tablets; this includes a list of teams and matches at the event. It also allows you to group teams into a user-defined number of groups. This is useful for pit scouting, where you may want to send several teams out to scout at once but you want to avoid double-scouting. It is also used to compile notes produced by the tablets. Because several tablets could be adding notes on the same team at the same time, we use the desktop to compile those notes into a "finalized" version. The desktop can also output a CSV file of all match data for use in a spreadsheet program, as well as generate PDFs of note and pit data. However, since we have added the ability to view that data directly on the tablets, those functions probably won't be used.

### First time setup

When you first compile and run WildRank desktop, it will prompt you for some configuration data. At this point, you should ensure that you have a flash drive plugged into your computer, an Internet connection, and a folder somewhere on your computer where you want to store the data. First, select the **root** of your flash drive in the window that says "Select the Flash Drive location". Next, select the folder you want to store data in on the "Select the Local location" window.

Now that the app knows where to store its data, it will prompt you for your team number and the current year. Enter those in the appropriate fields and click "Load events". Select the event you will be scouting and click "Download" on the next screen. At this point, the data will be downloaded using the [The Blue Alliance](http://thebluealliance.com) API. Once downloading the event data has finished, enter the number of tablets you wish to use for pit scouting. Teams will be grouped into that number of groups; you can later assign each tablet to pit scout a specific group. Click "Write pit configuration" and wait for that to complete.

At this point, the data is only stored locally on your machine. To sync it to the flash drive, click "Sync with flash drive". Once that completes, you can use the flash drive to configure each of your tablets!

### Subsequent launches

After the initial configuration, the app saves the local directory, the flash drive directory, and the current event in a file called ```save.json``` that can be found in the root directory of the project. On each subsequent launch, the app will look for that file. If it finds it, it will ask you to load it. Clicking "yes" will let you load that configuration into the app and skip the initial setup again. If you change directories or the event later, make sure you click "Save configuration" on the main menu!

When you want to setup WildRank for a new event, you can do one of two things. You can click "no" when prompted to load the saved data, which will put you into initial configuration mode. Alternatively, you can delete the save file. When the app doesn't find it, if will prompt you for configuration.

### Syncing and managing data

Once you have collected some data with the tablets, you will probably want to sync that data back to your computer in order to compile notes and generate CSV data. The steps to do so will typically look something like the following:

1. Click "Sync with Flash Drive" to load the latest data from the flash drive and push out any data that may have been modified since the last sync (for instance, edited notes).
2. To compile notes, click "Prepare notes" and then "Compile".
3. Click "Sync with Flash Drive" to sync out the newly compiled notes to the flash drive.

And that's it! In the future, we plan to add a single button to automate all those tasks. Now that you have the latest data from the tablets on your computer, you can generate CSV data or a PDF.

### Configuring games

WildRank allows you to define a game configuration that will be used to parse match files into a CSV, as well as to provide support for manually entering match data. The latter is important because if something were to happen to your tablets, you would still be able to manually collect and enter data into the WildRank system.

Games are defined with a file called ```game.wild``` that is located in the root of the directory of your local location. A game config file looks something like this:

```
game-name: Aerial Assist
main-key: scoring
section-key: autonomous
item: Auto High Goals, scored_high; num
item: Auto Low Goals, scored_low; num
item: Auto High Hot Goals, scored_hot_high; num
section-key: teleop
item: High Goals, scored_high; num
item: Low Goals, scored_low; num
item: Missed Goals, missed; num
item: Scoring Zones, score_zones; text
section-key: post_match
item: Damaged, damaged; bool
item: Tipped, tipped; bool
item: Lost Comm, lost_comm; bool
```

The ```game-name``` line defines the name of this game. The ```main-key``` attribute defines the root element in the match JSON object that scoring info is located in. Each ```section-key``` defines a JSON object located within the ```main-key``` object. Finallly, each ```item``` represents a single object within the specified ```section-key``` object. The format of an item is as follows:

```
item: NAME, JSON_KEY; TYPE
```

Each attribute means something slightly different depending on where it is used (either the CSV generator or the manual match entering mode).

For the CSV generator:
 * ```main-key```, ```section-key```, and the ```JSON_KEY``` attribute of an item are used to locate a specific JSON object within a match results JSON object. For instance, given the above configuration file, the "Lost Comm" item is expected to be located within a match result JSON object as follows:

```json
{
    "scoring": {
        "post_match": {
            "lost_comm": true
        }
    }
}
```

 * ```NAME``` is included in the first row of the CSV file as a column header.
 * ```TYPE``` determines how the specified object will be written in the CSV. A ```num``` type is expected to be a number and is written as such. A ```text``` type is included as a string wrapped in double quotes. A ```bool``` type is written as a 1 for ```true``` and a 0 for ```false```.

For manual match entering:
 * ```main-key```, ```section-key```, and the ```JSON_KEY``` attribute of an item are all used to place values within a match result JSON object. Behavior is the same as in the CSV generator.
 * ```NAME``` is displayed as a label next to the input field.
 * ```TYPE``` determines the input field that is presented for the given item. ```num``` will produce an input box for numbers, ```text``` will produce a standard text field, and ```bool``` will produce a checkbox.

### CSV Generation

The CSV file for matches is generated based on the provided ```game.wild``` file. Each line contains the match number, the team number, and the scouter's name as the first three values. The remaining values consist of each ```item``` from the ```game.wild``` file in the order they appear in the file.


#Contributing
Want to add features, fix bugs, or just poke around the code? No problem!

1. Set up your development environment; this project was built using Eclipse.
2. Fork this repository, import the project to your IDE, and create a branch for your changes
3. Make, commit, and push your changes to your branch
4. Submit a pull request here and we'll review it and get it added in!

For more detailed instructions, checkout [GitHub's Guide to Contributing](https://guides.github.com/activities/contributing-to-open-source/)

We ask that all contributions be team/game agnostic; they should support the framework in general and not be designed for any one game or team. If you don't know if that describes the feature you want to submit, or if you can make the case that your specific feature would be valuable, open an issue and we'll get back to you!
