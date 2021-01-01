package ca.retrylife.gradle.sensibledefaults

import org.gradle.api.provider.Property

class SensiblePluginJavadocSettings {

    // Enable / Disable all of javadoc
    boolean enabled = true;

    // Override the documentation name
    String name = null;

    // List of external docs to reference
    String[] references = [
        'http://docs.spring.io/spring/docs/4.3.x/javadoc-api/', 
        'http://docs.oracle.com/javase/8/docs/api/', 
        'http://docs.spring.io/spring-ws/docs/2.3.0.RELEASE/api/', 
        'http://docs.spring.io/spring-security/site/docs/4.0.4.RELEASE/apidocs/'
    ];

    // CLI args
    String[] options = [
        'Xdoclint:none', 
        '-quiet'
    ]
}

class SensiblePluginSettings {
    SensiblePluginJavadocSettings javadoc = new SensiblePluginJavadocSettings();
    boolean srcJar =true;
    // Property<String> ghRepo ;
}