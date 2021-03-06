package ca.retrylife.gradle.sensibledefaults

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.Test

import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.bundling.Jar

import org.gradle.api.publish.maven.MavenPublication

public class GradleSensibleDefaultsPlugin implements Plugin<Project> {

    public void apply(Project project) {
        // Enable the Java plugin for the user
        project.getPluginManager().apply(org.gradle.api.plugins.JavaPlugin.class)
        project.getPluginManager().apply(org.gradle.api.publish.maven.plugins.MavenPublishPlugin.class)

        // Set up the settings block
        def settings = project.extensions.create('sensible', SensiblePluginSettings)

        // Javadoc task
        if (project.sensible.javadoc.enabled) {
            // Javadoc generation task
            project.task('sensibleJavadoc', type: Javadoc) {
                source = project.sourceSets.main.allJava
                classpath = project.sourceSets.main.runtimeClasspath

                // Set the JavaDoc webpage title
                title = "$project.name $project.version API"

                // These fix a JavaDoc bug
                options.addBooleanOption('-no-module-directories', true)
                options.author true

                // External references and options
                options.links = project.sensible.javadoc.references

                project.sensible.javadoc.options.each {
                    options.addStringOption it
                }
            }

            // Javadoc JAR
            project.task('sensibleJavadocJar', type: Jar) {
                from project.tasks.sensibleJavadoc
                classifier = 'javadoc'
            }

            project.tasks.assemble.dependsOn project.tasks.sensibleJavadocJar
        }

        // SRCJAR
        if (project.sensible.srcJar) {
            project.task('sensibleSrcJar', type: Jar) {
                from project.sourceSets.main.allJava
                classifier = 'sources'
            }

            project.tasks.assemble.dependsOn project.tasks.sensibleSrcJar
        }

        project.jar {
            manifest {
                attributes(
                    'Build-Timestamp': new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()),
                    'Created-By'     : 'Gradle',
                    'Build-Jdk'      : "${System.properties['java.version']} (${System.properties['java.vendor']} ${System.properties['java.vm.version']})",
                    'Build-OS'       : "${System.properties['os.name']} ${System.properties['os.arch']} ${System.properties['os.version']}"
                )
            }
        }

        project.publishing {

            // GH deploy
            if (project.ghRepo != null) {
                println "Using ${project.ghRepo} as publish target"
                repositories {
                    maven {
                        name = 'GitHubPackages'
                        url = Project.uri("https://maven.pkg.github.com/${project.ghRepo}")
                        credentials {
                            username = project.findProperty('gpr.user') ?: System.getenv('USERNAME')
                            password = project.findProperty('gpr.key') ?: System.getenv('TOKEN')
                        }
                    }
                }
                publications {
                    gpr(MavenPublication) {
                        from project.components.java
                        if (project.sensible.srcJar) {
                            artifact project.tasks.sensibleSrcJar
                        }
                        if (project.sensible.javadoc.enabled) {
                            artifact project.tasks.sensibleJavadocJar
                        }
                    }
                }
            }else {
                println "Not using GitHub as a publish target"
                publications {
                    maven(MavenPublication) {
                        from project.components.java
                        if (project.sensible.srcJar) {
                            artifact project.tasks.sensibleSrcJar
                        }
                        if (project.sensible.javadoc.enabled) {
                            artifact project.tasks.sensibleJavadocJar
                        }
                    }
                }
            }
        }

        // Unit test outputs
        project.tasks.withType(Test) {
            testLogging {
                // set options for log level LIFECYCLE
                events TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.STANDARD_OUT
                exceptionFormat TestExceptionFormat.FULL
                showExceptions true
                showCauses true
                showStackTraces true

                // set options for log level DEBUG and INFO
                debug {
                    events TestLogEvent.STARTED,
                        TestLogEvent.FAILED,
                        TestLogEvent.PASSED,
                        TestLogEvent.SKIPPED,
                        TestLogEvent.STANDARD_ERROR,
                        TestLogEvent.STANDARD_OUT
                    exceptionFormat TestExceptionFormat.FULL
                }
                info.events = debug.events
                info.exceptionFormat = debug.exceptionFormat

                afterSuite { desc, result ->
                    if (!desc.parent) { // will match the outermost suite
                        def output = "Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped)"
                        def startItem = '|  ', endItem = '  |'
                        def repeatLength = startItem.length() + output.length() + endItem.length()
                        println('\n' + ('-' * repeatLength) + '\n' + startItem + output + endItem + '\n' + ('-' * repeatLength))
                    }
                }
            }
        }
    }

}
