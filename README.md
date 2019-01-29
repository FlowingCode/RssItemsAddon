[![Build Status](https://jenkins.flowingcode.com/job/IronIcons-addon/badge/icon)](https://jenkins.flowingcode.com/job/IronIcons-addon)

# RSS Items Addon

Vaadin 10+ wrapper for https://github.com/TherapyChat/rss-items

## Online demo

[Online demo here](http://addonsv10.flowingcode.com/rss-items)

## Building and running demo

- git clone repository
- mvn clean install jetty:run

To see the demo, navigate to http://localhost:8080/

## Release notes

- **Version 1.0.0** Initial Version

## Issue tracking

The issues for this add-on are tracked on its github.com page. All bug reports and feature requests are appreciated. 

## Contributions

Contributions are welcome, but there are no guarantees that they are accepted as such. Process for contributing is the following:

- Fork this project
- Create an issue to this project about the contribution (bug or feature) if there is no such issue about it already. Try to keep the scope minimal.
- Develop and test the fix or functionality carefully. Only include minimum amount of code needed to fix the issue.
- Refer to the fixed issue in commit
- Send a pull request for the original project
- Comment on the original issue that you have implemented a fix for it

## License & Author

Add-on is distributed under Apache License 2.0. For license terms, see LICENSE.txt.

RssItems Addon is written by Flowing Code S.A.


# Developer Guide

## Using the component

- Call constructor
```
RssItems items = new RssItems("https://www.flowingcode.com/feeds/posts/default?alt=rss");
```
- Advanced constructor
```
RssItems items = new RssItems("https://www.flowingcode.com/feeds/posts/default?alt=rss",6,100,100);
```
