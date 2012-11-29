# mt2tumblr
is convert tool for blog data from MT4 to tumblr.  
This tool based on Akka for posting.
[Test site](http://mt2test.tumblr.com) is updated contents useing this (Japanese).

## Build & Usage
sbt launcher version is 0.12.1  

clone  
sbt compile  
edit config/app.scala file 
 
    val datapath = "MT EXPORT FILE PATH"
    val cons_key =  "YOUR CONSUMER KYE"
    val cons_secret = "YOUR CONSUMER SECRET"
    val username = "YOUR ACCOUNT NAME"
    val password = "PASSWORD"
    val hostname = "BLOG HOSTNAME"

sbt run

## Notes

this tool enables MT4 export file which format as blelow
 
    AUTHOR: author
    TITLE: blog title
    BASENAME: post
    STATUS: Publish
    ALLOW COMMENTS: 1
    CONVERT BREAKS: richtext
    ALLOW PINGS: 1
    PRIMARY CATEGORY: blog
    CATEGORY: blog
    DATE: MM/DD/YYYY 00:00:00 AM
    -----
    BODY:
    blog contents
    -----
    EXTENDED BODY:
    
    -----
    EXCERPT:
    
    -----
    KEYWORDS:
    
    -----
