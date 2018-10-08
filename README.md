# Build Tool Helper

This is Java library to provide following functionality.

* Install Maven Wrapper to Maven Project.
* Execute Meven command in dependent Java process.


## How to Use

```java
import io.sitoolkit.util.buidtoolhelper.MavenProject;

public class Main {

  public static void main(String[] args) {
    MavenProject
      .load("/path/to/your/maven/project")
      .mvnw("clean", "package")
      .execute();
  }
}
```

Above program is same with followig commands.

```
cd /path/to/your/maven/project
mvnw clean package
```

* "/path/to/your/maven/project" is the directory which pom.xml exists
* Maven wrapper will be installed automatically if it isn't. 
