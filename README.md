Encryption
============

Encrypt and decrypt values for any Android API version.


Usage

* Initialization:

```java
public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        EncryptionManager.initialize(this);
    }
}
```

* Usage:

```kotlin
val encryptedValue = EncryptionManager.getInstance().encrypt("foo")
val originValue = EncryptionManager.getInstance().decrypt(encryptedValue)

```


Download
--------

```groovy
dependencies {
  implementation 'com.avivvegh.encryption:encryption:1.0.2'
}
```



Library projects
--------------------

To use Encryption in a library, add the plugin to your `buildscript`:

```groovy
buildscript {
  repositories {
    jcenter()
  }
}
```



License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

