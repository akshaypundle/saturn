Saturn - 
======
[![Build Status](https://travis-ci.org/akshaypundle/saturn.svg?branch=develop)](https://travis-ci.org/akshaypundle/saturn)

Saturn give you access to option data and helps analyze returns for different strategies involving options (see [http://www.wikiwand.com/en/Options_strategies](http://www.wikiwand.com/en/Options_strategies)). The strategies are listed in the top bar. Saturn computes the return for each of the strategies for the entire market. The results are presented in a table where you can easily search, sort and filter results. 

General Usage
-------------
Saturn make it easy to analyze what stock give what kind of return for a given strategy. To achieve this, Saturn calculates returns for each of the strategies for each of the listed options on CBOE. The results are presented in a table where you can easily search, sort and filter them.

### Search
You can search by typing text in the search box. The input text is broken up into space delimited tokens and all columns are searched for each token. Here are a few sample searches you can try.


``TSLA 2015-04-24`` Serach for Tesla options expiring 2015-04-24

``TSLA 2015-04-24 1.67`` Serach for Tesla options expiring 2015-04-24 with any column having the string 1.67


### Filters
Saturn creates filters (left sidebar) for each of the numeric columns. You can filter by minimum and maximum value. Saturn ignores any blank filter boxes.

### Other features

* Sort by clicking on the table headers.
* Clicking on a row selects it and shows details about the stock including key indicators and a 5 day chart.
* Navigate to the next/previous row with down/up arrow and next/previous page with the right/left arrow keys.
* Data is refreshed daily, 3pm PST

Options
-------
This view shows raw calls and puts expiring over the next 2 weeks.

Covered calls
-------------
In the covered call strategy, you buy the underlying stock and sell an in the money call. In the event that the stock price is above the strike at expiry, the stock gets assigned and you keep the profit.

Saturn calculates return (ROI) and the Protect percentage for all listed options for the covered call strategy. The ROI is the return you make if the call gets assigned and protect pergentage is the amount the stock can drop for you to make zero return (disregarding trading costs). 

In general, making the same return with the greater protection is better. You can analyze this in saturn by filtering on minimum ROI, searching for the expiry and sorting on the protect%..



More details at the [covered call wiki entry](https://www.wikiwand.com/en/Covered_call)

Short Puts
----------

Short Puts are equivalent to the covered call strategy and the calculations that saturn does are very similar. Short puts allow you to get the same return as covered calls but with only one option trade (instead of an option and a stock in covered call). With short puts, there is also no investment up front.

More details at the [short put wiki entry](http://www.wikiwand.com/en/Option_(finance)#/Short_put)

Butterfly
---------

The butterfly strategy is a pure option strategy. It involves buying a low and a high call and selling 2 mid calls (eg: Buy 170 and 180 call and sell two 175 calls). Profit is maximum if the stock price at expiry is the mid call price.

Saturn calculates the butterfly returns for all options within 15% of the current stock price. The low, mid and high prices are displayed as well as the setup cost and the maximum ROI. The Zero% column shows how much the stock has to move for you to make zero return. If the stock moves ore that than percentage, this strategy will lose money.

More details at the [butterfly wiki entry](http://www.wikiwand.com/en/Butterfly_(options))

---------------------------------------------------------------------------------

Installation
============

Initial Setup
--------

1. Install gradle (http://www.gradle.org/installation)
2. Install node (https://nodejs.org/download/)
3. Install Java (https://java.com/en/download/)
4. ``export JAVA_HOME=<java install directory>``
5. ``git clone https://github.com/akshaypundle/saturn.git``
6. ``cd saturn/src/web``
7. ``npm install -g grunt-cli``
8. ``npm install``

You should now be able to edit the java and web source files. 

Eclipse setup
--------
In the ``saturn`` directory, run ``gradle eclipse``. This will generate a project that you can import into eclipse. 

Web build
------

* ``grunt`` in the ``saturn/src/web`` directory for building.
* ``grunt devWatch`` in the ``saturn/src/web`` directory for running the build in "dev" mode. This will launch a http server at 8080 (so you can test out changes by going to http://localhost:8080/). It will also watch files and rebuild the ones that change.

Java build
------
In the ``saturn`` directory, run ``gradle build``

Deploying to ec2
---------
in the ``saturn`` directory, run ``scripts/deploy.sh``. You will need the private key in your ``~/.ssh`` folder.


