# Using Maven #

If you want to use the Lanterna library through [Apache Maven](http://maven.apache.org), it's very easy to do. Just put this dependency in your `pom.xml`:
```
    <dependencies>
    ...
        <dependency>
            <groupId>com.googlecode.lanterna</groupId>
            <artifactId>lanterna</artifactId>
            <version>2.1.7</version>
        </dependency>
    ...
    </dependencies>
```

Adjust the version number as required (I'm probably not going to remember to update this page every time I make a release). Since the Sonatype OSS repository is synchronized with Maven Central, you don't need to add any extra repository definitions to your project or your maven settings.

## Using snapshot releases ##
If you want to try a snapshot release of Lanterna through Maven, you'll need to add the Sonatype OSS snapshot repository to you project `pom.xml` or your global maven settings. I have not tested this, but according to [this](https://github.com/thucydides-webtests/thucydides/wiki/Getting-Started) site, you can add it to your user's `settings.xml` (or, probably easier, to your project `pom.xml`):
```
           <repositories>
           ...
                <repository>
                    <id>sonatype-oss-snapshots</id>
                    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
                </repository>
           ...
           </repositories>
```